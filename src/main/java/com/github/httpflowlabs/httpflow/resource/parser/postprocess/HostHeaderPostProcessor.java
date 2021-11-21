package com.github.httpflowlabs.httpflow.resource.parser.postprocess;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.exception.MalformedHttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.pattern.HttpFlowLinePattern;
import com.github.httpflowlabs.httpflow.resource.parser.preprocess.HttpFlowLine;

public class HostHeaderPostProcessor implements PostProcessor {

    @Override
    public void process(HttpFlowElement element, HttpFlowLine currLine) {
        if (element.getProtocol() == HttpFlowProtocol.HTTPFLOW) {
            return;
        }

        if (!HttpFlowLinePattern.URL_WITH_SCHEME.matcher(element.getUrl()).matches()) {
            if (element.getHeader("Host").exists()) {
                String host = element.getHeader("Host").lastValue();
                if (!host.startsWith("http://") && !host.startsWith("https://")) {
                    host = "https://" + host;
                }
                element.setUrl(host + element.getUrl());

            } else {
                throw new MalformedHttpFlowException("Invalid url - Host not found ", currLine.getNumber());
            }
        }
    }

}
