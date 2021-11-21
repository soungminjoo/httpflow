package com.github.httpflowlabs.httpflow.support.easyread.regex.support;

public class ParenthesesOrEmptyPair {

    private ParenthesesOrEmpty leftParen = new ParenthesesOrEmpty(false);
    private ParenthesesOrEmpty rightParen = new ParenthesesOrEmpty(true);
    private boolean caseIgnorable;

    public ParenthesesOrEmpty getLeftParen() {
        return leftParen;
    }

    public ParenthesesOrEmpty getRightParen() {
        return rightParen;
    }

    public void changeToParentheses() {
        this.leftParen.changeToParentheses();
        this.rightParen.changeToParentheses();
    }

    public void setCaseIgnorable(boolean caseIgnorable) {
        this.caseIgnorable = caseIgnorable;
    }

    public void ignoreCase() {
        if (this.caseIgnorable) {
            this.leftParen.setCaseInsensitive();
            this.rightParen.setCaseInsensitive();
        }
    }
}
