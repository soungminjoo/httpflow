package com.github.httpflowlabs.httpflow.core.request.cookie;

import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRequestCookieResolver implements RequestCookieResolver {

    protected Map<String, HttpCookie> getTargetCookieFromFlowDef(HttpFlowElement element) {
        List<String> cookieList = element.getHeader("Cookie").asList();
        Map<String, HttpCookie> targetCookieNameSet = new HashMap<>();

        for (String cookieStr : cookieList) {
            String[] tokens = cookieStr.split(";");

            for (String token : tokens) {
                List<HttpCookie> cookies = HttpCookie.parse(token);

                for (HttpCookie cookie : cookies) {
                    targetCookieNameSet.put(cookie.getName(), cookie);
                }
            }
        }
        return targetCookieNameSet;
    }

    protected void addCookie(CookieStore cookieStore, HttpCookie flowDefCookie, URI uri) {
        BasicClientCookie cookie = new BasicClientCookie(flowDefCookie.getName(), flowDefCookie.getValue());
        cookie.setDomain(uri.getHost());
        cookieStore.addCookie(cookie);
    }

}
