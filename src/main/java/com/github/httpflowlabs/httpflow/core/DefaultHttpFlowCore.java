package com.github.httpflowlabs.httpflow.core;

import com.github.httpflowlabs.httpflow.core.controller.HttpFlowBlockRepeatController;
import com.github.httpflowlabs.httpflow.core.controller.HttpFlowController;
import com.github.httpflowlabs.httpflow.resource.HttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRetryHttpTemplate;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.cookie.CookieStore;

import java.util.Date;

@Getter
@Setter
public class DefaultHttpFlowCore extends AbstractHttpFlowConfig implements IHttpFlowCore {

    private HttpFlowBlockRepeatController blockRepeatController = new HttpFlowBlockRepeatController();

    @Override
    public void execute(HttpFlowResource resource) {
        execute(resource.getDocument());
    }

    @Override
    public void execute(HttpFlowDocument document) {
        HttpFlowController flowManager = new HttpFlowController(document, super.getLoopCount(), context);

        while (flowManager.hasNextElement() && !context.isStopped()) {
            HttpFlowElement nextElement = flowManager.nextElement();
            context.getCookieStore().clearExpired(new Date());

            blockRepeatController.processElementWithRepeatBlock(flowManager, nextElement, (element) -> {
                processElement(element);
            });
        }
    }

    protected void processElement(HttpFlowElement element) {
        HttpFlowRequest request = new HttpFlowRequest(element, context);
        CookieStore cookieStore = requestCookieResolver.resolve(context.getCookieStore(), element, request);

        HttpTemplateResponse<String> response = new HttpFlowRetryHttpTemplate().exchange(request, cookieStore);

        responseHandler.handleResponse(this, element, request, response);
    }

}
