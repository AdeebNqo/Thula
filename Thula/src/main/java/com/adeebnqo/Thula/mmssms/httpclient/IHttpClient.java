package com.adeebnqo.Thula.mmssms.httpclient;

import java.io.IOException;

/**
 * This is a class that I will be using to wrap OkHttp since Apache's AndroidHttpClient is deprecated.
 *
 */

public interface IHttpClient {
    IHttpResponse execute(HttpHost target, IHttpRequest request) throws IOException;
    void close();
}
