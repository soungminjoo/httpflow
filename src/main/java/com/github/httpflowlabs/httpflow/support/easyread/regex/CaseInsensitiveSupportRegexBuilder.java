package com.github.httpflowlabs.httpflow.support.easyread.regex;

public class CaseInsensitiveSupportRegexBuilder extends EasyReadRegexBuilder {

    public CaseInsensitiveSupportRegexBuilder() {
    }

    public CaseInsensitiveSupportRegexBuilder(int patternCompileFlag) {
        super(patternCompileFlag);
    }

    public EasyReadRegexBuilder ignoreCase() {
        super.currentParenOrEmptyPair.ignoreCase();
        return this;
    }
}
