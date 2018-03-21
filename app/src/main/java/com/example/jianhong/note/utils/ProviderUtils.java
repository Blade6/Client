package com.example.jianhong.note.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;


public class ProviderUtils {
    public static int updateNote(Context context, Note note) {
        ContentValues values = note.toContentValues();
        return context.getContentResolver().update(ContentUris.withAppendedId(NoteProvider
                .BASE_URI, note.getId()), values, null, null);
    }

    public static Uri insertNote(Context context, Note note) {
        ContentValues values = note.toContentValues();
        values.put(NoteDB.USER_ID, AccountUtils.getUserId());
        return context.getContentResolver().insert(NoteProvider.BASE_URI, values);
    }

    public static int updateNoteBook(Context context, NoteBook noteBook) {
        ContentValues values = noteBook.toContentValues();
        return context.getContentResolver().update(ContentUris.withAppendedId(NoteProvider
                .NOTEBOOK_URI, noteBook.getId()), values, null, null);
    }

    public static Uri insertNoteBook(Context context, NoteBook noteBook) {
        ContentValues values = noteBook.toContentValues();
        values.put(NoteDB.USER_ID, AccountUtils.getUserId());
        return context.getContentResolver().insert(NoteProvider.NOTEBOOK_URI, values);
    }

    public static int deleteNoteBook(Context context, NoteBook noteBook) {
        return context.getContentResolver().delete(ContentUris.withAppendedId(NoteProvider
                .NOTEBOOK_URI, noteBook.getId()), null, null);
    }

    public static int updateSyn(Context context, long uid, int syn_status, long syn_uid) {
        ContentValues values = new ContentValues();
        values.put("user_id", uid);
        values.put("syn_status", syn_status);
        values.put("syn_uid", syn_uid);
        return context.getContentResolver().update(NoteProvider
                .SYN_URI, values, null, null);
    }

    public static long getSyn(Context context, long uid) {
        ContentValues values = new ContentValues();
        values.put("user_id", uid);

        Cursor cursor = context.getContentResolver().query(NoteProvider.SYN_URI, null, null, null, null);
        int syn_status = 0;
        long syn_uid = 0L;
        if (cursor.moveToFirst()) {
            syn_status = cursor.getInt(cursor.getColumnIndex("syn_status"));
            syn_uid = cursor.getLong(cursor.getColumnIndex("syn_uid"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return syn_uid * 10 + syn_status;
    }

}
