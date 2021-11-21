package com.github.httpflowlabs.httpflow.core.controller;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import ognl.Ognl;
import ognl.OgnlException;

import java.util.HashMap;

public class HttpFlowController {

    private HttpFlowContext context;
    private HttpFlowDocument document;
    private int loopCount;
    private int nextElementIndex;

    public HttpFlowController(HttpFlowDocument document, int loopCount, HttpFlowContext context) {
        this.document = document;
        this.loopCount = loopCount;
        this.context = context;
    }

    public boolean hasNextElement() {
        while (nextElementIndex < document.getElements().size()) {
            HttpFlowElement element = document.getElements().get(nextElementIndex);
            if (checkHttpFlowIfTrue(element)) {
                return true;
            }
            nextElementIndex++;
        }

        return false;
    }

    private boolean checkHttpFlowIfTrue(HttpFlowElement element) {
        for (String expression : element.getHeader(HttpFlowConstants.HTTP_FLOW_IF).asList()) {
            expression = HttpFlowUtils.translateWithVelocity(expression, context.getCookieMergedVelocityContext(), true);

            try {
                Object expr = Ognl.parseExpression(expression);
                Object value = Ognl.getValue(expr, new HashMap(), new Object());

                if (!(value instanceof Boolean)) {
                    throw new HttpFlowXHeaderException(HttpFlowConstants.HTTP_FLOW_IF + " value must be boolean expression");
                }
                if (!(Boolean) value) {
                    return false;
                }
            } catch (OgnlException e) {
                throw new HttpFlowXHeaderException(e);
            }
        }
        return true;
    }

    public HttpFlowElement nextElement() {
        HttpFlowElement element = document.getElements().get(nextElementIndex++);
        if (this.loopCount > 1 && !hasNextElement()) {
            this.loopCount--;
            this.nextElementIndex = 0;
            this.context.reset();
        }
        return element;
    }

    public int getAbsIndex(HttpFlowElement element) {
        return document.getElements().indexOf(element);
    }

    public void resetElementIndex(int index) {
        this.nextElementIndex = index;
    }

}
