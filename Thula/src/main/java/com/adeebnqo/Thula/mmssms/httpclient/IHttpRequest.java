package com.adeebnqo.Thula.mmssms.httpclient;

import com.android.mms.transaction.ProgressCallbackEntity;

import java.util.HashMap;

public interface IHttpRequest {
    void setProgressCallbackEntity(ProgressCallbackEntity entity);
    void setMethodType(String method);
    void addHeader(String key, String val);
    void setProxyDetails(String host, int port);

    String getUrl();
    HashMap<String, String> getHeaders();
    String getMethodType();
    ProgressCallbackEntity getProgressCallbackEntity();
}
