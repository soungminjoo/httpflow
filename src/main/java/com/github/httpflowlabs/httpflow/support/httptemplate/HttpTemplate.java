package com.github.httpflowlabs.httpflow.support.httptemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.httpflowlabs.httpflow.support.httptemplate.entity.HttpEntityResolver;
import com.github.httpflowlabs.httpflow.support.httptemplate.exception.HttpTemplateException;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateEntity;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpTemplate {

    private HttpEntityResolver httpEntityResolver = new HttpEntityResolver();
    private Long connectTimeout;
    private CookieStore cookieStore;

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public <T> HttpTemplateResponse<T> exchange(String url, String httpMethod, HttpTemplateEntity request, Class<T> responseType) {
        try (CloseableHttpClient httpclient = getHttpClient()) {

            ClassicHttpRequest httpRequest = resolveHttpRequest(url, httpMethod, request);
            try (ClassicHttpResponse response = execute(httpclient, httpRequest)) {
                return new HttpTemplateResponse<>(response, responseType);
            }

        } catch (ParseException e) {
            throw new HttpTemplateException(e);
        } catch (IOException e) {
            throw new HttpTemplateException(e);
        }
    }

    private CloseableHttpClient getHttpClient() {
        if (this.connectTimeout != null) {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(this.connectTimeout, TimeUnit.MILLISECONDS).build();
            return HttpClients.custom().setDefaultRequestConfig(config).build();
        }
        return HttpClients.createDefault();
    }

    protected ClassicHttpResponse execute(CloseableHttpClient httpclient, ClassicHttpRequest httpRequest) throws IOException {
        HttpClientContext context = null;
        if (this.cookieStore != null) {
            context = HttpClientContext.create();
            context.setAttribute(HttpClientContext.COOKIE_STORE, this.cookieStore);
        }
        return httpclient.execute(httpRequest, context);
    }

    protected ClassicHttpRequest resolveHttpRequest(String url, String httpMethod, HttpTemplateEntity request) throws JsonProcessingException {
        ClassicHttpRequest httpRequest = null;
        if (httpMethod.equalsIgnoreCase("GET")) {
            httpRequest = new HttpGet(url);

        } else if (httpMethod.equalsIgnoreCase("POST")) {
            httpRequest = new HttpPost(url);

        } else if (httpMethod.equalsIgnoreCase("PUT")) {
            httpRequest = new HttpPut(url);

        } else if (httpMethod.equalsIgnoreCase("DELETE")) {
            httpRequest = new HttpDelete(url);

        } else {
            throw new HttpTemplateException("Unsupported http method : " + httpMethod.toUpperCase());
        }

        httpRequest.setHeaders(request.getHeaders());
        if (request.getContentType() == null) {
            httpRequest.addHeader("Content-Type", "application/json; charset=utf-8");
        }

        if (request.getBody() != null) {
            httpRequest.setEntity(httpEntityResolver.resolveRequestEntity(request));
        }
        return httpRequest;
    }

}
