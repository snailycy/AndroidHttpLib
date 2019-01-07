package com.github.snailycy.http.callback;

public interface HttpRequestCallback<RESPONSE, ERROR> {

    void onSuccess(RESPONSE response);

    void onError(ERROR error);

}
