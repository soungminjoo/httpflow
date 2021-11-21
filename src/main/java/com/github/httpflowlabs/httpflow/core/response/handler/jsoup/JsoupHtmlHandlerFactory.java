package com.github.httpflowlabs.httpflow.core.response.handler.jsoup;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

public class JsoupHtmlHandlerFactory {

    private Map<String, JsoupHtmlHandler> handlerMap = new CaseInsensitiveMap<>();

    public JsoupHtmlHandlerFactory() {
        registerHandler(new SelectorSyntaxJsoupHandler());
        registerHandler(new NotEmptyInputValueJsoupHandler(true));
    }

    public void registerHandler(JsoupHtmlHandler jsoupHtmlHandler) {
        if (!handlerMap.containsKey(jsoupHtmlHandler.getHandlerKey())) {
            handlerMap.put(jsoupHtmlHandler.getHandlerKey(), jsoupHtmlHandler);
        }
    }

    public JsoupHtmlHandler getJsoupHtmlHandler(String jsoupHtmlHandlerKey) {
        if (jsoupHtmlHandlerKey.indexOf(" ") != -1) {
            jsoupHtmlHandlerKey = jsoupHtmlHandlerKey.substring(0, jsoupHtmlHandlerKey.indexOf(" "));
        }
        return handlerMap.get(jsoupHtmlHandlerKey);
    }

}
