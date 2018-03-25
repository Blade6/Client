package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.net.NetBroker;
import com.example.jianhong.note.net.BooksData;
import com.example.jianhong.note.net.NetNote;
import com.example.jianhong.note.net.NetNoteBook;
import com.example.jianhong.note.net.NotesData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhong on 2018/3/18.
 */

public class SynStatusUtils {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final int FORGET = -1;
    public static final int NOTHING = 0;
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 4;
    public static final int N_UPDATE = 3;   // new -> update
    public static final int N_DELETE = 5;   // new -> delete
    public static final int U_DELETE = 6;   // update -> delete
    public static final int N_U_DELETE = 7; // new -> update -> delete

    // 增删改查时调用
    public static void setSyn(Context context) {
        long syn = ProviderUtils.getSyn(context, AccountUtils.getUserId());
        int syn_status = (int) syn % 10;
        LogUtils.d("MainActivity", "SYN:" + syn);
        LogUtils.d("MainActivity", "SYN_STATUS:" + syn_status);
        if (syn_status == 1) {
            // ignore
        } else {
            long cur = syn / 10;
            ProviderUtils.updateSyn(context, AccountUtils.getUserId(), 1, cur+1);
            LogUtils.d("MainActivity", "After update:" + ProviderUtils.getSyn(context, AccountUtils.getUserId()));
        }
    }

    public static void setStatus(Note note, int add) {
        int cur = note.getSynStatus();
        switch (cur) {
            case NOTHING:
                note.setSynStatus_inside(add);
                break;
            case NEW:
                note.setSynStatus_inside(NEW + add);
                break;
            case UPDATE:
                note.setSynStatus_inside(UPDATE + add);
                break;
            case N_UPDATE:
                if (add == DELETE) note.setSynStatus_inside(N_UPDATE + add);
                break;
            default:
                //ignore
                break;
        }
    }

    public static void setStatus(NoteBook noteBook, int add) {
        int cur = noteBook.getSynStatus();
        switch (cur) {
            case NOTHING:
                noteBook.setSynStatus_inside(add);
                break;
            case NEW:
                noteBook.setSynStatus_inside(NEW + add);
                break;
            case UPDATE:
                noteBook.setSynStatus_inside(UPDATE + add);
                break;
            case N_UPDATE:
                if (add == DELETE) noteBook.setSynStatus_inside(N_UPDATE + add);
                break;
            default:
                //ignore
                break;
        }
    }

    public static BooksData booksToServer(List<NoteBook> synNoteBooks) {
        List<NetNoteBook> insert = new ArrayList<>();
        List<NetNoteBook> update = new ArrayList<>();
        for (NoteBook noteBook : synNoteBooks) {
            if (isInsert(noteBook)) {
                insert.add(NetBroker.toServer(noteBook));
            } else {
                update.add(NetBroker.toServer(noteBook));
            }
        }
        BooksData booksData = new BooksData(insert, update);
        return booksData;
    }

    public static NotesData notesToServer(List<Note> synNotes) {
        List<NetNote> insert = new ArrayList<>();
        List<NetNote> update = new ArrayList<>();
        for (Note note : synNotes) {
            if (isInsert(note)) {
                insert.add(NetBroker.toServer(note));
            } else {
                update.add(NetBroker.toServer(note));
            }
        }
        NotesData notesData = new NotesData(insert, update);
        return notesData;
    }

    private static boolean isInsert(NoteBook noteBook) {
        return isInsert(noteBook.getSynStatus());
    }

    private static boolean isInsert(Note note) {
        return isInsert(note.getSynStatus());
    }

    private static boolean isInsert(int synStatus) {
        if (synStatus == NEW || synStatus == N_UPDATE)
            return true;
        else
            return false;
    }

}
