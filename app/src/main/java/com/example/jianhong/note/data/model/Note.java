package com.example.jianhong.note.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.utils.SynStatusUtils;

/**
 * Created by jianhong on 2018/3/12.
 */

public class Note implements Parcelable {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    private int synStatus = SynStatusUtils.NOTHING; // 同步状态，确定执行同步操作时是否需要提交到服务器

    private int id = 0; // note的本地编号
    private String content = ""; // note的内容
    private long create_time; // 创建时间
    private long upd_time; // 最后编辑时间
    private int noteBookId = 0;//数据表中笔记本的id号[本地使用]，为0时使用默认笔记本 简记

    private long guid; // 服务器创建的id，唯一确定一条note
    private long bookGuid; // 笔记所属笔记本在服务器的唯一id

    private int deleted = FALSE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSynStatus_inside(int synStatus) {
        this.synStatus = synStatus;
    }

    public void setSynStatus(int synStatus) {
        SynStatusUtils.setStatus(this, synStatus);
    }

    public int getSynStatus() {
        return synStatus;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCreateTime(Long create_time) {
        this.create_time = create_time;
    }

    public long getCreateTime() {
        return create_time;
    }

    public void setUpdTime(Long upd_time) {
        this.upd_time = upd_time;
    }

    public long getUpdTime() {
        return upd_time;
    }

    public void setNoteBookId(int noteBookId) {
        this.noteBookId = noteBookId;
    }

    public int getNoteBookId() {
        return noteBookId;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }

    public Long getBookGuid() {
        return bookGuid;
    }

    public void setBookGuid(Long bookGuid) {
        this.bookGuid = bookGuid;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel parcel) {
            Note note = new Note();
            note.id = parcel.readInt();
            note.synStatus = parcel.readInt();
            note.content = parcel.readString();
            note.create_time = parcel.readLong();
            note.upd_time = parcel.readLong();
            note.noteBookId = parcel.readInt();
            note.deleted = parcel.readInt();
            note.guid = parcel.readLong();
            note.bookGuid = parcel.readLong();
            return note;
        }

        @Override
        public Note[] newArray(int i) {
            return new Note[i];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(synStatus);
        parcel.writeString(content);
        parcel.writeLong(create_time);
        parcel.writeLong(upd_time);
        parcel.writeInt(noteBookId);
        parcel.writeInt(deleted);
        parcel.writeLong(guid);
        parcel.writeLong(bookGuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ContentValues toContentValues() {
        ContentValues values = toInsertContentValues();
        values.put(NoteDB.ID, id);
        return values;
    }

    public ContentValues toInsertContentValues() {
        ContentValues values = new ContentValues();

        values.put(NoteDB.SYN_STATUS, synStatus);
        values.put(NoteDB.CONTENT, content);
        values.put(NoteDB.CREATE_TIME, create_time);
        values.put(NoteDB.UPD_TIME, upd_time);
        values.put(NoteDB.NOTEBOOK_ID, noteBookId);
        values.put(NoteDB.DELETED, deleted);
        values.put(NoteDB.GUID, guid);
        values.put(NoteDB.BOOK_GUID, bookGuid);

        return values;
    }

    @Override
    public String toString() {
        String toString = "note:[" + "Id->" + getId()
                + " syn_status->" + getSynStatus()
                + " content->" + getContent()
                + " create_time->" + getCreateTime()
                + " upd_time->" + getUpdTime()
                + " book_id->" + getNoteBookId()
                + " delete->" + getDeleted()
                + " guid->" + getGuid()
                + " bookguid->" + getBookGuid()
                + "]/n";
        return toString;
    }
}
