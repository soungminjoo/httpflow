package com.github.httpflowlabs.httpflow.resource.parser.preprocess;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.pattern.HttpFlowLinePattern;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpFlowLine {

    private String text;
    private int number;

    private HttpFlowLine prev;
    private HttpFlowLine next;

    @Getter(AccessLevel.NONE)
    private EasyReadRegexMatcher statusLineMatcher;

    @Getter(AccessLevel.NONE)
    private EasyReadRegexMatcher httpFlowControlMatcher;

    @Getter(AccessLevel.NONE)
    private EasyReadRegexMatcher headerLineMatcher;


    public HttpFlowLine(String text, int number) {
        this.text = text;
        this.number = number;
    }

    public boolean isEmpty() {
        return HttpFlowUtils.isEmpty(text, true);
    }

    public boolean isComment() {
        return text.startsWith("--");
    }

    public boolean isHfdDelimiter() {
        return text.startsWith("---");
    }

    public boolean isStatusLine() {
        if (this.statusLineMatcher != null) {
            return true;
        }

        EasyReadRegexMatcher matcher = HttpFlowLinePattern.STATUS_LINE_PATTERN.matcher(text.trim());
        if (matcher.matches()) {
            this.statusLineMatcher = matcher;
        }
        return matcher.matches();
    }

    public String getMethod() {
        return this.statusLineMatcher.getTokenPicked("method");
    }

    public String getUrl() {
        return this.statusLineMatcher.getTokenPicked("url");
    }

    public String getProtocol() {
        return this.statusLineMatcher.getTokenPicked("protocol");
    }

    public boolean isHeaderLine() {
        if (this.headerLineMatcher != null) {
            return true;
        }

        EasyReadRegexMatcher matcher = HttpFlowLinePattern.HEADER_LINE_PATTERN.matcher(text.trim());
        if (matcher.matches()) {
            this.headerLineMatcher = matcher;
        }
        return matcher.matches();
    }

    public String getHeaderName() {
        String headerName = headerLineMatcher.getTokenPicked("headerName");
        if (headerName.startsWith(":" + HttpFlowConstants.HTTP_FLOW_CUSTOM_HEADER_PREFIX)) {
            headerName = headerName.substring(1);
        }
        return headerName;
    }

    public String getHeaderValue() {
        return headerLineMatcher.getTokenPicked("headerValue");
    }

    @Override
    public String toString() {
        return text + " (Line:" + number + ")";
    }

    public boolean isHttpFlowControl() {
        if (this.httpFlowControlMatcher != null) {
            return true;
        }

        EasyReadRegexMatcher matcher = HttpFlowLinePattern.HTTPFLOW_CONTROL_PATTERN.matcher(text.trim());
        if (matcher.matches()) {
            this.httpFlowControlMatcher = matcher;

        } else {
            matcher = HttpFlowLinePattern.HTTPFLOW_CONTROL_PATTERN_METHOD_ONLY.matcher(text.trim());
            if (matcher.matches()) {
                this.httpFlowControlMatcher = matcher;
            }
        }

        return matcher.matches();
    }

    public String getHttpFlowControlMethod() {
        return httpFlowControlMatcher.getTokenPicked("method");
    }

    public String getHttpFlowControlParams() {
        return httpFlowControlMatcher.getTokenPicked("param");
    }

    public String getHttpFlowControlProtocol() {
        String protocol = httpFlowControlMatcher.getTokenPicked("protocol");
        if (protocol == null) {
            protocol = HttpFlowProtocol.HTTPFLOW.name();
        }
        return protocol;
    }

}
