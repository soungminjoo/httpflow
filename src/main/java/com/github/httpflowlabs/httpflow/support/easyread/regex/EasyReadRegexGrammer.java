package com.github.httpflowlabs.httpflow.support.easyread.regex;

import com.github.httpflowlabs.httpflow.support.easyread.regex.exception.EasyReadRegexException;
import com.github.httpflowlabs.httpflow.support.easyread.regex.support.ParenthesesOrEmptyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class EasyReadRegexGrammer extends EasyReadRegexSubBuilderSupport {

    /**
     * string : "String"
     *
     * @param str
     * @return
     */
    public CaseInsensitiveSupportRegexBuilder string(String str) {
        return string(str, null);
    }

    public CaseInsensitiveSupportRegexBuilder string(String[] strArray) {
        return string(strArray, null);
    }

    public CaseInsensitiveSupportRegexBuilder string(List<String> strArray) {
        return string(strArray.toArray(new String[0]), null);
    }

    public CaseInsensitiveSupportRegexBuilder string(String str, Object repeatCount) {
        processStrOrRegexPrivate(str, true, repeatCount, "string", true);
        return (CaseInsensitiveSupportRegexBuilder) this;
    }

    public CaseInsensitiveSupportRegexBuilder string(String[] strArray, Object repeatCount) {
        processStrOrRegexPrivate(strArray, true, repeatCount, "string", true);
        return (CaseInsensitiveSupportRegexBuilder) this;
    }

    public CaseInsensitiveSupportRegexBuilder string(List<String> strArray, Object repeatCount) {
        processStrOrRegexPrivate(strArray, true, repeatCount, "string", true);
        return (CaseInsensitiveSupportRegexBuilder) this;
    }

    /**
     * character : "Only one character"
     *
     * @param ch
     * @return
     */
    public EasyReadRegexBuilder character(char ch) {
        return character(ch, null);
    }

    public EasyReadRegexBuilder character(char[] charaters) {
        return character(charaters, null);
    }

    public EasyReadRegexBuilder character(String charaters) {
        return character(charaters, null);
    }

    public EasyReadRegexBuilder character(char ch, Object repeatCount) {
        processStrOrRegexPrivate(String.valueOf(ch), true, repeatCount, "character", true);
        return (EasyReadRegexBuilder) this;
    }

    public EasyReadRegexBuilder character(char[] charaters, Object repeatCount) {
        List<String> strArray = new ArrayList<>();
        for (int i = 0; i < charaters.length; i++) {
            strArray.add(String.valueOf(charaters[i]));
        }
        processStrOrRegexPrivate(strArray, true, repeatCount, "string", true);
        return (EasyReadRegexBuilder) this;
    }

    public EasyReadRegexBuilder character(String charaters, Object repeatCount) {
        if (charaters.length() != 1) {
            throw throwEasyReadRegexException(repeatCount, "character", "(\"" + charaters + "\", ");
        }
        return character(charaters.toCharArray()[0], repeatCount);
    }

    /**
     * regex : "Same with java.util.regex.Pattern.compile()"
     *
     * @param regex
     * @return
     */
    public EasyReadRegexBuilder regex(String regex) {
        return regex(regex, null);
    }

    public EasyReadRegexBuilder regex(String regex, Object repeatCount) {
        processStrOrRegexPrivate(regex, false, repeatCount, "regex", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * any : "."
     *
     * @return
     */
    public EasyReadRegexBuilder anychar() {
        return anychar(null);
    }

    public EasyReadRegexBuilder anychar(Object repeatCount) {
        return anychar(repeatCount, false);
    }

    public EasyReadRegexBuilder anychar(Object repeatCount, boolean greedy) {
        if (("*".equals(repeatCount) || "+".equals(repeatCount)) && !greedy) {
            repeatCount += "?";
        }
        processStrOrRegexPrivate(".", false, repeatCount, "anychar", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * whitechar : " ", "\t"
     *
     * @return
     */
    public EasyReadRegexBuilder whitechar() {
        return whitechar(null);
    }

    public EasyReadRegexBuilder whitechar(Object repeatCount) {
        processStrOrRegexPrivate("\\s", false, repeatCount, "whitechar", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * alphabet : "[a-zA-Z]"
     *
     * @return
     */
    public EasyReadRegexBuilder alphabet() {
        return alphabet(null);
    }

    public EasyReadRegexBuilder alphabet(Object repeatCount) {
        processStrOrRegexPrivate("[a-zA-Z]", false, repeatCount, "alphabet", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * digit : "\\d"
     *
     * @return
     */
    public EasyReadRegexBuilder digit() {
        return digit(null);
    }

    public EasyReadRegexBuilder digit(Object repeatCount) {
        processStrOrRegexPrivate("\\d", false, repeatCount, "digit", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * alphabet and digit : "[a-zA-Z0-9]"
     *
     * @return
     */
    public EasyReadRegexBuilder alphaDigit() {
        return alphaDigit(null);
    }

    public EasyReadRegexBuilder alphaDigit(Object repeatCount) {
        processStrOrRegexPrivate("[a-zA-Z0-9]", false, repeatCount, "alphaDigit", false);
        return (EasyReadRegexBuilder) this;
    }

    /**
     * identifier : "[_a-zA-Z][_a-zA-Z0-9]*" or "_[_a-zA-Z0-9]+"
     *
     * @return
     */
    public EasyReadRegexBuilder identifier() {
        return identifier(null);
    }

    public EasyReadRegexBuilder identifier(Object repeatCount) {
        processStrOrRegexPrivate("([a-zA-Z][_a-zA-Z0-9]*|_[_a-zA-Z0-9]+)", false, repeatCount, "identifier", false);
        return (EasyReadRegexBuilder) this;
    }

    public EasyReadRegexBuilder or() {
        regexPattern.append("|");
        return (EasyReadRegexBuilder) this;
    }

    /**
     * Returns regular expression string
     *
     * @return
     */
    @Override
    public String toString() {
        return regexPattern.toString();
    }

    private void processStrOrRegexPrivate(String str, boolean needQuote, Object repeatCount, String methodName, boolean caseIgnorable) {
        if (str.length() == 0) {
            throw throwEasyReadRegexException(repeatCount, methodName, "(\"\", ");
        }

        super.currentParenOrEmptyPair = new ParenthesesOrEmptyPair();
        super.currentParenOrEmptyPair.setCaseIgnorable(caseIgnorable);
        regexPattern.append(currentParenOrEmptyPair.getLeftParen())
                .append(getStrQuoted(needQuote, str))
                .append(currentParenOrEmptyPair.getRightParen());

        repeat(repeatCount);
    }

    private void processStrOrRegexPrivate(List<String> strArray, boolean needQuote, Object repeatCount, String methodName, boolean caseIgnorable) {
        processStrOrRegexPrivate(strArray.toArray(new String[0]), needQuote, repeatCount, methodName, caseIgnorable);
    }

    private void processStrOrRegexPrivate(String[] strArray, boolean needQuote, Object repeatCount, String methodName, boolean caseIgnorable) {
        if (strArray.length == 0) {
            throw throwEasyReadRegexException(repeatCount, methodName, "(empty String[], ");
        }

        super.currentParenOrEmptyPair = new ParenthesesOrEmptyPair();
        super.currentParenOrEmptyPair.setCaseIgnorable(caseIgnorable);
        regexPattern.append(currentParenOrEmptyPair.getLeftParen());

        for (int i = 0; i < strArray.length; i++) {
            regexPattern.append(getStrQuoted(needQuote, strArray[i]));
            if (i < strArray.length - 1) {
                this.or();
            }

        }

        regexPattern.append(currentParenOrEmptyPair.getRightParen());
        repeat(repeatCount);
    }

    private String getStrQuoted(boolean needQuote, String str) {
        return (needQuote) ? Pattern.quote(str) : str;
    }

    private EasyReadRegexException throwEasyReadRegexException(Object repeatCount, String methodName, String s) {
        String rcStr = (repeatCount instanceof String) ? repeatCount.toString() : String.valueOf(repeatCount);
        return new EasyReadRegexException("Invalid " + methodName + "() : " + methodName + s + rcStr + ")");
    }

}
