package com.github.httpflowlabs.httpflow.support.easyread.regex.split;

import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;

public class TwoSplitToken {

    private EasyReadRegexMatcher matcher;

    public TwoSplitToken(EasyReadRegexMatcher matcher) {
        this.matcher = matcher;
    }

    public String get(int index) {
        String value = matcher.getTokenPicked(index);
        if (value == null) {
            return "";
        }
        return value;
    }

    public String getLeft() {
        return get(0);
    }

    public String getRight() {
        return get(1);
    }

}
