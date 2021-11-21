package com.github.httpflowlabs.httpflow.support.httptemplate.model;

import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpTemplateUrlEncodedFormEntity extends UrlEncodedFormEntity {

    public HttpTemplateUrlEncodedFormEntity(Map<String, Object> bodyMap) {
        super(toNameAndValuePairList(bodyMap));
    }

    private static List<? extends NameValuePair> toNameAndValuePairList(Map<String, Object> bodyMap) {
        List<NameValuePair> list = new ArrayList<>();
        for (String name : bodyMap.keySet()) {
            list.add(new BasicNameValuePair(name, String.valueOf(bodyMap.get(name))));
        }
        return list;
    }

}
