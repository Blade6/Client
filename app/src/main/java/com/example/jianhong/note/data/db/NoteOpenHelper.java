package com.example.jianhong.note.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER =
            "create table table_user  ("
                    + "user_id integer primary key,"
                    + "syn_status integer,"
                    + "syn_uid integer"
                    + ")";

    public static final String CREATE_TABLE =
            "create table " + NoteDB.TABLE_NOTE + " ("
                    + "id integer primary key autoincrement,"
                    + "syn_status integer,"
                    + "content text,"
                    + "create_time integer,"
                    + "edit_time integer,"
                    + "notebook_id integer,"
                    + "deleted integer,"
                    + "guid text,"
                    + "book_guid text,"
                    + "user_id long"
                    + ")";

    public static final String CREATE_TABLE_NOTEBOOK =
            "create table " + NoteDB.TABLE_NOTEBOOK + " ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "syn_status integer,"
                    + "num integer,"
                    + "deleted integer,"
                    + "notebook_guid text,"
                    + "user_id long"
                    + ")";

    public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                           int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_NOTEBOOK);
    }

    /**
     * 升级数据库
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
