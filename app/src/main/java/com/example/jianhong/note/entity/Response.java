package com.example.jianhong.note.entity;

import org.json.JSONObject;

/**
 * 服务器响应类
 *
 */
public class Response {

    private boolean ReturnCode;
    private JSONObject Data;
    private long syn_uid;

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

    public long getSynUid() {
        return syn_uid;
    }

    public void setSynUid(long syn_uid) {
        this.syn_uid = syn_uid;
    }


}
