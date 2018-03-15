package com.example.jianhong.note.litepreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jianhong.note.litepreferences.model.Pref;

import java.util.Map;
import java.util.Set;

public class ActualUtil {

    private String name;
    private SharedPreferences mSharedPreferences;
    private Map<String, Pref> mMap;

    public ActualUtil(String name, Map<String, Pref> map) {
        this.name = name;
        this.mMap = map;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void putToMap(String key, Pref pref) {
        mMap.put(key, pref);
    }

    private void checkExist(Pref pref) {
        if (null == pref) {
            throw new NullPointerException("operate a pref that isn't contained in data set,maybe there are some wrong in initialization of LitePrefs");
        }
    }

    private Pref readyOperation(String key) {
        Pref pref = mMap.get(key);
        checkExist(pref);
        return pref;
    }

    public int getInt(String key) {
        Pref pref = readyOperation(key);
        if (pref.queried) {
            return pref.getCurInt();
        } else {
            pref.queried = true;
            int ans = mSharedPreferences.getInt(key, pref.getDefInt());
            pref.setValue(ans);
            return ans;
        }
    }

    public long getLong(String key) {
        Pref pref = readyOperation(key);
        if (pref.queried) {
            return pref.getCurLong();
        } else {
            pref.queried = true;
            long ans = mSharedPreferences.getLong(key, pref.getDefLong());
            pref.setValue(ans);
            return ans;
        }
    }

    public float getFloat(String key) {
        Pref pref = readyOperation(key);
        if (pref.queried) {
            return pref.getCurFloat();
        } else {
            pref.queried = true;
            float ans = mSharedPreferences.getFloat(key, pref.getDefFloat());
            pref.setValue(ans);
            return ans;
        }
    }

    public boolean getBoolean(String key) {
        Pref pref = readyOperation(key);
        if (pref.queried) {
            return pref.getCurBoolean();
        } else {
            pref.queried = true;
            boolean ans = mSharedPreferences.getBoolean(key, pref.getDefBoolean());
            pref.setValue(ans);
            return ans;
        }
    }

    public String getString(String key) {
        Pref pref = readyOperation(key);
        if (pref.queried) {
            return pref.getCurString();
        } else {
            pref.queried = true;
            String ans = mSharedPreferences.getString(key, pref.getDefString());
            pref.setValue(ans);
            return ans;
        }
    }


    public boolean putInt(String key, int value) {
        Pref pref = readyOperation(key);
        pref.queried = true;
        pref.setValue(value);
        return mSharedPreferences.edit().putInt(key, value).commit();
    }


    public boolean putLong(String key, long value) {
        Pref pref = readyOperation(key);
        pref.queried = true;
        pref.setValue(value);
        return mSharedPreferences.edit().putLong(key, value).commit();
    }


    public boolean putFloat(String key, float value) {
        Pref pref = readyOperation(key);
        pref.queried = true;
        pref.setValue(value);

        return mSharedPreferences.edit().putFloat(key, value).commit();
    }

    public boolean putBoolean(String key, boolean value) {
        Pref pref = readyOperation(key);
        pref.queried = true;
        pref.setValue(value);

        return mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean putString(String key, String value) {
        Pref pref = readyOperation(key);
        pref.queried = true;
        pref.setValue(value);

        return mSharedPreferences.edit().putString(key, value).commit();
    }

    public boolean remove(String key) {
        Pref pref = readyOperation(key);
        pref.queried = false;

        return mSharedPreferences.edit().remove(key).commit();
    }

    public boolean clear() {
        Set<String> keySet = mMap.keySet();
        for (String key : keySet) {
            Pref pref = mMap.get(key);
            pref.queried = false;
        }

        return mSharedPreferences.edit().clear().commit();
    }

    public Map<String, Pref> getPrefsMap() {
        return mMap;
    }
}
