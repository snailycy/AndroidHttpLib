package com.github.snailycy.http.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 针对某些url 设置超时时间
 */
public class TimeoutInterceptor implements okhttp3.Interceptor {

    /**
     * 需要单独设置超时时间的接口
     */
    private List<String> mTimeoutPaths;
    private int mTimeout;

    public TimeoutInterceptor(List<String> path,int timeout) {
        mTimeoutPaths = path;
        mTimeout = timeout;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        if (newIntercept(chain.request())) {
            Chain chainNew = chain.withConnectTimeout(mTimeout, TimeUnit.MILLISECONDS)
                    .withReadTimeout(mTimeout, TimeUnit.MILLISECONDS);
            return chainNew.proceed(chain.request());
        }
        return chain.proceed(chain.request());
    }

    private boolean newIntercept(Request originRequest) {
        if (null == originRequest || null == originRequest.url() || TextUtils.isEmpty(originRequest.url().encodedPath())
                || null == mTimeoutPaths || mTimeoutPaths.isEmpty()) {
            return false;
        }
        String path = originRequest.url().encodedPath();
        for (String timeoutPath : mTimeoutPaths) {
            if (!TextUtils.isEmpty(timeoutPath) && path.endsWith(timeoutPath)) {
                return true;
            }
        }
        return false;
    }
}
