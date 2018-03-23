package com.example.jianhong.note.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.utils.AccountUtils;
import com.example.jianhong.note.utils.SynStatusUtils;

public class NoteDB {
    public static final String TAG = "NoteDB";

    public static final String DB_NAME = "db_note";

    public static final String TABLE_NOTE = "table_note";
    public static final String TABLE_NOTEBOOK = "table_notebook";

    // common
    public static final String ID = "id";
    public static final String SYN_STATUS = "syn_status";
    public static final String DELETED = "deleted";
    public static final String USER_ID = "user_id";

    // table_note
    public static final String CONTENT = "content";
    public static final String CREATE_TIME = "create_time";
    public static final String EDIT_TIME = "edit_time";
    public static final String GUID = "guid";
    public static final String BOOK_GUID = "book_guid";
    public static final String NOTEBOOK_ID = "notebook_id";

    // table_notebook
    public static final String NAME = "name";
    public static final String NOTEBOOK_GUID = "notebook_guid";
    public static final String NOTES_NUM = "num";

    public static final int VERSION = 1; // 数据库版本

    private static NoteDB noteDB;
    private SQLiteDatabase db;

    private NoteDB(Context context) {
        NoteOpenHelper openHelper = new NoteOpenHelper(context, DB_NAME, null, VERSION);
        db = openHelper.getWritableDatabase();
    }

    /**
     * 获取NoteDB实例
     */
    public synchronized static NoteDB getInstance(Context context) {
        if (noteDB == null) {
            noteDB = new NoteDB(context);
        }
        return noteDB;
    }

    public void insertSyn(long uid, long syn_uid) {
        ContentValues values = new ContentValues();
        values.put("user_id", uid);
        values.put("syn_status", 0);
        values.put("syn_uid", syn_uid);
        db.insert("table_user", null, values);
    }

    public void updateSyn(long uid, long syn_uid) {
        ContentValues values = new ContentValues();
        values.put("user_id", uid);
        values.put("syn_status", 0);
        values.put("syn_uid", syn_uid);
        db.update("table_user", values, "user_id = ?", new String[]{"" + uid});
    }

    public long loadSyn() {
        Cursor cursor = db.query("table_user", null, USER_ID + " = ?",
                new String[]{"" + AccountUtils.getUserId()}, null, null, null);
        long syn_uid = 0L;
        if (cursor.moveToFirst()) {
            syn_uid = cursor.getLong(cursor.getColumnIndex("syn_uid"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return syn_uid;
    }

    /**
     *--------------------------------------table_note----------------------------------------------
     */

    /**
     * 将Note实例存入数据库
     */
    public void insertNote(Note note) {
        if (note != null) {
            ContentValues values = new ContentValues();
            values.put(SYN_STATUS, note.getSynStatus());
            values.put(CONTENT, note.getContent());
            values.put(CREATE_TIME, note.getCreateTime());
            values.put(EDIT_TIME, note.getEditTime());
            values.put(NOTEBOOK_ID, note.getNoteBookId());
            values.put(DELETED, note.getDeleted());
            values.put(GUID, note.getGuid());
            values.put(BOOK_GUID, note.getBookGuid());
            values.put(USER_ID, AccountUtils.getUserId());

            db.insert(TABLE_NOTE, null, values);
        }
    }

    /**
     * 从数据库中读取Note数据
     */
    public List<Note> loadNotes() {
        Cursor cursor = db.query(TABLE_NOTE, null, DELETED + " " + "!= ?" + " and " + USER_ID + " = ?",
                new String[]{"" + SynStatusUtils.TRUE, "" + AccountUtils.getUserId()}, null, null, EDIT_TIME + " desc");
        return loadRawNotes(cursor);
    }

    public List<Note> loadSynNotes() {
        Cursor cursor = db.query(TABLE_NOTE, null, SYN_STATUS + " > ?" + " and " + USER_ID + " = ?",
                new String[]{"" + SynStatusUtils.NOTHING, "" + AccountUtils.getUserId()}, null, null, EDIT_TIME + " desc");
        return loadRawNotes(cursor);
    }

    public List<Note> loadRawNote() {
        Cursor cursor = db.query(TABLE_NOTE, null, null, null, null, null, EDIT_TIME + " desc");
        return loadRawNotes(cursor);
    }

    private List<Note> loadRawNotes(Cursor cursor) {
        List<Note> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                note.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
                note.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
                note.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
                note.setEditTime(cursor.getLong(cursor.getColumnIndex(EDIT_TIME)));
                note.setNoteBookId(cursor.getInt(cursor.getColumnIndex(NOTEBOOK_ID)));
                note.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
                note.setGuid(cursor.getLong(cursor.getColumnIndex(GUID)));
                note.setBookGuid(cursor.getLong(cursor.getColumnIndex(BOOK_GUID)));
                note.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));

                list.add(note);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

//
//
//    public Note getNoteByGuid(String guid) {
//        Cursor cursor = db.query(TABLE_NOTE, null, "guid = ?", new String[]{guid}, null, null,
//                null);
//        if (cursor.moveToFirst()) {
//            Note note = new Note();
//            note.setId(cursor.getInt(cursor.getColumnIndex(ID)));
//            note.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
//            note.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
//            note.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
//            note.setEditTime(cursor.getLong(cursor.getColumnIndex(EDIT_TIME)));
//            note.setNoteBookId(cursor.getInt(cursor.getColumnIndex(NOTEBOOK_ID)));
//            note.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
//            note.setGuid(cursor.getLong(cursor.getColumnIndex(GUID)));
//            note.setBookGuid(cursor.getLong(cursor.getColumnIndex(BOOK_GUID)));
//
//            cursor.close();
//            return note;
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return null;
//    }
//
//    /**
//     * 根据主键删除Note数据
//     */
//    public boolean deleteNote(int id) {
//        return db.delete(TABLE_NOTE, "id = ?", new String[]{"" + id}) == 1;
//    }
//
    /**
     * 根据主键更新Note数据
     */
    public boolean updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, note.getSynStatus());
        values.put(CONTENT, note.getContent());
        values.put(CREATE_TIME, note.getCreateTime());
        values.put(EDIT_TIME, note.getEditTime());
        values.put(DELETED, note.getDeleted());
        values.put(NOTEBOOK_ID, note.getNoteBookId());
        values.put(GUID, note.getGuid());
        values.put(BOOK_GUID, note.getBookGuid());

        return db.update(TABLE_NOTE, values, "id = ?", new String[]{"" + note.getId()}) == 1;
    }

