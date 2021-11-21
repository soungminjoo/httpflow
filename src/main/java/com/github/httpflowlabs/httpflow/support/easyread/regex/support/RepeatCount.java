package com.github.httpflowlabs.httpflow.support.easyread.regex.support;

import com.github.httpflowlabs.httpflow.support.easyread.regex.exception.EasyReadRegexException;

public class RepeatCount {

    private String repeatCount = "";
    private int count;

    public static RepeatCount valueOf(Object expr) {
        if (expr == null) {
            return null;
        }
        return new RepeatCount(expr);
    }

    public RepeatCount(Object expr) {
        if (expr instanceof String) {
            if ("*".equals(expr) || "*?".equals(expr) || "+?".equals(expr) || "+".equals(expr) || "?".equals(expr)) {
                this.repeatCount = (String) expr;
            } else {
                boolean isIntegerExpr = setIntegerRepeatCount((String) expr);
                if (isIntegerExpr) {
                    return;
                }
                throw new EasyReadRegexException("Invalid repeatCount : " + expr);
            }

        } else if (expr instanceof Integer) {
            setIntegerRepeatCount((Integer) expr);

        } else {
            throw new EasyReadRegexException("Invalid repeatCount : " + expr);
        }
    }

    public boolean isOne() {
        return this.count == 1;
    }

    private void setIntegerRepeatCount(int count) {
        this.count = count;
        if (count == 0) {
            throw new EasyReadRegexException("RepeatCount cannot be zero.");
        } else if (count != 1) {
            repeatCount = "{" + count + "}";
        }
    }

    private boolean setIntegerRepeatCount(String expr) {
        try {
            setIntegerRepeatCount(Integer.parseInt(expr));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return repeatCount;
    }
}
