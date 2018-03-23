package com.example.jianhong.note.net;

import com.example.jianhong.note.utils.SynStatusUtils;

/**
 * Created by jianhong on 2018/3/19.
 */

public class NetNoteBook {

    public static final long LOCAL_NEW = 0; // 当serverId为此值时，表示NoteBook新建，还没有Guid

    private int androidId = 0;
    private long serverId = 0L;
    private String name = "";
    private int notesNum = 0;
    private int deleted = SynStatusUtils.FALSE;

    public int getAndroidId() {
        return androidId;
    }

    public void setAndroidId(int androidId) {
        this.androidId = androidId;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNotesNum() {
        return notesNum;
    }

    public void setNotesNum(int notesNum) {
        this.notesNum = notesNum;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }


}
