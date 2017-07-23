package com.adeebnqo.Thula.mmssms.okhttpimpl;


import com.adeebnqo.Thula.mmssms.httpclient.IHttpEntity;

import java.io.InputStream;

import okhttp3.ResponseBody;

public class OkHttpEntityImpl implements IHttpEntity {

    ResponseBody body;
    public OkHttpEntityImpl(ResponseBody body) {
        this.body = body;
    }

    @Override
    public long getContentLength() {
        return body.contentLength();
    }

    @Override
    public InputStream getContent() {
        return body.byteStream();
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public void consumeContent() {
        body.close();
    }
}
