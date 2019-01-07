package com.github.snailycy.http.interceptor;

import android.content.Context;

import com.github.snailycy.http.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class CacheControlInterceptor implements Interceptor {
    //缓存有效期 1天
    public static final long CACHE_STALE_SECOND = 24 * 60 * 60;

    private Context mContext;

    public CacheControlInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtils.isConnected(mContext)) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }
        Response originalResponse = chain.proceed(request);

        if (NetworkUtils.isConnected(mContext)) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .header("Content-Type", "application/json")
                    .removeHeader("Pragma").build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached," + CACHE_STALE_SECOND)
                    .removeHeader("Pragma").build();
        }
    }
}
