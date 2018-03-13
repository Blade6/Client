package com.example.jianhong.note.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.jianhong.note.db.model.Note;
import com.example.jianhong.note.db.model.NoteBook;
import com.example.jianhong.note.db.provider.NoteProvider;


public class ProviderUtils {
    public static int updateNote(Context context, Note note) {
        ContentValues values = note.toContentValues();
        return context.getContentResolver().update(ContentUris.withAppendedId(NoteProvider
                .BASE_URI, note.getId()), values, null, null);
    }

    public static Uri insertNote(Context context, Note note) {
        ContentValues values = note.toContentValues();
        return context.getContentResolver().insert(NoteProvider.BASE_URI, values);
    }

    public static int updateNoteBook(Context context, NoteBook noteBook) {
        ContentValues values = noteBook.toContentValues();
        return context.getContentResolver().update(ContentUris.withAppendedId(NoteProvider
                .NOTEBOOK_URI, noteBook.getId()), values, null, null);
    }

    public static Uri insertNoteBook(Context context, NoteBook noteBook) {
        ContentValues values = noteBook.toContentValues();
        return context.getContentResolver().insert(NoteProvider.NOTEBOOK_URI, values);
    }

    public static int deleteNoteBook(Context context, NoteBook noteBook) {
        return context.getContentResolver().delete(ContentUris.withAppendedId(NoteProvider
                .NOTEBOOK_URI, noteBook.getId()), null, null);
    }

}
