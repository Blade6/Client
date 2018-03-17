package com.example.jianhong.note.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

/**
 * Created by jianhong on 2018/3/12.
 */

public class CommonUtils {

    public static void wordCount(String str, int[] res) {
        if (res.length < 3) {
            throw new IllegalStateException("the arg {int[] res} length must >= 3");
        }

//        计算 字数 = 英文单词 + 中文字
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
//                    连成一个单词
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

    public static void feedback(Context mContext) {
        // 必须明确使用mailto前缀来修饰邮件地址
        Uri uri = Uri.parse("mailto:hjh<893426994@qq.com>");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "PureNote用户反馈" + " Version:" + getVersionName(mContext));
        // 主题
        intent.putExtra(Intent.EXTRA_TEXT, "Manufacturer:" + Build.MANUFACTURER +
                " - Device name: " + Build.MODEL + " - SDK Version: " + Build.VERSION.SDK_INT + "  "); // 正文
        mContext.startActivity(Intent.createChooser(intent, "Select email client"));
    }

}
