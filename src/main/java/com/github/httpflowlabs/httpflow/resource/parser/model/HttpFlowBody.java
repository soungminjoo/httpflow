package com.github.httpflowlabs.httpflow.resource.parser.model;

import lombok.Setter;

@Setter
public class HttpFlowBody {

    private StringBuilder stringBody;

    public String toString() {
        if (this.stringBody != null) {
            return this.stringBody.toString();
        }
        return "";
    }

    public void appendString(String text) {
        if (this.stringBody == null) {
            this.stringBody = new StringBuilder();
        }
        this.stringBody.append(text);
    }

    public void trimFinalLineBreak() {
        while (stringBody.length() > 0 && isLineBreak(stringBody.charAt(stringBody.length() - 1))) {
            stringBody.deleteCharAt(stringBody.length() - 1);
        }
    }

    private boolean isLineBreak(char charAt) {
        return charAt == '\n' || charAt == '\r';
    }

}
