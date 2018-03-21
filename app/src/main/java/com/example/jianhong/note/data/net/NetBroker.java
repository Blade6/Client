package com.example.jianhong.note.data.net;

import android.content.Context;

import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.entity.Response;
import com.example.jianhong.note.utils.LogUtils;
import com.example.jianhong.note.utils.PreferencesUtils;
import com.example.jianhong.note.utils.ProviderUtils;
import com.example.jianhong.note.utils.SynStatusUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jianhong on 2018/3/19.
 */

public class NetBroker {

    private static final String TAG = NetBroker.class.getSimpleName();

    public static NetNoteBook toServer(NoteBook noteBook) {
        NetNoteBook nnb = new NetNoteBook();
        nnb.setAndroidId(noteBook.getId());
        nnb.setName(noteBook.getName());
        nnb.setNotesNum(noteBook.getNotesNum());
        nnb.setDeleted(noteBook.getDeleted());

        nnb.setServerId(noteBook.getNotebookGuid());
        return nnb;
    }

    public static NetNote toServer(Note note) {
        NetNote nt = new NetNote();
        nt.setAndroidId(note.getId());
        nt.setBookId(note.getNoteBookId());
        nt.setContent(note.getContent());
        nt.setCreateTime(note.getCreateTime());
        nt.setEditTime(note.getEditTime());
        nt.setDeleted(note.getDeleted());

        nt.setServerId(note.getGuid());
        nt.setBookGuid(note.getBookGuid());
        return nt;
    }

    public static void handleDownloadResult(Context context, JSONObject jsonObject, long uid) {
        NoteDB.getInstance(context).deleteAllNoteBooks();
        NoteDB.getInstance(context).deleteAllNotes();

        HashMap<Long, Integer> booksMap = new HashMap<>();
        try {
            JSONArray books = jsonObject.getJSONArray("books");
            for (int i = 0; i < books.length(); ++i) {
                JSONObject book = books.getJSONObject(i);
                long server_id = book.getLong("id");
                String name = book.getString("name");
                int count = book.getInt("count");
                int deleted = book.getInt("deleted");

                NoteBook nb = new NoteBook();
                nb.setSynStatus_inside(SynStatusUtils.NOTHING);
                nb.setName(name);
                nb.setNotesNum(count);
                nb.setDeleted(deleted);
                nb.setNotebookGuid(server_id);
                nb.setUserId(uid);

                int android_id = NoteDB.getInstance(context).insertNoteBook(nb);
                booksMap.put(server_id, android_id);

                if (name.equals("简记")) {
                    PreferencesUtils.putInt(PreferencesUtils.JIAN_LOCAL_ID, android_id);
                    PreferencesUtils.putInt(PreferencesUtils.JIAN_NUM, count);
                }
            }
        } catch (JSONException e) {
            LogUtils.d(TAG, "handleDownloadResult JSONException in books");
        } finally {

        }

        try {
            JSONArray notes = jsonObject.getJSONArray("notes");
            for (int i = 0; i < notes.length(); ++i) {
                LogUtils.d(TAG, "i:"+i);
                JSONObject note = notes.getJSONObject(i);
                long guid = note.getLong("id");
                String content = note.getString("content");
                long create_time = note.getLong("create_time");
                long edit_time = note.getLong("edit_time");
                int deleted = note.getInt("deleted");
                long bookguid = note.getLong("notebook_id");

                Note nt = new Note();
                nt.setSynStatus_inside(SynStatusUtils.NOTHING);
                nt.setContent(content);
                nt.setCreateTime(create_time);
                nt.setEditTime(edit_time);
                nt.setDeleted(deleted);
                nt.setGuid(guid);
                nt.setBookGuid(bookguid);
                nt.setUserId(uid);
                nt.setNoteBookId(booksMap.get(bookguid));

                NoteDB.getInstance(context).insertNote(nt);
            }
        } catch (JSONException e) {
            LogUtils.d(TAG, "handleDownloadResult JSONException in notes");
        } finally {

        }
    }

    public static void handleUploadResult(Context context, JSONObject jsonObject,
            BooksData booksData, NotesData notesData) {
        HashMap<Integer, Long> booksMap = new HashMap<>();
        try {
            JSONObject books = jsonObject.getJSONObject("books");

            if (books != null) {
                List<Integer> books_id = booksData.getAndroidId();
                for (int i : books_id) {
                    Long guid = books.getLong("k" + i);
                    booksMap.put(i, guid);
                }
                handleUploadBooks(context, booksMap, booksData);
            }
        } catch (JSONException e) {
            LogUtils.d(TAG, "handleResponse JSONException in books");
        } finally {

        }

        try {
            JSONObject notes = jsonObject.getJSONObject("notes");
            if (notes != null) {
                List<Integer> notes_id = notesData.getAndroidId();
                HashMap<Integer, Long> notesMap = new HashMap<>();
                for (int i : notes_id) {
                    Long guid = notes.getLong("k" + i);
                    notesMap.put(i, guid);
                }
                handleUploadNotes(context, notesMap, booksMap, notesData);
            }
        } catch (JSONException e) {
            LogUtils.d(TAG, "handleResponse JSONException in notes");
        } finally {

        }

    }

    private static void handleUploadBooks(Context context, HashMap<Integer, Long> booksMap,
        BooksData booksData) {
        List<NetNoteBook> insertNoteBooks = booksData.getInsertNoteBooks();
        for (NetNoteBook nnb : insertNoteBooks) {
            int id = nnb.getAndroidId();
            long guid = booksMap.get(id);
            NoteDB.getInstance(context).updateNoteBook(id, guid);
        }

        List<NetNoteBook> updateNoteBooks = booksData.getUpdateNoteBooks();
        for (NetNoteBook nnb : updateNoteBooks) {
            NoteDB.getInstance(context).updateNoteBook(nnb.getAndroidId());
        }

        int cur = PreferencesUtils.getInt(PreferencesUtils.NOTEBOOK_ID);
        if (booksMap.containsKey(cur)) {
            long bookguid = booksMap.get(cur);
            PreferencesUtils.putLong(PreferencesUtils.NOTEBOOK_GUID, bookguid);
        }
    }

    private static void handleUploadNotes(Context context, HashMap<Integer, Long> notesMap,
        HashMap<Integer, Long> booksMap, NotesData notesData) {
        List<NetNote> insertNotes = notesData.getInsertNotes();
        for (NetNote nn : insertNotes) {
            int id = nn.getAndroidId();
            long guid = notesMap.get(id);
            long bookGuid;
            if (nn.getBookGuid() == 0) {
                bookGuid = booksMap.get(nn.getBookId());
            } else {
                bookGuid = nn.getBookGuid();
            }

            int count = NoteDB.getInstance(context).updateNote(id, guid, bookGuid);
            LogUtils.d(TAG, "insertNote:"+id+" "+guid+" "+bookGuid+" count:"+count);
        }

        List<NetNote> updateNotes = notesData.getUpdateNotes();
        for (NetNote nn : updateNotes) {
            NoteDB.getInstance(context).updateNote(nn.getAndroidId());
        }
    }

}
