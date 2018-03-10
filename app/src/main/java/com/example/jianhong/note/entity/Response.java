package com.example.jianhong.note.entity;

import org.json.JSONObject;

/**
 * 服务器响应类
 *
 */
public class Response {

    private boolean ReturnCode;
    private JSONObject Data;

    public Response() {

    }

    public boolean getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(boolean ReturnCode) {
        this.ReturnCode = ReturnCode;
    }

    public JSONObject getData() {
        return Data;
    }

    public void setData(JSONObject Data) {
        this.Data = Data;
    }



}