    public int updateNote(int id, long guid, long bookGuid) {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.NOTHING);
        values.put(GUID, guid);
        values.put(BOOK_GUID, bookGuid);

        return db.update(TABLE_NOTE, values, "id = ?", new String[]{"" + id});
    }

    public void updateNote(int id) {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.NOTHING);

        db.update(TABLE_NOTE, values, "id = ?", new String[]{"" + id});
    }

    public int deleteAllNotes() {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.FORGET);
        values.put(DELETED, SynStatusUtils.TRUE);

        return db.update(TABLE_NOTE, values, null, null);
    }

    /**
     *----------------------------------table_notebook----------------------------------------------
     */
    // 初次建立数据库时调用，使得第一个笔记本的id为0
    public int insertDefaultNoteBook(NoteBook noteBook) {
        if (noteBook != null) {
            ContentValues values = new ContentValues();
            values.put(ID, 0);// 默认笔记本为“简记”，id为0
            values.put(NAME, noteBook.getName());
            values.put(SYN_STATUS, noteBook.getSynStatus());
            values.put(NOTEBOOK_GUID, noteBook.getNotebookGuid());
            values.put(DELETED, noteBook.getDeleted());
            values.put(NOTES_NUM, noteBook.getNotesNum());
            values.put(USER_ID, AccountUtils.getUserId());

            return (int)db.insert(TABLE_NOTEBOOK, null, values);
        }
        return -1;
    }

    public int insertNoteBook(NoteBook noteBook) {
        if (noteBook != null) {
            ContentValues values = new ContentValues();
            values.put(NAME, noteBook.getName());
            values.put(SYN_STATUS, noteBook.getSynStatus());
            values.put(NOTEBOOK_GUID, noteBook.getNotebookGuid());
            values.put(DELETED, noteBook.getDeleted());
            values.put(NOTES_NUM, noteBook.getNotesNum());
            values.put(USER_ID, AccountUtils.getUserId());

            return (int)db.insert(TABLE_NOTEBOOK, null, values);
        }
        return -1;
    }

    public List<NoteBook> loadNoteBooks() {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, DELETED + " != ?" + " and " + USER_ID + " = ?",
                new String[]{"" + SynStatusUtils.TRUE, "" + AccountUtils.getUserId()}, null, null, null);
        return loadRawNoteBooks(cursor);
    }

    public List<NoteBook> loadSynNoteBooks() {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, SYN_STATUS + " > ?" + " and " + USER_ID + " = ?",
                new String[]{"" + SynStatusUtils.NOTHING, "" + AccountUtils.getUserId()}, null, null, null);
        return loadRawNoteBooks(cursor);
    }

    public List<NoteBook> loadRawNoteBook() {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, null, null, null, null, null);
        return loadRawNoteBooks(cursor);
    }

    public List<NoteBook> loadRawNoteBooks(Cursor cursor) {
        List<NoteBook> list = new ArrayList<NoteBook>();
        if (cursor.moveToFirst()) {
            do {
                NoteBook noteBook = new NoteBook();
                noteBook.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                noteBook.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                noteBook.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
                noteBook.setNotebookGuid(cursor.getLong(cursor.getColumnIndex(NOTEBOOK_GUID)));
                noteBook.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
                noteBook.setNotesNum(cursor.getInt(cursor.getColumnIndex(NOTES_NUM)));
                noteBook.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));

                list.add(noteBook);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }


    public boolean updateNoteBook(NoteBook noteBook) {
        ContentValues values = new ContentValues();
        values.put(NAME, noteBook.getName());
        values.put(SYN_STATUS, noteBook.getSynStatus());
        values.put(NOTEBOOK_GUID, noteBook.getNotebookGuid());
        values.put(DELETED, noteBook.getDeleted());
        values.put(NOTES_NUM, noteBook.getNotesNum());

        return db.update(TABLE_NOTEBOOK, values, "id = ?", new String[]{"" + noteBook.getId()})
                == 1;
    }

    public void updateNoteBook(int id, long guid) {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.NOTHING);
        values.put(NOTEBOOK_GUID, guid);
        db.update(TABLE_NOTEBOOK, values, "id = ?", new String[]{"" + id});
    }

    public void updateNoteBook(int id) {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.NOTHING);
        db.update(TABLE_NOTEBOOK, values, "id = ?", new String[]{"" + id});
    }

    public int deleteAllNoteBooks() {
        ContentValues values = new ContentValues();
        values.put(SYN_STATUS, SynStatusUtils.FORGET);
        values.put(DELETED, SynStatusUtils.TRUE);
        return db.update(TABLE_NOTEBOOK, values, "id != ?", new String[]{"0"});
    }

    public long getDefaultBookGuid() {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, NAME + " = ?" + " and " + USER_ID + " = ?",
                new String[]{"简记", "" + AccountUtils.getUserId()}, null, null, null);
        long guid = 0L;
        if (cursor.moveToFirst()) {
            guid = cursor.getInt(cursor.getColumnIndex(ID));
            return guid;
        }
        if (cursor != null) {
            cursor.close();
        }
        return guid;
    }

