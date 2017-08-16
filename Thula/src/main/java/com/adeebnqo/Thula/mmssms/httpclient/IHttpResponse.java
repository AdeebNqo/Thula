package com.adeebnqo.Thula.mmssms.httpclient;


public interface IHttpResponse {
    IHttpEntity getEntity();
    IHttpStatusLine getStatusLine();
}
