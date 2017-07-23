package com.adeebnqo.Thula.mmssms.httpclient;


public class HttpHost {

    public static String DEFAULT_SCHEME_NAME = "http";
    private String host;
    private int port;
    private String defaultScheme;

    public HttpHost(String host, int port, String httpScheme) {
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDefaultScheme() {
        return defaultScheme;
    }
}
