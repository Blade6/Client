package com.example.jianhong.note.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

class DirectionScrollListenerRV extends RecyclerView.OnScrollListener {

    private final FloatingActionButton mFloatingActionButton;
    int preDy;

    DirectionScrollListenerRV(FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (dy * preDy > 0) {
            return;
        }

        mFloatingActionButton.hide(dy > 0);

        preDy = dy;
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
    }
}