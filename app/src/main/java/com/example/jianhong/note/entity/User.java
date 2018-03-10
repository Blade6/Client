package com.example.jianhong.note.entity;

import android.text.TextUtils;

import java.io.Serializable;


public class User implements Serializable{

    private String userid;
    private String username; // 用户名
    private String password; // 密码
    private String headUrl;// 头像路径

    public String getUserId() {
        if (!TextUtils.isEmpty(userid))
            return userid;
        else
            return "";
    }

    public void setUserId(String userid) { this.userid =  userid; }

    public String getUsername() {
        if (!TextUtils.isEmpty(username))
            return username;
        else
            return "";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (!TextUtils.isEmpty(password))
            return password;
        else
            return "";
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
