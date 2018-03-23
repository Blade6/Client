package com.example.jianhong.note.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhong on 2018/3/19.
 */

public class NotesData {

    private List<NetNote> insertNotes;
    private List<NetNote> updateNotes;

    public NotesData(List<NetNote> insertNotes, List<NetNote> updateNotes) {
        this.insertNotes = insertNotes;
        this.updateNotes = updateNotes;
    }

    public List<NetNote> getInsertNotes() {
        return insertNotes;
    }

    public void setInsertNotes(List<NetNote> insertNotes) {
        this.insertNotes = insertNotes;
    }

    public List<NetNote> getUpdateNotes() {
        return updateNotes;
    }

    public void setUpdateNotes(List<NetNote> updateNotes) {
        this.updateNotes = updateNotes;
    }

    public List<Integer> getAndroidId() {
        List<Integer> list = new ArrayList<>();
        for (NetNote nn : insertNotes) {
            list.add(nn.getAndroidId());
        }
        return list;
    }

}
