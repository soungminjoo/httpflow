package com.github.httpflowlabs.httpflow.core.request.cookie;

import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;

import java.net.HttpCookie;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultRequestCookieResolver extends AbstractRequestCookieResolver {

    @Override
    public CookieStore resolve(CookieStore localCookieStore, HttpFlowElement element, HttpFlowRequest request) {
        Map<String, HttpCookie> targetCookieFromFlowDef = getTargetCookieFromFlowDef(element);
        Set<String> cookieNamesToAdd = getFlowDefCookieNotExistInLocalStorage(localCookieStore, targetCookieFromFlowDef);

        URI uri = HttpFlowUtils.parseURI(request.getUrl());
        for (String name : cookieNamesToAdd) {
            HttpCookie flowDefCookie = targetCookieFromFlowDef.get(name);
            addCookie(localCookieStore, flowDefCookie, uri);
        }

        return localCookieStore;
    }

    private Set<String> getFlowDefCookieNotExistInLocalStorage(CookieStore localCookieStore, Map<String, HttpCookie> targetCookieFromFlowDef) {
        Set<String> targetCookieNameSet = new HashSet(targetCookieFromFlowDef.keySet());

        for (Cookie localCookie : localCookieStore.getCookies()) {
            if (targetCookieNameSet.contains(localCookie.getName())) {
                targetCookieNameSet.remove(localCookie.getName());
            }
        }
        return targetCookieNameSet;
    }

}
