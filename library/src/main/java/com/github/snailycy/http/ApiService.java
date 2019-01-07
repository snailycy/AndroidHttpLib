package com.github.snailycy.http;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Observable<ResponseBody> doGet(@Url String url, @HeaderMap Map<String, String> headerMap);

    @POST
    Observable<ResponseBody> doPost(@Url String url, @HeaderMap Map<String, String> headerMap,
                                    @Body RequestBody body);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url String url, @PartMap() Map<String, RequestBody> maps);

    /**
     * Retrofit默认会将整个文件移到内存中，为了避免这种情况，我们需要为这种请求加一个特殊的注解
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFile(@Url String url, @PartMap() Map<String, RequestBody> maps,
                                        @Part MultipartBody.Part file);
}
