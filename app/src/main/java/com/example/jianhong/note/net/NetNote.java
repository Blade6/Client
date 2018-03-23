package com.example.jianhong.note.data.net;

/**
 * Created by jianhong on 2018/3/19.
 */

public class NetNote {

    public static final long LOCAL_NEW = 0; // 当serverId为此值时，表示Note新建，还没有Guid;
    // 当bookGuid为此值时，表示其所属文件夹新建，还没有Guid

    private int androidId = 0;
    private long serverId = 0L;
    private int bookId = 0;
    private long bookGuid = 0L;
    private String content;
    private long createTime;
    private long editTime;
    private int deleted;

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

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public long getBookGuid() {
        return bookGuid;
    }

    public void setBookGuid(long bookGuid) {
        this.bookGuid = bookGuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public int isDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
