package com.example.jianhong.note.net;

/**
 * Created by jianhong on 2018/3/19.
 */

public class NetData {

    public long user_id;
    private BooksData booksData;
    private NotesData notesData;

    public NetData(long user_id, BooksData booksData, NotesData notesData) {
        this.user_id = user_id;
        this.booksData = booksData;
        this.notesData = notesData;
    }

    public BooksData getBooksData() {
        return booksData;
    }

    public void setBooksData(BooksData booksData) {
        this.booksData = booksData;
    }

    public NotesData getNotesData() {
        return notesData;
    }

    public void setNotesData(NotesData notesData) {
        this.notesData = notesData;
    }

}
