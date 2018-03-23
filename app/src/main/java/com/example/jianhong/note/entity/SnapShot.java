package com.example.jianhong.note.entity;

public class SnapShot {
    private String content;
    private int selectionEnd;

    public SnapShot(String content, int selectionEnd) {
        this.content = content;
        this.selectionEnd = selectionEnd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    public void setSelectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
    }
}
