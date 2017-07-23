package com.adeebnqo.Thula.mmssms.httpclient;


import java.io.InputStream;

public interface IHttpEntity {
    long getContentLength();
    InputStream getContent();
    boolean isChunked();
    void consumeContent();
}
