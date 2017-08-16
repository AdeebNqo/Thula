package com.adeebnqo.Thula.mmssms.okhttpimpl;


import com.adeebnqo.Thula.mmssms.httpclient.IHttpEntity;
import com.adeebnqo.Thula.mmssms.httpclient.IHttpResponse;
import com.adeebnqo.Thula.mmssms.httpclient.IHttpStatusLine;
import okhttp3.Response;

public class OkHttpResponseImpl implements IHttpResponse {

    private Response actualResponse;

    public OkHttpResponseImpl(Response okhtpResponse) {
        actualResponse = okhtpResponse;
    }

    @Override
    public IHttpEntity getEntity() {
        return new OkHttpEntityImpl(actualResponse.body());
    }

    @Override
    public IHttpStatusLine getStatusLine() {
        return new OkHttpStatusLineImpl(actualResponse.code(), actualResponse.message());
    }
}
