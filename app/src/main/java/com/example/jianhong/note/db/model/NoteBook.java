package com.example.jianhong.note.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jianhong on 2018/3/12.
 */

public class NoteBook implements Parcelable {

    public static final String TAG = "NoteBook";

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static final int NOTHING = 0;
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    private int synStatus = NOTHING; // 同步状态

    private int id;
    private String name;
    private long notebookGuid;
    private int notesNum = 0;
    private int selected = FALSE;
    private int deleted = FALSE;

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected == TRUE;
    }

    public int getNotesNum() {
        return notesNum;
    }

    public void setNotesNum(int notesNum) {
        this.notesNum = notesNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNotebookGuid() {
        return notebookGuid;
    }

    public void setNotebookGuid(Long notebookGuid) {
        this.notebookGuid = notebookGuid;
    }

    public int getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(int synStatus) {
        this.synStatus = synStatus;
    }

    public boolean needUpdate() {
        return synStatus == UPDATE;
    }

    public boolean needDelete() {
        return synStatus == DELETE;
    }

    public boolean needCreate() {
        return synStatus == NEW;
    }

    public boolean isDeleted() {
        return deleted == TRUE;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(synStatus);
        parcel.writeLong(notebookGuid);
        parcel.writeInt(deleted);
        parcel.writeInt(notesNum);
        parcel.writeInt(selected);
    }

    public ContentValues toContentValues() {
        ContentValues values = toInsertContentValues();
        values.put(NoteDB.ID, id);
        return values;
    }

    public ContentValues toInsertContentValues() {
        ContentValues values = new ContentValues();
        values.put(NoteDB.NAME, name);
        values.put(NoteDB.SYN_STATUS, synStatus);
        values.put(NoteDB.NOTEBOOK_GUID, notebookGuid);
        values.put(NoteDB.DELETED, deleted);
        values.put(NoteDB.NOTES_NUM, notesNum);
        values.put(NoteDB.SELECTED, selected);
        return values;
    }

    public static final Creator<NoteBook> CREATOR = new Creator<NoteBook>() {
        @Override
        public NoteBook createFromParcel(Parcel parcel) {
            NoteBook noteBook = new NoteBook();
            noteBook.id = parcel.readInt();
            noteBook.name = parcel.readString();
            noteBook.synStatus = parcel.readInt();
            noteBook.notebookGuid = parcel.readLong();
            noteBook.deleted = parcel.readInt();
            noteBook.notesNum = parcel.readInt();
            noteBook.selected = parcel.readInt();
            return noteBook;
        }

        @Override
        public NoteBook[] newArray(int i) {
            return new NoteBook[i];
        }
    };

}
