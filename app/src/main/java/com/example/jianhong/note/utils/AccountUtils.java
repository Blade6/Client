package com.example.jianhong.note.utils;

import android.content.Context;
import android.gesture.Prediction;

import com.example.jianhong.note.entity.User;

/**
 * 用户信息管理类
 */
public class AccountUtils {

    public static String getUserId() {
        return PrefrencesUtils.getString(PrefrencesUtils.USER_ID);
    }

    public static void setUserId(String str) {
        PrefrencesUtils.putString(PrefrencesUtils.USER_ID, str);
    }

    public static String getUserName() {
        return PrefrencesUtils.getString(PrefrencesUtils.USER_NAME);
    }

    public static void setUserName(String str) {
        PrefrencesUtils.putString(PrefrencesUtils.USER_NAME, str);
    }

    public static String getUserPwd() {
        return PrefrencesUtils.getString(PrefrencesUtils.USER_PWD);
    }

    public static void setUserPwd(String str) {
        PrefrencesUtils.putString(PrefrencesUtils.USER_PWD, str);
    }

    public static String getUserHeadUrl(){
        return PrefrencesUtils.getString(PrefrencesUtils.USER_HEAD_URL);
    }

    public static void setUserHeadUrl(String str) {
        PrefrencesUtils.putString(PrefrencesUtils.USER_HEAD_URL, str);
    }

    public static void login() {
        PrefrencesUtils.putBoolean(PrefrencesUtils.LOGIN_STATE, true);
    }

    public static void logout() {
        PrefrencesUtils.putBoolean(PrefrencesUtils.LOGIN_STATE, false);
    }

    public static boolean isLogin() {
        return PrefrencesUtils.getBoolean(PrefrencesUtils.LOGIN_STATE) == true;
    }

    public static void saveUserInfos(User user) {
        setUserId(user.getUserId());
        setUserName(user.getUsername());
        setUserPwd(user.getPassword());
        setUserHeadUrl(user.getHeadUrl());
        login();
    }

    public static void clearAllInfos(){
        setUserId("");
        setUserName("");
        setUserPwd("");
        setUserHeadUrl("");
        logout();
    }
}
