package com.github.httpflowlabs.httpflow.support.easyread.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasyReadRegexMatcher {

    private Pattern pattern;
    private String input;
    private Matcher matcher;
    private boolean matches;
    private Map<String, EasyReadRegexBuilder> tokenPickerMap;

    EasyReadRegexMatcher(String regex, String input, Map<String, EasyReadRegexBuilder> tokenPickerMap, Integer patternCompileFlag) {
        if (patternCompileFlag != null) {
            this.pattern = Pattern.compile(regex, patternCompileFlag);
        } else {
            this.pattern = Pattern.compile(regex);
        }
        this.input = input;
        this.matcher = pattern.matcher(input);
        this.matches = this.matcher.matches();
        this.tokenPickerMap = tokenPickerMap;
    }

    public boolean matches() {
        return this.matches;
    }

    public String getTokenPicked(int pickerName) {
        return this.getTokenPicked(String.valueOf(pickerName));
    }

    public String getTokenPicked(String pickerName) {
        if (this.matches) {
            try {
                String found = matcher.group(decoratePickerName(pickerName));
                if ("".equals(found)) {
                    return null;
                }
                return found;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public static String decoratePickerName(String pickerName) {
        return EasyReadRegexSubBuilderSupport.PICKER_PREFIX + pickerName;
    }

    public List<String> getTokenPickedAsList(String pickerName) {
        return this.getTokenPickedAsList(pickerName, null);
    }

    public List<String> getTokenPickedAsList(String pickerName, String delimiter) {
        List<String> list = new ArrayList<>();
        if (this.matches) {
            list.addAll(parseGroupStringToList(pickerName, delimiter));
        }
        return list;
    }

    private List<String> parseGroupStringToList(String pickerName, String delimiter) {
        try {
            EasyReadRegexBuilder pickerExpr = this.tokenPickerMap.get(pickerName);
            String groupStr = matcher.group(decoratePickerName(pickerName));

            List<String> list = new ArrayList<>();
            Pattern pattern = Pattern.compile("(?<" + EasyReadRegexMatcher.decoratePickerName(pickerName) + ">" + pickerExpr.toString() + ").*");
            Matcher matcher = pattern.matcher(groupStr);
            while (matcher.matches()) {
                String listItem = matcher.group(decoratePickerName(pickerName));
                if (delimiter != null && listItem.endsWith(delimiter)) {
                    list.add(listItem.substring(0, listItem.lastIndexOf(delimiter)));
                } else {
                    list.add(listItem);
                }

                groupStr = groupStr.substring(listItem.length());
                matcher = pattern.matcher(groupStr);
            }
            return list;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getInput() {
        return input;
    }

}
