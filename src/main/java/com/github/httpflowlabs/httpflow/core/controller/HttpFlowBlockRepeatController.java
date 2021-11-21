package com.github.httpflowlabs.httpflow.core.controller;

import com.github.httpflowlabs.httpflow.resource.parser.exception.MalformedHttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowControlElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.methods.HttpFlowControl;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

public class HttpFlowBlockRepeatController {

    public void processElementWithRepeatBlock(HttpFlowController flowManager, HttpFlowElement nextElement, Consumer<HttpFlowElement> supplier) {
        if (!(nextElement instanceof HttpFlowControlElement)) {
            supplier.accept(nextElement);
            return;
        }

        int nextBlockRepeatCount = getBlockRepeatCount(nextElement);
        if (!flowManager.hasNextElement()) {
            return;
        }

        int blockStartIndex = flowManager.getAbsIndex(nextElement);
        for (int i = 0; i < nextBlockRepeatCount; i++) {

            flowManager.resetElementIndex(blockStartIndex + 1);
            while (flowManager.hasNextElement()) {
                HttpFlowElement childElement = flowManager.nextElement();
                processElementWithRepeatBlock(flowManager, childElement, supplier);
                if (existsBlockRepeatEnd(childElement)) {
                    break;
                }
            }
        }
    }

    private int getBlockRepeatCount(HttpFlowElement nextElement) {
        if (nextElement instanceof HttpFlowControlElement) {
            HttpFlowControlElement ce = (HttpFlowControlElement) nextElement;
            if (HttpFlowControl.FOR.equalsIgnoreCase(ce.getMethod())) {
                if (StringUtils.isEmpty(ce.getParams())) {
                    throw new MalformedHttpFlowException(HttpFlowControl.FOR + " control method requires number params. ex) FOR 3 HTTPFLOW/1.0");
                }
                try {
                    int count = Integer.parseInt(ce.getParams().trim());
                    if (count < 1) {
                        throw new MalformedHttpFlowException(HttpFlowControl.FOR + " control method param value count cannot be zero or minus.");
                    }
                    return count;
                } catch (NumberFormatException e) {
                    throw new MalformedHttpFlowException(HttpFlowControl.FOR + " control method param value is invalid : " + ce.getParams());
                }
            }
        }
        return 1;
    }

    private boolean existsBlockRepeatEnd(HttpFlowElement nextElement) {
        if (nextElement instanceof HttpFlowControlElement) {
            HttpFlowControlElement ce = (HttpFlowControlElement) nextElement;
            if (HttpFlowControl.END_FOR.equalsIgnoreCase(ce.getMethod())) {
                return true;
            }
        }
        return false;
    }

}
