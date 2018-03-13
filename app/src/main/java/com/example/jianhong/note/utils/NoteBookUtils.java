package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.db.model.NoteDB;
import com.example.jianhong.note.db.model.NoteBook;

public class NoteBookUtils {

    public static void updateNoteBook(Context mContext, int id, int diff) {
        NoteDB db = NoteDB.getInstance(mContext);
        NoteBook noteBook = db.getNoteBookById(id);
        int num = noteBook.getNotesNum();
        noteBook.setNotesNum(num + diff);
        ProviderUtils.updateNoteBook(mContext, noteBook);
    }
}
