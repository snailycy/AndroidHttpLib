package com.github.snailycy.http.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.github.snailycy.http.RetrofitManager;
import com.github.snailycy.http.bean.HttpResponseBean;
import com.github.snailycy.http.callback.HttpRequestCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.$Gson$Types;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class HttpUtils {
    private static HttpUtils sHttpUtils;
    public static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private static final Gson GSON = new Gson();
    private static Context sContext;
    private String mPath;
    private RetrofitManager mRetrofitManager;
    private Map<String, String> mHeaders;
    private Map<String, Object> mParams;
    private String mSecret;

    public static HttpUtils getInstance(Context context) {
        sContext = context;
        if (sHttpUtils == null) {
            synchronized (HttpUtils.class) {
                if (sHttpUtils == null) {
                    sHttpUtils = new HttpUtils();
                }
            }
        }
        return sHttpUtils;
    }

    private HttpUtils() {
        mRetrofitManager = new RetrofitManager(sContext);
    }

    public HttpUtils setHost(String host) {
        mRetrofitManager.setHost(host);
        return this;
    }

    public HttpUtils setPath(String path) {
        this.mPath = path;
        return this;
    }

    public HttpUtils addHeaders(Map<String, String> headers) {
        this.mHeaders = headers;
        return this;
    }

    public HttpUtils addParams(Map<String, Object> params) {
        this.mParams = params;
        return this;
    }

    public HttpUtils addAES(String secret) {
        this.mSecret = secret;
        return this;
    }

    public <RESPONSE, ERROR> void doGet(final HttpRequestCallback<RESPONSE, ERROR> httpRequestCallback) {
        try {
            Map<String, String> headers = wrapperCommonHeaders(mHeaders);
            String params = map2Params(mParams);
            if (!TextUtils.isEmpty(mSecret)) {
                params = URLEncoder.encode(AESCryptUtils.encrypt(params, mSecret), "UTF-8");
            }
            String url = mPath + "?" + params;
            mRetrofitManager.getApiService().doGet(url, headers)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {
                            String body = responseBody.string();
                            reportSuccess(body, httpRequestCallback);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) throws Exception {
                            reportError(e, httpRequestCallback);
                        }
                    });
        } catch (Throwable t) {
            reportError(t, httpRequestCallback);
            t.printStackTrace();
        }
    }

    public <RESPONSE, ERROR> void doPost(final HttpRequestCallback<RESPONSE, ERROR> httpRequestCallback) {
        try {
            Map<String, String> headers = wrapperCommonHeaders(mHeaders);
            String params = GSON.toJson(mParams);
            if (!TextUtils.isEmpty(mSecret)) {
                params = URLEncoder.encode(AESCryptUtils.encrypt(params, mSecret), "UTF-8");
            }
            mRetrofitManager.getApiService().doPost(mPath, headers, RequestBody.create(MediaType.parse(CONTENT_TYPE), params))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Exception {
                            String body = responseBody.string();
                            reportSuccess(body, httpRequestCallback);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) throws Exception {
                            reportError(e, httpRequestCallback);
                        }
                    });
        } catch (Throwable t) {
            reportError(t, httpRequestCallback);
            t.printStackTrace();
        }
    }

    private String map2Params(Map<String, Object> params) {
        if (params == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private <RESPONSE, ERROR> void reportError(Throwable e, HttpRequestCallback<RESPONSE, ERROR> httpRequestCallback) {
        try {
            httpRequestCallback.onError(null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private <RESPONSE, ERROR> void reportSuccess(String body, HttpRequestCallback<RESPONSE, ERROR> httpRequestCallback) {
        try {
            if (!TextUtils.isEmpty(mSecret)) {
                body = AESCryptUtils.decrypt(URLDecoder.decode(body, "UTF-8"), mSecret);
            }
            HttpResponseBean httpResponseBean = GSON.fromJson(body, HttpResponseBean.class);
            Type[] genericInterfaces = httpRequestCallback.getClass().getGenericInterfaces();
            ParameterizedType responsePT = (ParameterizedType) genericInterfaces[0];
            Type responseType = $Gson$Types.canonicalize(responsePT.getActualTypeArguments()[0]);
            JsonElement data = httpResponseBean.getData();
            if (data != null) {
                if ("class java.lang.String".equalsIgnoreCase(responseType.toString())) {
                    httpRequestCallback.onSuccess((RESPONSE) data.toString());
                } else {
                    httpRequestCallback.onSuccess((RESPONSE) GSON.fromJson(data, responseType));
                }
            } else {
                ParameterizedType errorPT = (ParameterizedType) genericInterfaces[1];
                Type errorType = $Gson$Types.canonicalize(errorPT.getActualTypeArguments()[0]);
                if ("class java.lang.String".equalsIgnoreCase(errorType.toString())) {
                    httpRequestCallback.onError((ERROR) httpResponseBean.getError().toString());
                } else {
                    httpRequestCallback.onError((ERROR) GSON.fromJson(httpResponseBean.getError(), errorType));
                }
            }
        } catch (Throwable t) {
            reportError(t, httpRequestCallback);
            t.printStackTrace();
        }
    }

    private Map<String, String> wrapperCommonHeaders(Map<String, String> headers) {
        Map<String, String> finalHeaders = new HashMap<>();
        finalHeaders.put("system", "android");
        finalHeaders.put("version", String.valueOf(VersionUtils.getVersionCode(sContext)));
        finalHeaders.put("screenSize", String.valueOf(DeviceUtils.getDisplayResolution(sContext)));
        finalHeaders.put("sysVersion", String.valueOf(Build.VERSION.SDK_INT));
        finalHeaders.put("network", String.valueOf(NetworkUtils.getNetworkType(sContext)));
        finalHeaders.put("deviceBrand", String.valueOf(getValueEncoded(DeviceUtils.getBrand())));
        finalHeaders.put("androidId", DeviceUtils.getAndroidId(sContext));
        finalHeaders.put("imei", DeviceUtils.getIMEI(sContext));
        finalHeaders.put("mac", DeviceUtils.getMac(sContext));
        String packageName = sContext.getPackageName();
        finalHeaders.put("User-agent", packageName);
        if (!CollectionsUtils.isEmpty(headers)) {
            finalHeaders.putAll(headers);
        }
        return finalHeaders;
    }

    /**
     * 由于okhttp中的 value 不支持 null, \n 和 中文这样的特殊字符
     *
     * @param value
     * @return
     */
    private String getValueEncoded(String value) {
        if (value == null) {
            return "";
        }
        String newValue = value.replace("\n", "");

        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
                try {
                    String encodeStr = URLEncoder.encode(newValue, "UTF-8");
                    return encodeStr;
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
        return newValue;
    }
}
