package com.github.snailycy.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.github.snailycy.http.callback.HttpRequestCallback;
import com.github.snailycy.http.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author snailycy
 */
public class SampleActivity extends Activity {
    public static final String TAG = "SampleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        Map<String, String> headers = new HashMap<>();
        headers.put("testheaders", "testheaders");

        Map<String, Object> params = new HashMap<>();
        params.put("testparams", "testparams");

        HttpUtils.getInstance(this)
                .setHost("http://119.29.115.76/")
                .setPath("applogin")
                .addHeaders(headers)
                .addParams(params)
                .addAES("1234567890123456")
                .doPost(new HttpRequestCallback<String, String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, "onSuccess = " + s);
                    }

                    @Override
                    public void onError(String s) {
                        Log.e(TAG, "onError = " + s);
                    }
                });

    }


}
