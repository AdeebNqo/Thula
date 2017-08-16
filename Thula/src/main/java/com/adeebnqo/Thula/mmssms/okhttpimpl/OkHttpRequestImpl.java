package com.adeebnqo.Thula.mmssms.okhttpimpl;

import com.adeebnqo.Thula.mmssms.httpclient.IHttpRequest;
import com.android.mms.transaction.ProgressCallbackEntity;

import java.util.HashMap;


public class OkHttpRequestImpl implements IHttpRequest {

    private String url;
    private HashMap<String, String> headers = new HashMap<>();
    private String proxyHost;
    private int proxyPort;
    private ProgressCallbackEntity entity;
    private String requestMethod;

    public OkHttpRequestImpl(String url) {
        this.url = url;
    }

    @Override
    public void setProgressCallbackEntity(ProgressCallbackEntity entity) {
        this.entity = entity;
    }

    @Override
    public void setMethodType(String method) {
        this.requestMethod = method;
    }

    @Override
    public void addHeader(String key, String val) {
        headers.put(key, val);
    }

    @Override
    public void setProxyDetails(String host, int port) {
        this.proxyHost = host;
        this.proxyPort = port;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getMethodType() {
        return requestMethod;
    }

    @Override
    public ProgressCallbackEntity getProgressCallbackEntity() {
        return entity;
    }
}
