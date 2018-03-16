package com.example.jianhong.note.utils;

import android.content.Context;

import com.example.jianhong.note.R;
import com.example.jianhong.note.litepreferences.rawmaterial.BaseLitePrefs;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class PrefrencesUtils extends BaseLitePrefs {

    public static final String DATA = "note_pref";

    public static final String LOGIN_STATE = "login_state";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_PWD = "user_pwd";
    public static final String USER_HEAD_URL = "user_head_url";

    public static final String VERSION_CODE = "version_code";

    public static final String NOTEBOOK_ID = "notebook_id"; // 当前选中文件夹，默认为 简记
    public static final String NOTEBOOK_NAME = "notebook_name"; // 当前文件夹名字
    public static final String JIAN_NUM = "jian_num"; // 简记文件夹的笔记数目

    /** 用户自定义数据 **/
    public static final String NOTE_MAX_LENGTH_RATIO = "note_max_length_key"; // 笔记高度
    public static final String ONE_COLUMN = "one_column"; // 是否单列显示
    public static final String CREATE_ORDER = "create_order"; // 是否按照笔记创建时间升序显示
    public static final String QUICK_SAVE_LOCATION = "quick_save_location"; // 摘录默认保存文件夹

    public static void initFromXml(Context context) {
        try {
            initFromXml(context, R.xml.prefs);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

}
