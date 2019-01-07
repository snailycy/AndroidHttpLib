package com.github.snailycy.http.bean;

import com.google.gson.JsonElement;

import java.io.Serializable;

/**
 * Created by ycy on 2018/3/31.
 */

public class HttpResponseBean implements Serializable {
    private int code;
    private JsonElement data;
    private String errorCode;
    private JsonElement message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public JsonElement getMessage() {
        return message;
    }

    public void setMessage(JsonElement message) {
        this.message = message;
    }
}
