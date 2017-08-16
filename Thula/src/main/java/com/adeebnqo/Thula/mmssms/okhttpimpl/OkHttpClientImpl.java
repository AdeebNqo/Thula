package com.adeebnqo.Thula.mmssms.okhttpimpl;

import android.util.Log;

import com.adeebnqo.Thula.mmssms.httpclient.HttpHost;
import com.adeebnqo.Thula.mmssms.httpclient.IHttpClient;
import com.adeebnqo.Thula.mmssms.httpclient.IHttpRequest;
import com.adeebnqo.Thula.mmssms.httpclient.IHttpResponse;
import com.android.mms.transaction.ProgressCallbackEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient.Builder;


public class OkHttpClientImpl implements IHttpClient {

    private OkHttpClient client;

    public OkHttpClientImpl(int timeout){
        //TODO : proxy
        client = new OkHttpClient();
        Builder builder = new Builder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        client = builder.build();
    }

    @Override
    public IHttpResponse execute(HttpHost target, IHttpRequest request) throws IOException {

        Request.Builder reqBuilder = new Request.Builder();

        //adding url
        reqBuilder = reqBuilder.url(request.getUrl());
        //adding headers
        HashMap<String, String> headers = request.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder = reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        //If the we're posting post
        if (request.getMethodType().equalsIgnoreCase("POST")) {
            ProgressCallbackEntity entity = request.getProgressCallbackEntity();
            MediaType mmsType = MediaType.parse(entity.getContentType() + "; charset=utf-8");
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            entity.writeTo(outstream);
            RequestBody body = RequestBody.create(mmsType, outstream.toByteArray());
            reqBuilder.post(body);
        }

        Request okHttpRequest = reqBuilder.build();
        IHttpResponse response = new OkHttpResponseImpl(client.newCall(okHttpRequest).execute());
        return response;
    }

    @Override
    public void close() {
        //TODO : close the connection.
    }
}
