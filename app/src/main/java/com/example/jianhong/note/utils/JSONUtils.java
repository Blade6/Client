package com.example.jianhong.note.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.jianhong.note.entity.Response;

public class JSONUtils {

	public static Response handleResponse(String response) {
        LogUtils.d("JSON", response);
        Response res = new Response();
		try {
			JSONObject jsonObject = new JSONObject(response.toString());
			String returnCode = jsonObject.getString("returnCode");
			if (returnCode.equals("succ")) {
                res.setReturnCode(true);
				JSONObject data = jsonObject.getJSONObject("data");
                res.setData(data);
			}
			else {
                res.setReturnCode(false);
			}
		} catch (JSONException e) {
            res.setReturnCode(false);
		} finally {
			return res;
		}
	}

}
