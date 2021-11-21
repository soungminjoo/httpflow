package com.github.httpflowlabs.httpflow.support.httptemplate.model;

import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexBuilder;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;
import com.github.httpflowlabs.httpflow.support.httptemplate.exception.HttpTemplateException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTemplateEntity<T> {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private static final EasyReadRegexBuilder CONTENT_TYPE_PATTERN = EasyReadRegexBuilder.newBuilder()
            .pick("contentType", regex -> regex.anychar("*"))
            .character(';').whitechar("*").string("charset=").ignoreCase().anychar("*");

    private List<Header> headers = new ArrayList<>();
    private ContentType contentType;

    private Object body;

    public HttpTemplateEntity() {
    }

    public HttpTemplateEntity(T body) {
        this.body = body;
    }

    public void addHeader(String name, Object value) {
        if (CONTENT_LENGTH.equalsIgnoreCase(name)) {
            return;
        }
        if (CONTENT_TYPE.equalsIgnoreCase(name)) {
            resolveContentType(value);
        }
        this.headers.add(new BasicHeader(name, value));
    }

    private void resolveContentType(Object value) {
        if (value instanceof ContentType) {
            this.contentType = (ContentType) value;

        } else if (value instanceof String) {
            EasyReadRegexMatcher m = CONTENT_TYPE_PATTERN.matcher((String) value);
            if (m.matches()) {
                value = m.getTokenPicked("contentType");
            }
            this.contentType = ContentType.create((String) value);
        } else {
            throw new HttpTemplateException(CONTENT_TYPE + " value must be type of org.apache.hc.core5.http.ContentType.class or String.class");
        }
    }

    public Header[] getHeaders() {
        return this.headers.toArray(new Header[0]);
    }

    public void removeCookieHeader() {
        List<Header> cookieHeaders = this.headers.stream()
                .filter(header -> "Cookie".equalsIgnoreCase(header.getName()))
                .collect(Collectors.toList());

        this.headers.removeAll(cookieHeaders);
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setBody(HttpEntity entity) {
        this.body = entity;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return (T) body;
    }

}
