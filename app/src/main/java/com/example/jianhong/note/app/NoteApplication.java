package com.example.jianhong.note.app;

import android.app.Application;
import android.content.Context;

import com.example.jianhong.note.ui.activity.MainActivity.SyncHandler;

/**
 * Created by jianhong on 2018/3/17.
 */

public class NoteApplication extends Application {

    private static Context mContext;
    private SyncHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public SyncHandler getHandler() {
        return mHandler;
    }

    public void setHandler(SyncHandler handler) {
        mHandler = handler;
    }

}
