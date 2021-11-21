package com.github.httpflowlabs.httpflow.resource.parser;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.exception.MalformedHttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowBody;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowControlElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.postprocess.PostProcessor;
import com.github.httpflowlabs.httpflow.resource.parser.preprocess.*;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;

import java.util.ArrayList;
import java.util.List;

public class HttpFlowParser {

    private ChromeStatusLineConverter chromeStatusLineConverter = new ChromeStatusLineConverter();
    private HttpFlowCommentTrimmer commentTrimmer = new HttpFlowCommentTrimmer();
    private HttpFlowProtocolValidator httpFlowProtocolValidator = new HttpFlowProtocolValidator();
    private List<PostProcessor> postProcessors = PostProcessor.getProcessors();

    public HttpFlowDocument parse(String httpFlowContents) {
        HttpFlowLine[] stringLines = chromeStatusLineConverter.convert(HttpFlowUtils.splithttpFlowContents(httpFlowContents));
        List<HttpFlowLines> linesList = commentTrimmer.trimComment(stringLines);

        List<HttpFlowElement> parsedElements = new ArrayList<>();
        for (HttpFlowLines lines : linesList) {
            boolean isStatusLineFound = false;
            while (lines.hasNextLine()) {
                HttpFlowLine currLine = lines.nextLine();
                if (currLine.isEmpty() && !isStatusLineFound) {
                    continue;
                }

                if (currLine.isStatusLine()) {
                    parsedElements.add(parseAndGetHttpFlowElement(currLine, lines));
                    isStatusLineFound = true;
                } else if (currLine.isHttpFlowControl()) {
                    parsedElements.add(parseAndGetHttpFlowControlElement(currLine, lines));
                    isStatusLineFound = true;

                } else if (!isStatusLineFound) {
                    throw new MalformedHttpFlowException("Each execution must start status line.", currLine.getNumber());
                }
            }
        }

        return new HttpFlowDocument(parsedElements);
    }

    private HttpFlowElement parseAndGetHttpFlowElement(HttpFlowLine currLine, HttpFlowLines lines) {
        HttpFlowElement element = new HttpFlowElement();
        element.setMethod(currLine.getMethod().toUpperCase());
        element.setUrl(currLine.getUrl());
        element.setProtocol(currLine.getProtocol());

        parseHeaders(element, lines);
        parseBody(element, lines);

        postProcessors.forEach(processor -> processor.process(element, currLine));
        return element;
    }

    private HttpFlowElement parseAndGetHttpFlowControlElement(HttpFlowLine currLine, HttpFlowLines lines) {
        HttpFlowControlElement element = new HttpFlowControlElement();
        element.setMethod(currLine.getHttpFlowControlMethod().toUpperCase());
        element.setParams(currLine.getHttpFlowControlParams());
        element.setProtocol(currLine.getHttpFlowControlProtocol());
        return element;
    }

    private void parseHeaders(HttpFlowElement element, HttpFlowLines lines) {
        lines.consumeLines(currLine -> {
            if (currLine.isHeaderLine()) {
                if (element.getProtocol() == HttpFlowProtocol.HTTPFLOW) {
                    httpFlowProtocolValidator.validate(element, currLine);
                }
                element.addHeader(currLine.getHeaderName(), currLine.getHeaderValue());
            }
        }, true);
    }

    private void parseBody(HttpFlowElement element, HttpFlowLines lines) {
        HttpFlowBody body = new HttpFlowBody();

        lines.consumeLines(currLine -> {
            body.appendString(currLine.getText() + "\n");
        }, false);

        if (body.toString().length() > 0) {
            body.trimFinalLineBreak();
            element.setBody(body);

        } else if ("POST|PUT".toUpperCase().contains(element.getMethod().toUpperCase())) {
            element.setBody(new HttpFlowBody());
        }
    }

}
