package com.example.jianhong.note.entity;

import java.util.ArrayList;

public class Keeper {
    private SnapShot state;

    private static final int MAX_CAPACITY = 30;
    private ArrayList<SnapShot> lastSnapShot;
    private ArrayList<SnapShot> nextSnapShot;

    public Keeper(SnapShot snapShot) {
        state = snapShot;
        initSnapShotList();
    }

    private void initSnapShotList() {
        lastSnapShot = new ArrayList<>(MAX_CAPACITY);
        nextSnapShot = new ArrayList<>(MAX_CAPACITY);
    }

    public SnapShot createSnapShot() {
        return state;
    }

    public void setState(SnapShot snapShot) {
        if (null == snapShot) return;
        this.state = snapShot;
    }

    public SnapShot getState() {
        return state;
    }

    public void newState(SnapShot state) {
        push(lastSnapShot);
        nextSnapShot.clear();
        this.state = state;
    }

    private void push(ArrayList<SnapShot> lastSnapShot) {
        checkMaxCapacity(lastSnapShot);
        lastSnapShot.add(createSnapShot());
    }

    private SnapShot pop(ArrayList<SnapShot> list) {
        if (list.size() > 0) {
            int lastIndex = list.size() - 1;
            SnapShot newSnapShot = list.get(lastIndex);
            list.remove(lastIndex);
            return newSnapShot;
        }
        return null;
    }

    private void checkMaxCapacity(ArrayList<SnapShot> list) {
        if (list.size() >= MAX_CAPACITY) {
            list.remove(0);
        }
    }

    public void undo() {
        SnapShot lastState = pop(lastSnapShot);
        if (null != lastState) {
            push(nextSnapShot);
            setState(lastState);
        }
    }


    public void redo() {
        SnapShot nextState = pop(nextSnapShot);
        if (null != nextState) {
            push(lastSnapShot);
            setState(nextState);
        }
    }

    public int lastSize() {
        return lastSnapShot.size();
    }

    public int nextSize() {
        return nextSnapShot.size();
    }
}
