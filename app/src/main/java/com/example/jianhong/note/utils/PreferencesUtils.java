package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.R;
import com.example.jianhong.note.litepreferences.rawmaterial.BaseLitePrefs;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class PreferencesUtils extends BaseLitePrefs {

    public static final String DATA = "note_pref";

    public static final String LOGIN_STATE = "login_state";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_PWD = "user_pwd";
    public static final String USER_HEAD_URL = "user_head_url";

    public static final String NOTEBOOK_ID = "notebook_id"; // 当前选中文件夹，默认为 简记
    public static final String NOTEBOOK_NAME = "notebook_name"; // 当前文件夹名字
    public static final String NOTEBOOK_GUID = "notebook_guid"; // 当前文件夹的guid

    public static final String JIAN_NUM = "jian_num"; // 简记文件夹的笔记数目

    /** 用户自定义数据 **/
    public static final String ONE_COLUMN = "one_column"; // 是否单列显示
    public static final String LIGHTNING_EXTRACT = "lightning_extract"; // 闪电摘录
    public static final String LIGHTNING_EXTRACT_SAVE_LOCATION = "lightning_extract_save_location"; // 摘录默认保存文件夹的ID
    public static final String LIGHTNING_EXTRACT_SAVE_NAME = "lightning_extract_save_name"; // 摘录默认保存文件夹的名字

    public static void initFromXml(Context context) {
        try {
            initFromXml(context, R.xml.prefs);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

}
