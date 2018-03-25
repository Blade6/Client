package com.example.jianhong.note.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import com.example.jianhong.note.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SystemUtils {

    private Context context;
    private  String PREF_NAME = "creativelocker.pref";
    private  final String BG_PIC_PATH ="bg_pic_path";

    public SystemUtils(Context context)
    {
        this.context=context;
    }

    public  void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public  SharedPreferences getPreferences() {
        SharedPreferences pre =context.getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    public String getString(String str)
    {
        SharedPreferences share= getPreferences();
        return share.getString(str,null);
    }

    /**
     * 保存背景皮肤图片的地址
     */
    public  void saveBgPicPath(String path)
    {
        set(BG_PIC_PATH,path);

    }

    public  String getPath() {
        return getString(BG_PIC_PATH);
    }

    public Bitmap getBitmapByPath(Activity aty, String path) {
        AssetManager am = aty.getAssets();
        Bitmap bitmap = null;
        InputStream is =null;
        try {
            is = am.open("bkgs/" + path);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
