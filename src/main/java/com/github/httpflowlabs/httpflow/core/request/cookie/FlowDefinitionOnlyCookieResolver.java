package com.github.httpflowlabs.httpflow.core.request.cookie;

import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Map;

public class FlowDefinitionOnlyCookieResolver extends AbstractRequestCookieResolver {

    @Override
    public CookieStore resolve(CookieStore localCookieStore, HttpFlowElement element, HttpFlowRequest request) {
        Map<String, HttpCookie> targetCookieFromFlowDef = getTargetCookieFromFlowDef(element);
        CookieStore currentCookieStore = new BasicCookieStore();
        URI uri = HttpFlowUtils.parseURI(request.getUrl());

        for (HttpCookie cookie : targetCookieFromFlowDef.values()) {
            addCookie(currentCookieStore, cookie, uri);
        }
        return currentCookieStore;
    }

}
