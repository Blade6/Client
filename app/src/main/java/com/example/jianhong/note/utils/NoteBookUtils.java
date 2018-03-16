package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.NoteBook;

public class NoteBookUtils {

    private static final String TAG = NoteBookUtils.class.getSimpleName();

    public static void updateNoteBook(Context mContext, int id, int diff) {
        NoteDB db = NoteDB.getInstance(mContext);
        NoteBook noteBook = db.getNoteBookById(id);
        int num = noteBook.getNotesNum();
        noteBook.setNotesNum(num + diff);
        if (id == 0) {
            PrefrencesUtils.putInt(PrefrencesUtils.JIAN_NUM, num+diff);
        }
        ProviderUtils.updateNoteBook(mContext, noteBook);
    }
}
