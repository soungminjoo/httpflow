package com.github.httpflowlabs.httpflow.core.response.handler;

import com.github.httpflowlabs.httpflow.core.AbstractHttpFlowConfig;
import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.pattern.HttpFlowLinePattern;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexBuilder;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;


public abstract class ResponseLineParserSupport extends ResponseAssertionSupport {

    protected void handleResponse(AbstractHttpFlowConfig httpFlow, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
        super.handleResponse(httpFlow, element, request, response);

        for (String headerName : element.getHeaders().keySet()) {
            if (headerName.equalsIgnoreCase(HttpFlowConstants.HTTP_FLOW_BODY_MATCH_REGEX)) {
                for (String parseRule : element.getHeaders().get(headerName).asList()) {
                    parseResponseBodyAndMatchRegex(httpFlow.getContext(), parseRule, response.getBody());
                }
            }
        }
    }

    private void parseResponseBodyAndMatchRegex(HttpFlowContext context, String parseRule, String body) {
        EasyReadRegexMatcher matcher = HttpFlowLinePattern.LINE_PARSE_RULE_PATTERN.matcher(parseRule);
        if (matcher == null || !matcher.matches()) {
            return;
        }

        String ruleKey = matcher.getTokenPicked("ruleKey");
        String rule = matcher.getTokenPicked("rule");
        String namedRule = rule.replaceFirst("\\(", "(?<"+ EasyReadRegexMatcher.decoratePickerName("value")+">");
        EasyReadRegexBuilder ruleRegex = EasyReadRegexBuilder.newBuilder().regex(namedRule);

        String[] bodyLines = body.split("\n");
        for (int i = 0; i < bodyLines.length; i++) {
            matcher = ruleRegex.matcher(bodyLines[i].trim());
            if (matcher.matches()) {
                String value = matcher.getTokenPicked("value");
                context.getVelocityContext().put(ruleKey, value);
            }
        }
    }
}
