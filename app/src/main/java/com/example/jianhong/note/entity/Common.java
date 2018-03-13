package com.example.jianhong.note.entity;

/**
 * Created by jianhong on 2018/3/13.
 */

public class Common {

    private static int notebookId = 0; // 记录当前的笔记本id

    public static int getNoteBookId() {
        return notebookId;
    }

    public static void setNoteBookId(int notebookId2) {
        notebookId = notebookId2;
    }

}
