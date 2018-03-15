package com.example.jianhong.note.litepreferences.model;

import android.content.Context;


public class StringPref extends Pref {
    private int defRes;
    private Context mContext;

    /**
     * Special Pref to support that Preference whose default String value
     * want to be expressed by a resId.
     * <p>
     * Pass the **Application Context** or **Main Activity**
     * as parameter to avoid your activity
     * cannot be recycled by GC.
     *
     * @param key     key of preference
     * @param defRes  the resource id of the default value
     * @param context Pass the **Application Context** or **Main Activity**
     * @see Pref,Pref#curValue,Pref#setValue(String)
     */
    public StringPref(String key, int defRes, Context context) {
        this.key = key;
        this.defRes = defRes;
        mContext = context;
    }

    @Override
    public String getDefString() {
        return mContext.getString(defRes);
    }
}
