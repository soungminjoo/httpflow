package com.github.httpflowlabs.httpflow.support.easyread.regex;

import com.github.httpflowlabs.httpflow.support.easyread.regex.split.TwoSplitToken;

import java.util.regex.Pattern;

public class EasyReadRegexBuilder extends EasyReadRegexGrammer {

    private Integer patternCompileFlag;

    EasyReadRegexBuilder() {
    }

    public EasyReadRegexBuilder(int patternCompileFlag) {
        this.patternCompileFlag = patternCompileFlag;
    }

    public static EasyReadRegexBuilder newBuilder() {
        return new CaseInsensitiveSupportRegexBuilder();
    }

    public static EasyReadRegexBuilder newBuilder(int patternCompileFlag) {
        return new CaseInsensitiveSupportRegexBuilder(patternCompileFlag);
    }

    public static TwoSplitToken splitIntoTwo(String input, String delimeter) {
        if (input.indexOf(delimeter) == -1) {
            input += delimeter;
        }
        EasyReadRegexMatcher matcher = EasyReadRegexBuilder.newBuilder()
                .pick("0", regex -> regex.anychar("*?"))
                .string(delimeter)
                .pick("1", regex -> regex.anychar("*"))
                .matcher(input);
        return new TwoSplitToken(matcher);
    }

    public EasyReadRegexBuilder ignoreNewline() {
        if (this.patternCompileFlag != null) {
            this.patternCompileFlag |= Pattern.DOTALL;
        } else {
            this.patternCompileFlag = Pattern.DOTALL;
        }
        return this;
    }

    public EasyReadRegexMatcher matcher(String input) {
        if (input == null) {
            return null;
        }
        return new EasyReadRegexMatcher(super.regexPattern.toString(), input, super.tokenPickerMap, patternCompileFlag);
    }

}
