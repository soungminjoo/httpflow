package com.github.httpflowlabs.httpflow.resource.parser.pattern;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.model.methods.HttpFlowControl;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexBuilder;

public class HttpFlowLinePattern {

    private static final String[] methodTypes = new String[]{"POST", "GET", "PUT", "DELETE"};
    private static final String[] controlTypes = HttpFlowControl.ALL_TYPES;

    public static final EasyReadRegexBuilder PROTOCOL_PATTERN = EasyReadRegexBuilder.newBuilder()
            .whitechar("*")
            .pick("protocol", regex -> {
                regex.string(HttpFlowProtocol.names()).ignoreCase();
            }).character("/").digit("+")
            .subBuilder(regex -> {
                regex.character(".").digit("+");
            }, "?");

    public static final EasyReadRegexBuilder STATUS_LINE_PATTERN = EasyReadRegexBuilder.newBuilder()
            .pick("method", regex -> {
                regex.string(methodTypes).ignoreCase();
            }).whitechar("+")
            .pick("url", regex -> {
                regex.anychar("+");
            })
            .subBuilder(PROTOCOL_PATTERN, "?");

    public static final EasyReadRegexBuilder HTTPFLOW_CONTROL_PATTERN = EasyReadRegexBuilder.newBuilder()
            .pick("method", regex -> {
                regex.string(controlTypes).ignoreCase();
            }).whitechar("+")
            .pick("param", regex -> {
                regex.anychar("+");
            })
            .subBuilder(PROTOCOL_PATTERN, "?");

    public static final EasyReadRegexBuilder HTTPFLOW_CONTROL_PATTERN_METHOD_ONLY = EasyReadRegexBuilder.newBuilder()
            .pick("method", regex -> {
                regex.string(controlTypes).ignoreCase();
            })
            .subBuilder(PROTOCOL_PATTERN, "?");

    public static final EasyReadRegexBuilder HEADER_LINE_PATTERN = EasyReadRegexBuilder.newBuilder()
            .pick("headerName", regex -> {
                regex.anychar("+");
            }).whitechar("*")
            .character(":").whitechar("*")
            .pick("headerValue", regex -> {
                regex.anychar("*");
            });

    public static final EasyReadRegexBuilder URL_WITH_SCHEME = EasyReadRegexBuilder.newBuilder()
            .string(new String[]{"http://", "https://"}).ignoreCase().anychar("*");

    public static final EasyReadRegexBuilder LINE_PARSE_RULE_PATTERN = EasyReadRegexBuilder.newBuilder()
            .whitechar("*")
            .pick("ruleKey", regex -> regex.identifier())
            .whitechar("*").string("USING").whitechar("*")
            .pick("rule", regex -> regex.anychar("*"));

}
