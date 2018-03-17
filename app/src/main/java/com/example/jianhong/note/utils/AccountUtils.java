package com.example.jianhong.note.utils;

import com.example.jianhong.note.entity.User;

/**
 * 用户信息管理类
 */
public class AccountUtils {

    public static String getUserId() {
        return PreferencesUtils.getString(PreferencesUtils.USER_ID);
    }

    public static void setUserId(String str) {
        PreferencesUtils.putString(PreferencesUtils.USER_ID, str);
    }

    public static String getUserName() {
        return PreferencesUtils.getString(PreferencesUtils.USER_NAME);
    }

    public static void setUserName(String str) {
        PreferencesUtils.putString(PreferencesUtils.USER_NAME, str);
    }

    public static String getUserPwd() {
        return PreferencesUtils.getString(PreferencesUtils.USER_PWD);
    }

    public static void setUserPwd(String str) {
        PreferencesUtils.putString(PreferencesUtils.USER_PWD, str);
    }

    public static String getUserHeadUrl(){
        return PreferencesUtils.getString(PreferencesUtils.USER_HEAD_URL);
    }

    public static void setUserHeadUrl(String str) {
        PreferencesUtils.putString(PreferencesUtils.USER_HEAD_URL, str);
    }

    public static void login() {
        PreferencesUtils.putBoolean(PreferencesUtils.LOGIN_STATE, true);
    }

    public static void logout() {
        PreferencesUtils.putBoolean(PreferencesUtils.LOGIN_STATE, false);
    }

    public static boolean isLogin() {
        return PreferencesUtils.getBoolean(PreferencesUtils.LOGIN_STATE) == true;
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
