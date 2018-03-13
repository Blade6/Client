package com.example.jianhong.note.utils;

import com.example.jianhong.note.db.model.Note;

/**
 * Created by jianhong on 2018/3/12.
 */

public class CommonUtils {

    public static String twoDigit(int n) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("00");
        return format.format(n);
    }

    public static String twoDigit(String str) {
        if (str.length() == 1) {
            return "0" + str;
        }
        return str;
    }

    public static String timeStamp(Note note) {
        String tmp = "";
        String[] allInfo;
        allInfo = note.getTime().split(",");
        //不知原因的数组越界，故暂时在此进行检测
        if (allInfo.length == 3 && (Integer.parseInt(allInfo[2]) >= 1 && Integer.parseInt
                (allInfo[2]) <= 31)) {
            tmp = allInfo[0]
                    + "."
                    + (Integer.parseInt(allInfo[1]) + 1)
                    + "."
                    + Integer.parseInt(allInfo[2]);
        }
        return tmp;
    }

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

}
