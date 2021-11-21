package com.github.httpflowlabs.httpflow.support.easyread.regex.support;

public class ParenthesesOrEmpty {

    private boolean isRightParen;
    private boolean isEmpty = true;
    private boolean isCaseInsensitive;

    public ParenthesesOrEmpty(boolean isRightParen) {
        this.isRightParen = isRightParen;
    }

    public void changeToParentheses() {
        this.isEmpty = false;
    }

    @Override
    public String toString() {
        if (this.isEmpty && !isCaseInsensitive) {
            return "";
        }
        if (isRightParen) {
            return ")";
        }
        return (isCaseInsensitive)? "((?i)" : "(";
    }

    public void setCaseInsensitive() {
        this.isCaseInsensitive = true;
    }
}
