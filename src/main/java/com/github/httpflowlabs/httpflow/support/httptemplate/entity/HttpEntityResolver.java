package com.github.httpflowlabs.httpflow.support.httptemplate.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.httpflowlabs.httpflow.support.httptemplate.exception.HttpTemplateException;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateEntity;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateUrlEncodedFormEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class HttpEntityResolver {

    public HttpEntity resolveRequestEntity(HttpTemplateEntity request) throws JsonProcessingException {
        Object body = request.getBody();

        if (body instanceof HttpEntity) {
            return (HttpEntity) body;

        } else if (body instanceof String) {
            return new StringEntity((String) body, request.getContentType());

        } else if (body instanceof byte[]) {
            return new ByteArrayEntity((byte[]) body, request.getContentType());

        } else if (body instanceof InputStream) {
            return new InputStreamEntity((InputStream) body, request.getContentType());

        } else if (body instanceof File) {
            return new FileEntity((File) body, request.getContentType());

        } else if (body instanceof Path) {
            return new PathEntity((Path) body, request.getContentType());

        } else if (body instanceof Map) {
            if (request.getContentType() == null || request.getContentType().isSameMimeType(ContentType.APPLICATION_JSON)) {
                ContentType contentType = request.getContentType();
                if (contentType == null) {
                    contentType = ContentType.APPLICATION_JSON.withCharset("UTF-8");
                }
                if (contentType.getCharset() == null) {
                    contentType = contentType.withCharset("UTF-8");
                }
                return new StringEntity(new ObjectMapper().writeValueAsString(body), contentType);

            } else if (ContentType.APPLICATION_FORM_URLENCODED.isSameMimeType(request.getContentType())) {
                return new HttpTemplateUrlEncodedFormEntity((Map<String, Object>) body);
            }

            throw new HttpTemplateException("Map type request body requires Content-Type header.");
        }
        throw new HttpTemplateException("Unsupported request body type : " + body.getClass());
    }

}
