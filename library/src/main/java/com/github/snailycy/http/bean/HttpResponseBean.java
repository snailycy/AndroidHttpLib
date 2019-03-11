package com.github.snailycy.http.bean;

import com.google.gson.JsonElement;

import java.io.Serializable;

/**
 * Created by ycy on 2018/3/31.
 */

public class HttpResponseBean implements Serializable {
    /**
     * api code
     */
    private int code;
    /**
     * api 业务成功下返回的数据
     */
    private JsonElement data;
    /**
     * api 业务异常下返回的数据
     */
    private JsonElement error;

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

    public JsonElement getError() {
        return error;
    }

    public void setError(JsonElement error) {
        this.error = error;
    }
}
