package com.github.httpflowlabs.httpflow.support.httptemplate.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class HttpTemplateResponse<T> {

    private int code;
    private String reasonPhrase;
    private T body;
    private Header[] headers;

    public HttpTemplateResponse(ClassicHttpResponse response, Class<T> responseType) throws IOException, ParseException {
        this.code = response.getCode();
        this.reasonPhrase = response.getReasonPhrase();

        String entityToString = EntityUtils.toString(response.getEntity());
        if (String.class.equals(responseType)) {
            this.body = (T) entityToString;
        } else if (entityToString != null && !entityToString.equals("")) {
            this.body = new ObjectMapper().readValue(entityToString, responseType);
        }

        this.headers = response.getHeaders();
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public T getBody() {
        return body;
    }

    public String getHeader(String name) {
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public Header[] getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HttpTemplateResponse{")
                .append(code).append(" ");
        if (reasonPhrase != null && !reasonPhrase.equals("")) {
            builder.append(reasonPhrase);
        }
        builder.append(", ")
                .append(body);
        for (Header header : headers) {
            builder.append(", ").append(header.getName()).append("=").append(header.getValue());
        }
        builder.append("}");

        return builder.toString();
    }

}
