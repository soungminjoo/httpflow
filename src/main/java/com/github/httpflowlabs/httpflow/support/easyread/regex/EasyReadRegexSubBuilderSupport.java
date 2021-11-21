package com.github.httpflowlabs.httpflow.support.easyread.regex;

import com.github.httpflowlabs.httpflow.support.easyread.regex.exception.EasyReadRegexException;
import com.github.httpflowlabs.httpflow.support.easyread.regex.picker.TokenPicker;
import com.github.httpflowlabs.httpflow.support.easyread.regex.support.DelayedStringBuilder;
import com.github.httpflowlabs.httpflow.support.easyread.regex.support.ParenthesesOrEmptyPair;
import com.github.httpflowlabs.httpflow.support.easyread.regex.support.RepeatCount;

import java.util.HashMap;
import java.util.Map;

abstract class EasyReadRegexSubBuilderSupport {

    public static final String PICKER_PREFIX = "PICKER";

    protected DelayedStringBuilder regexPattern = new DelayedStringBuilder();
    protected ParenthesesOrEmptyPair currentParenOrEmptyPair;
    protected Map<String, EasyReadRegexBuilder> tokenPickerMap = new HashMap<>();

    public EasyReadRegexBuilder pick(String pickerName, TokenPicker tokenPicker) {
        return this.pick(pickerName, tokenPicker, null);
    }
    public EasyReadRegexBuilder pick(String pickerName, TokenPicker tokenPicker, Object repeatCount) {
        checkRepeatablePickValid(repeatCount);

        EasyReadRegexBuilder pickerRegex = new CaseInsensitiveSupportRegexBuilder();
        tokenPicker.setUpPickerRegex(pickerRegex);
        return pickInternal(pickerName, repeatCount, pickerRegex);
    }

    public EasyReadRegexBuilder pick(String pickerName, EasyReadRegexBuilder builder) {
        return this.pick(pickerName, builder, null);
    }
    public EasyReadRegexBuilder pick(String pickerName, EasyReadRegexBuilder pickerRegex, Object repeatCount) {
        checkRepeatablePickValid(repeatCount);

        return pickInternal(pickerName, repeatCount, pickerRegex);
    }

    private EasyReadRegexBuilder pickInternal(String pickerName, Object repeatCount, EasyReadRegexBuilder pickerRegex) {
        EasyReadRegexBuilder thisAsBuilder = (EasyReadRegexBuilder) this;
        this.tokenPickerMap.put(pickerName, pickerRegex);

        regexPattern.append("(");
        if (pickerName != null) {
            regexPattern.append("?<" + EasyReadRegexMatcher.decoratePickerName(pickerName) + ">");
        }

        this.currentParenOrEmptyPair = new ParenthesesOrEmptyPair();
        regexPattern.append(currentParenOrEmptyPair.getLeftParen())
                .append(pickerRegex)
                .append(currentParenOrEmptyPair.getRightParen());

        repeat(repeatCount);

        regexPattern.append(")");
        return thisAsBuilder;
    }

    protected EasyReadRegexBuilder repeat(Object cnt) {
        RepeatCount rc = RepeatCount.valueOf(cnt);
        if (rc != null && !rc.isOne()) {
            currentParenOrEmptyPair.changeToParentheses();
            regexPattern.append(rc.toString());
        }
        return (EasyReadRegexBuilder) this;
    }

    public EasyReadRegexBuilder subBuilder(TokenPicker tokenPicker) {
        return subBuilder(tokenPicker, 1);
    }
    public EasyReadRegexBuilder subBuilder(TokenPicker tokenPicker, Object repeatCount) {
        return this.pick(null, tokenPicker, repeatCount);
    }
    public EasyReadRegexBuilder subBuilder(EasyReadRegexBuilder builder) {
        return subBuilder(builder, 1);
    }
    public EasyReadRegexBuilder subBuilder(EasyReadRegexBuilder builder, Object repeatCount) {
        return this.pick(null, builder, repeatCount);
    }

    private void checkRepeatablePickValid(Object repeatCount) {
        if (repeatCount != null && !"?".equals(repeatCount) && regexPattern.toString().endsWith("*") && !regexPattern.toString().endsWith("\\*")) {
            throw new EasyReadRegexException("any(\"*\") preceding repeatable pick() must be Non-greedy. Use any(\"*?\") instead.");
        }
    }

}
