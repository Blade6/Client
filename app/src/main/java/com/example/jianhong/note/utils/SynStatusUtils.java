package com.example.jianhong.note.utils;

import com.example.jianhong.note.data.model.Note;

/**
 * Created by jianhong on 2018/3/18.
 */

public class SynStatusUtils {

    public static final int NOTHING = 0;
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 4;
    public static final int N_UPDATE = 3;   // new -> update
    public static final int N_DELETE = 5;   // new -> delete
    public static final int U_DELETE = 6;   // update -> delete
    public static final int N_U_DELETE = 7; // new -> update -> delete

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
            default:
                //ignore
                break;
        }
    }

    private boolean isInsert(Note note) {
        int synStatus = note.getSynStatus();
        if (synStatus == NEW || synStatus == N_UPDATE || synStatus == N_DELETE || synStatus == N_U_DELETE)
            return true;
        else
            return false;
    }

}