//
//    public boolean deleteNoteBook(NoteBook noteBook) {
//        return db.delete(TABLE_NOTEBOOK, "id = ?", new String[]{"" + noteBook.getId()}) == 1;
//    }



    public NoteBook getNoteBookById(int id) {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, ID + " = ? and " + USER_ID + " = ?",
                new String[]{"" + id, "" + AccountUtils.getUserId()}, null, null, null);
        if (cursor.moveToFirst()) {
            NoteBook noteBook = new NoteBook();
            noteBook.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            noteBook.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            noteBook.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
            noteBook.setNotebookGuid(cursor.getLong(cursor.getColumnIndex(NOTEBOOK_GUID)));
            noteBook.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
            noteBook.setNotesNum(cursor.getInt(cursor.getColumnIndex(NOTES_NUM)));

            cursor.close();
            return noteBook;
        }

        cursor.close();
        return null;
    }

    /**
     *-----------------------------------------通用函数----------------------------------------------
     * 注意：NoteProvider对id做了别名处理
     */
    public static Note initNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        note.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
        note.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
        note.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
        note.setEditTime(cursor.getLong(cursor.getColumnIndex(EDIT_TIME)));
        note.setNoteBookId(cursor.getInt(cursor.getColumnIndex(NOTEBOOK_ID)));
        note.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
        note.setGuid(cursor.getLong(cursor.getColumnIndex(GUID)));
        note.setBookGuid(cursor.getLong(cursor.getColumnIndex(BOOK_GUID)));
        return note;
    }

    public static NoteBook initNoteBook(Cursor cursor) {
        NoteBook noteBook = new NoteBook();
        noteBook.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        noteBook.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        noteBook.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
        noteBook.setNotebookGuid(cursor.getLong(cursor.getColumnIndex(NOTEBOOK_GUID)));
        noteBook.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
        noteBook.setNotesNum(cursor.getInt(cursor.getColumnIndex(NOTES_NUM)));
        return noteBook;
    }
}
