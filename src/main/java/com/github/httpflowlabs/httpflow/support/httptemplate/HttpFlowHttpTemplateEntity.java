package com.github.httpflowlabs.httpflow.support.httptemplate;

import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateEntity;

import java.util.List;
import java.util.Map;

public class HttpFlowHttpTemplateEntity extends HttpTemplateEntity {

    public HttpFlowHttpTemplateEntity(HttpFlowRequest element) {
        loadHeaders(element.getHeaders());
        loadBody(element.getBody());
    }

    private void loadHeaders(String headersStr) {
        Map<String, Map> headers = HttpFlowUtils.readValueAsMap(headersStr);

        for (String headerName : headers.keySet()) {
            Map headerValues = headers.get(headerName);
            List<String> values = (List<String>) headerValues.get("values");

            for (String value : values) {
                super.addHeader(headerName, value);
            }
        }
    }

    private void loadBody(String body) {
        super.setBody(body);
    }

}
