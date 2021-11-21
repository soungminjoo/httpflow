package com.github.httpflowlabs.httpflow.resource.parser.model;

public class HttpFlowConstants {

    public static final String HTTP_FLOW_CUSTOM_HEADER_PREFIX = "X-HTTPFLOW";

    public static final String HTTP_FLOW_ASSERT_STATUS = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-ASSERT-STATUS";
    public static final String HTTP_FLOW_ASSERT_HEADER = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-ASSERT-HEADER";

    public static final String HTTP_FLOW_BODY_MATCH_REGEX = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-MATCH-BODY-REGEX";
    public static final String HTTP_FLOW_BODY_MATCH_JSOUP = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-MATCH-BODY-JSOUP";

    public static final String HTTP_FLOW_IF = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-IF";

    public static final String COOKIE_STORE_ENABLED = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-Cookie-Store-Enabled";
    public static final String COOKIE_STORE_FIRST = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-Cookie-Store-First";
    public static final String CSRF_META_ENABLED = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-Csrf-Meta-Enabled";

    public static final String HTTP_FLOW_PRINT_VARIABLE = HTTP_FLOW_CUSTOM_HEADER_PREFIX + "-PRINT-VARIABLE";

}
