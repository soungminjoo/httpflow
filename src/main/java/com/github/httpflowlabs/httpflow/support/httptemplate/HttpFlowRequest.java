package com.github.httpflowlabs.httpflow.support.httptemplate;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.response.handler.CsrfTokenParserSupport;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowHeaderValues;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.google.common.net.UrlEscapers;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

@Getter
@Setter
public class HttpFlowRequest {

    private String method;
    private String url;
    private String headers;
    private String body;

    public HttpFlowRequest(HttpFlowElement element, HttpFlowContext context) {
        this.method = element.getMethod();
        this.url = mergeByVelocityContext(element.getUrl(), context);
        this.url = UrlEscapers.urlFragmentEscaper().escape(this.url);

        this.headers = preprocessHeadersAsJsonString(element.getHeaders(), context.getCsrfToken());
        this.headers = mergeByVelocityContext(this.headers, context);

        if (element.getBody() != null) {
            this.body = mergeByVelocityContext(element.getBody().toString(), context);
        }
    }

    private String mergeByVelocityContext(String template, HttpFlowContext context) {
        return HttpFlowUtils.translateWithVelocity(template, context);
    }

    private String preprocessHeadersAsJsonString(Map<String, HttpFlowHeaderValues> headers, String csrfToken) {
        Map<String, HttpFlowHeaderValues> map = new CaseInsensitiveMap<>();
        for (String key : headers.keySet()) {
            if (headers.get(key).exists()) {
                map.put(key, headers.get(key));
            }
        }

        if (csrfToken != null) {
            HttpFlowHeaderValues values = new HttpFlowHeaderValues();
            values.add(csrfToken);
            map.put(CsrfTokenParserSupport.X_CSRF_TOKEN, values);
        }
        return HttpFlowUtils.writeValueAsString(map);
    }
    
}
