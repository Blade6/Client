package com.example.jianhong.note.db.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.jianhong.note.db.NoteOpenHelper;

public class NoteDB {
    public static final String TAG = "NoteDB";

    public static final String DB_NAME = "db_note";

    public static final String TABLE_NOTE = "table_note";
    public static final String TABLE_NOTEBOOK = "table_notebook";

    // common
    public static final String ID = "id";
    public static final String SYN_STATUS = "syn_status";
    public static final String DELETED = "deleted";

    // table_note
    public static final String TIME = "time";
    public static final String CONTENT = "content";
    public static final String CREATE_TIME = "create_time";
    public static final String UPD_TIME = "upd_time";

    public static final String GUID = "guid";
    public static final String BOOK_GUID = "book_guid";
    public static final String NOTEBOOK_ID = "notebook_id";

    // table_notebook
    public static final String NAME = "name";
    public static final String NOTEBOOK_GUID = "notebook_guid";
    public static final String NOTES_NUM = "num";
    public static final String SELECTED = "selected";

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

    /**
     *--------------------------------------table_note----------------------------------------------
     */

    /**
     * 将Note实例存入数据库
     */
    public void saveNote(Note note) {
        if (note != null) {
            ContentValues values = new ContentValues();
            values.put(SYN_STATUS, note.getSynStatus());
            values.put(CONTENT, note.getContent());
            values.put(CREATE_TIME, note.getCreateTime());
            values.put(UPD_TIME, note.getUpdTime());
            values.put(NOTEBOOK_ID, note.getNoteBookId());

            db.insert(TABLE_NOTE, null, values);
        }
    }

    /**
     * 从数据库中读取Note数据
     */
    public List<Note> loadNotes() {
        List<Note> list = new ArrayList<Note>();
        Cursor cursor = db.query(TABLE_NOTE, null, SYN_STATUS + " != ?" + " and " + DELETED + " " +
                "!= ?", new String[]{"" + Note.DELETE, "" + Note.TRUE}, null, null, "time desc");

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                //note.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
                note.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
                note.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
                note.setUpdTime(cursor.getLong(cursor.getColumnIndex(UPD_TIME)));
                note.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
                note.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
                note.setNoteBookId(cursor.getInt(cursor.getColumnIndex(NOTEBOOK_ID)));

                //note.setGuid(cursor.getString(cursor.getColumnIndex(GUID)));
                //note.setBookGuid(cursor.getString(cursor.getColumnIndex(BOOK_GUID)));

                list.add(note);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 根据主键更新Note数据
     */
    public boolean updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(TIME, note.getTime());
        values.put(CONTENT, note.getContent());
        values.put(UPD_TIME, note.getUpdTime());
        values.put(CREATE_TIME, note.getCreateTime());
        values.put(SYN_STATUS, note.getSynStatus());
        //values.put(GUID, note.getGuid());
        //values.put(BOOK_GUID, note.getBookGuid());
        values.put(DELETED, note.getDeleted());
        values.put(NOTEBOOK_ID, note.getNoteBookId());

        return db.update(TABLE_NOTE, values, "id = ?", new String[]{"" + note.getId()}) == 1;
    }

    /**
     *----------------------------------table_notebook----------------------------------------------
     */
    public void saveNoteBook(NoteBook noteBook) {
        if (noteBook != null) {
            ContentValues values = new ContentValues();
            values.put(NAME, noteBook.getName());
            values.put(SYN_STATUS, noteBook.getSynStatus());
            values.put(NOTEBOOK_GUID, noteBook.getNotebookGuid());
            values.put(DELETED, noteBook.getDeleted());
            values.put(NOTES_NUM, noteBook.getNotesNum());
            values.put(SELECTED, noteBook.getSelected());

            db.insert(TABLE_NOTEBOOK, null, values);
        }
    }

    public boolean updateNoteBook(NoteBook noteBook) {
        ContentValues values = new ContentValues();
        values.put(NAME, noteBook.getName());
        values.put(SYN_STATUS, noteBook.getSynStatus());
        values.put(NOTEBOOK_GUID, noteBook.getNotebookGuid());
        values.put(DELETED, noteBook.getDeleted());
        values.put(NOTES_NUM, noteBook.getNotesNum());
        values.put(SELECTED, noteBook.getSelected());

        return db.update(TABLE_NOTEBOOK, values, "id = ?", new String[]{"" + noteBook.getId()})
                == 1;
    }

    public boolean deleteNoteBook(NoteBook noteBook) {
        return db.delete(TABLE_NOTEBOOK, "id = ?", new String[]{"" + noteBook.getId()}) == 1;
    }

    public List<NoteBook> loadNoteBooks() {
        List<NoteBook> list = new ArrayList<NoteBook>();
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, DELETED + " != ?", new
                String[]{"" + NoteBook.TRUE}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                NoteBook noteBook = new NoteBook();
                noteBook.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                noteBook.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                noteBook.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
                noteBook.setNotebookGuid(cursor.getLong(cursor.getColumnIndex(NOTEBOOK_GUID)));
                noteBook.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
                noteBook.setNotesNum(cursor.getInt(cursor.getColumnIndex(NOTES_NUM)));
                noteBook.setSelected(cursor.getInt(cursor.getColumnIndex(SELECTED)));

                list.add(noteBook);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public NoteBook getNoteBookById(int id) {
        Cursor cursor = db.query(TABLE_NOTEBOOK, null, "id = ?", new String[]{"" + id}, null,
                null, null);
        if (cursor.moveToFirst()) {
            NoteBook noteBook = new NoteBook();
            noteBook.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            noteBook.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            noteBook.setSynStatus(cursor.getInt(cursor.getColumnIndex(SYN_STATUS)));
            noteBook.setNotebookGuid(cursor.getLong(cursor.getColumnIndex(NOTEBOOK_GUID)));
            noteBook.setDeleted(cursor.getInt(cursor.getColumnIndex(DELETED)));
            noteBook.setNotesNum(cursor.getInt(cursor.getColumnIndex(NOTES_NUM)));
            noteBook.setSelected(cursor.getInt(cursor.getColumnIndex(SELECTED)));

            cursor.close();
            return noteBook;
        }

        cursor.close();
        return null;
    }

}
