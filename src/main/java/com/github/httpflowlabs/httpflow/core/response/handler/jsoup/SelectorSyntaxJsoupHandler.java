package com.github.httpflowlabs.httpflow.core.response.handler.jsoup;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexBuilder;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SelectorSyntaxJsoupHandler implements JsoupHtmlHandler {

    public static final EasyReadRegexBuilder ATTR_PATTERN = EasyReadRegexBuilder.newBuilder()
            .character("@")
            .identifier();

    public static final EasyReadRegexBuilder SELECTOR_PATTERN = EasyReadRegexBuilder.newBuilder()
            .string("SELECT").ignoreCase().whitechar("+")
            .pick("variableType", regex -> {
                regex.string("text()").ignoreCase().or().subBuilder(ATTR_PATTERN);
            }).whitechar("+")
            .string("AS").ignoreCase().whitechar("+")
            .pick("variableName", regex -> {
                regex.identifier();
            }).whitechar("+")
            .string("XPATH").ignoreCase().whitechar("+")
            .pick("xpath", regex -> {
                regex.anychar("*");
            });

    @Override
    public String getHandlerKey() {
        return "SELECT";
    }

    @Override
    public void handle(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, Document document, String jsoupMatch) {
        // jsoupMatch : SELECT text() | @attrName AS {variable name} XPATH {XPath syntax expression}
        // ex#1 : SELECT @value AS timestamp XPATH //input[name=timestamp]
        // ex#2 : SELECT text() AS contents XPATH //div[id=contents]

        EasyReadRegexMatcher matcher = SELECTOR_PATTERN.matcher(jsoupMatch);
        if (matcher.matches()) {
            String value = null;
            Elements selection = document.selectXpath(matcher.getTokenPicked("xpath"));
            if (selection.size() > 0) {
                String variableType = matcher.getTokenPicked("variableType");
                if (variableType.startsWith("@")) {
                    String attrName = variableType.substring(1).trim();
                    value = selection.get(0).attr(attrName);
                } else if ("text()".equalsIgnoreCase(variableType)) {
                    value = selection.get(0).val().trim();
                }
            }
            if (value != null) {
                context.getVelocityContext().put(matcher.getTokenPicked("variableName"), value);
            }
        }
    }

    @Override
    public boolean isXHttpFlowHeaderRequired() {
        return false;
    }

}
