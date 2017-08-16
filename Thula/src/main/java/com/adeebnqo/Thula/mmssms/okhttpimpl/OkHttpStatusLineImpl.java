package com.adeebnqo.Thula.mmssms.okhttpimpl;


import com.adeebnqo.Thula.mmssms.httpclient.IHttpStatusLine;

public class OkHttpStatusLineImpl implements IHttpStatusLine {

    private String reason;
    private int code;

    public  OkHttpStatusLineImpl(int code, String reason){
        this.code = code;
        this.reason = reason;
    }
    @Override
    public int getStatusCode() {
        return code;
    }

    @Override
    public String getReasonPhrase() {
        return reason;
    }
}
