package com.github.httpflowlabs.httpflow.core.request.cookie;

import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.apache.hc.client5.http.cookie.CookieStore;

public class IgnoreFlowDefinitionCookieResolver implements RequestCookieResolver {

    @Override
    public CookieStore resolve(CookieStore localCookieStore, HttpFlowElement element, HttpFlowRequest request) {
        return localCookieStore;
    }

}
