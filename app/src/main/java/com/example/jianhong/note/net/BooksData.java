package com.example.jianhong.note.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhong on 2018/3/19.
 */

public class BooksData {

    private List<NetNoteBook> insertNoteBooks;
    private List<NetNoteBook> updateNoteBooks;

    public BooksData(List<NetNoteBook> insert, List<NetNoteBook> update) {
        this.insertNoteBooks = insert;
        this.updateNoteBooks = update;
    }

    public List<NetNoteBook> getInsertNoteBooks() {
        return insertNoteBooks;
    }

    public void setInsertNoteBooks(List<NetNoteBook> insertNoteBooks) {
        this.insertNoteBooks = insertNoteBooks;
    }

    public List<NetNoteBook> getUpdateNoteBooks() {
        return updateNoteBooks;
    }

    public void setUpdateNoteBooks(List<NetNoteBook> updateNoteBooks) {
        this.updateNoteBooks = updateNoteBooks;
    }

    public List<Integer> getAndroidId() {
        List<Integer> list = new ArrayList<>();
        for (NetNoteBook netNoteBook : insertNoteBooks) {
            list.add(netNoteBook.getAndroidId());
        }
        return list;
    }

}
