package com.example.jianhong.note.utils;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.example.jianhong.note.R;
import com.example.jianhong.note.data.db.NoteDB;
import com.example.jianhong.note.data.model.Note;
import com.example.jianhong.note.data.model.NoteBook;
import com.example.jianhong.note.data.provider.NoteProvider;

import java.util.List;

/**
 * Created by jianhong on 2018/3/12.
 */

public class CommonUtils {

    public static void wordCount(String str, int[] res) {
        if (res.length < 3) {
            throw new IllegalStateException("the arg {int[] res} length must >= 3");
        }

        // 计算 字数 = 英文单词 + 中文字
        boolean finding = false;//是否正在寻找单词结尾
        int engCnt = 0;
        int gbkCnt = 0;
        int spaceCnt = 0;//空白字符
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)
                    || 'a' <= (int) c && (int) c <= 'z'
                    || 'A' <= (int) c && (int) c <= 'Z') {
                if (finding) {
                    // 连成一个单词
                } else {
                    finding = true;
                }
            } else if (Character.isSpaceChar(c)) {
                if (finding) {
                    finding = false;
                    engCnt++;
                }
                spaceCnt++;
            } else if (0x4e00 <= (int) c && (int) c <= 0x9fa5) {
                if (finding) {
                    finding = false;
                    engCnt++;
                }
                gbkCnt++;
            } else {
                if (finding) {
                    finding = false;
                    engCnt++;
                }
            }
        }
        //最后一个
        if (finding) {
            engCnt++;
        }

        res[2] = str.length();
        res[1] = res[2] - spaceCnt;
        res[0] = engCnt + gbkCnt;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 闪电摘录
     */
    public static String extractNote(String str, int groupId, Context context) {
        String groupName;
        Cursor cursor = context.getContentResolver().query(
                ContentUris.withAppendedId(NoteProvider.NOTEBOOK_URI, groupId),
                NoteProvider.NOTEBOOK_PROJECTION, null, null, null);

        NoteBook noteBook = null;
        if (null != cursor && cursor.getCount() > 0) {
            cursor.moveToFirst();
            noteBook = NoteDB.initNoteBook(cursor);
        }

        if (noteBook != null) {
            groupName = noteBook.getName();
            extractNoteToDB(context, str, groupId);
        } else {
            groupName = context.getResources().getString(R.string.default_notebook);
            extractNoteToDB(context, str, 0);
        }
        return groupName;
    }

    /**
     * 闪电摘录，真实存入db与更新笔记本数量
     */
    public static void extractNoteToDB(Context mContext, String str, int groupId) {
        Note note = new Note();
        note.setContent(str);
        note.setCreateTime(TimeUtils.getCurrentTimeInLong());
        note.setEditTime(TimeUtils.getCurrentTimeInLong());
        note.setSynStatus(SynStatusUtils.NEW);
        note.setNoteBookId(groupId);

        ProviderUtils.insertNote(mContext, note);

        NoteBookUtils.updateNoteBook(mContext, groupId, +1);
    }

}
