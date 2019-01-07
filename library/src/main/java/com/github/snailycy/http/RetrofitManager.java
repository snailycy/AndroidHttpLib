package com.github.snailycy.http;


import android.content.Context;

import com.github.snailycy.http.interceptor.CacheControlInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitManager {

    private static final long CONNECT_TIMEOUT = 60;
    private static final long READ_TIMEOUT = 60;
    private static SSLContext sSSLContext;
    private Retrofit sRetrofit;
    private Context mContext;

    // 尝试修复 UndeliverableException https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
    static {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                if (null == e) {
                    return;
                }

                if (e instanceof UndeliverableException) {
                    e = e.getCause();
                }
                if ((e instanceof IOException) || (e instanceof SocketException)) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return;
                }
                if (e instanceof InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return;
                }
                if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                    // that's likely a bug in the application
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    return;
                }
                if (e instanceof IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    return;
                }
            }
        });
    }

    public OkHttpClient getOkHttpClient() {
        Cache cache = new Cache(new File(mContext.getApplicationContext().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);
        ignoreHttpsCertification();
        return new OkHttpClient.Builder()
                .cache(cache)
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(sSSLContext.getSocketFactory())
                .addInterceptor(new CacheControlInterceptor(mContext))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
    }

    /**
     * 忽略所有https证书
     */
    private void ignoreHttpsCertification() {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        }};
        try {
            sSSLContext = SSLContext.getInstance("SSL");
            sSSLContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
        } catch (Exception e) {
        }

    }

    public RetrofitManager(Context context) {
        this.mContext = context;
    }

    public ApiService getApiService() {
        return sRetrofit.create(ApiService.class);
    }

    public void setHost(String host) {
        sRetrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
    }
}
