package com.example.jianhong.note.utils;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.jianhong.note.entity.Response;
import com.google.gson.Gson;

public class JSONUtils {

    private static final String TAG = JSONUtils.class.getSimpleName();

    private static final String RETURN_CODE = "returnCode";
    private static final String SUCC = "succ";
    private static final String FAIL = "fail";

    private static final String INSERT = "insert";
    private static final String UPDATE = "update";

    private static final String ANDROID_ID = "android_id";
    private static final String SERVER_ID = "server_id";
    private static final String NAME = "name";
    private static final String NOTES_NUM = "notes_num";
    private static final String DELETED = "deleted";

	public static Response handleResponse(String response) {
        LogUtils.d(TAG, response);
        Response res = new Response();
		try {
			JSONObject jsonObject = new JSONObject(response.toString());
			String returnCode = jsonObject.getString("returnCode");
			if (returnCode.equals("succ")) {
                res.setReturnCode(true);

                int info = jsonObject.getInt("info");
                if (info == 0) {
                    // ignore
                } else if (info == 4) {
                    Long syn_uid = jsonObject.getLong("data");
                    res.setSynUid(syn_uid);
                } else {
                    JSONObject data = jsonObject.getJSONObject("data");
                    res.setData(data);
                }
			}
			else {
                res.setReturnCode(false);
			}
		} catch (JSONException e) {
            LogUtils.d(TAG, "handleResponse JSONException");
		} finally {
			return res;
		}
	}

    //将javabean转换成JSON字符串
    public static String converJavaBeanToJson(Object obj){
        if(obj == null){
            return "";
        }
        Gson gson = new Gson();
        String beanstr = gson.toJson(obj);
        if(!TextUtils.isEmpty(beanstr)){
            return beanstr;
        }
        return "";
    }

}
