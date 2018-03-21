package com.example.jianhong.note.data.model;

import android.text.TextUtils;

import com.example.jianhong.note.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class User implements Serializable{

    private long userid;
    private String username; // 用户名
    private String password; // 密码
    private String headUrl;// 头像路径

    public long getUserId() {
        return userid;
    }

    public void setUserId(long userid) { this.userid =  userid; }

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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public static User dealWithData(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setUserId(jsonObject.getLong("id"));
            user.setUsername(jsonObject.getString("username"));
            user.setPassword(jsonObject.getString("password"));
            user.setHeadUrl(jsonObject.getString("pic"));

        } catch (JSONException e) {
            LogUtils.d("JSONUtils", "dealWithData");
            e.printStackTrace();
        } finally {
            return user;
        }
    }

}
