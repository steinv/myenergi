package com.stein.myenergi;

import java.net.URI;

import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class HttpComponentsClientHttpRequestFactoryBasicAuth
        extends HttpComponentsClientHttpRequestFactory {

    HttpHost host;

    public HttpComponentsClientHttpRequestFactoryBasicAuth(CloseableHttpClient client, HttpHost host) {
        super(client);
        this.host = host;
    }

    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext();
    }

    private HttpContext createHttpContext() {
        BasicAuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);

        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        return localcontext;
    }

    public void setReadTimeout(int any) {
        // noop
    }
}