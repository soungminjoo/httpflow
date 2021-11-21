package com.github.httpflowlabs.httpflow.core.response.handler.jsoup;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class NotEmptyInputValueJsoupHandler implements JsoupHtmlHandler {

    private boolean isXHttpFlowHeaderRequired;

    public NotEmptyInputValueJsoupHandler() {
    }

    public NotEmptyInputValueJsoupHandler(boolean isXHttpFlowHeaderRequired) {
        this.isXHttpFlowHeaderRequired = isXHttpFlowHeaderRequired;
    }

    @Override
    public String getHandlerKey() {
        return "NOT_EMPTY_INPUT_VALUE";
    }

    @Override
    public void handle(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, Document document, String jsoupMatch) {
        Elements inputs = document.getElementsByTag("input");
        for (int i = 0; i < inputs.size(); i++) {
            if (!StringUtils.isEmpty(inputs.get(i).val())) {
                context.getVelocityContext().put(inputs.get(i).attr("name"), inputs.get(i).val());
            }
        }
    }

    @Override
    public boolean isXHttpFlowHeaderRequired() {
        return isXHttpFlowHeaderRequired;
    }

}
