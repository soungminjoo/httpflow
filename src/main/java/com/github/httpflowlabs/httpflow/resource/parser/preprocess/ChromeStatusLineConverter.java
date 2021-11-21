package com.github.httpflowlabs.httpflow.resource.parser.preprocess;

import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexBuilder;
import com.github.httpflowlabs.httpflow.support.easyread.regex.EasyReadRegexMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChromeStatusLineConverter {

    private static final String AUTHORITY = ":authority";
    public static final String METHOD = ":method";
    public static final String PATH = ":path";
    public static final String SCHEME = ":scheme";

    private EasyReadRegexBuilder statusLinePattern = EasyReadRegexBuilder.newBuilder()
            .pick("name", regex -> {
                regex.string(new String[]{AUTHORITY, METHOD, PATH, SCHEME}).ignoreCase();
            })
            .whitechar("*")
            .character(":").whitechar("*")
            .pick("value", regex -> {
                regex.anychar("*");
            });

    public HttpFlowLine[] convert(String[] lines) {
        List<HttpFlowLine> converted = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            EasyReadRegexMatcher matcher = statusLinePattern.matcher(lines[i].trim());
            if (matcher.matches()) {
                Map<String, String> statusLineTokens = new HashMap<>();
                int originalIndex = i;
                i += collectStatusMatchers(statusLineTokens, matcher, lines, i + 1);
                converted.add(new HttpFlowLine(mergeToSingleStatusLine(statusLineTokens), originalIndex + 1));
            } else {
                HttpFlowLine line = new HttpFlowLine(trimIfLineIsEmpty(lines[i]), i + 1);
                converted.add(line);
            }
        }

        return converted.toArray(new HttpFlowLine[0]);
    }

    private String trimIfLineIsEmpty(String line) {
        if ("".equals(line.trim())) {
            line = line.trim();
        }
        return line;
    }

    private String mergeToSingleStatusLine(Map<String, String> statusLineToken) {
        StringBuilder builder = new StringBuilder();
        builder.append(statusLineToken.get(METHOD)).append(" ")
                .append(statusLineToken.get(SCHEME)).append("://")
                .append(statusLineToken.get(AUTHORITY))
                .append(statusLineToken.get(PATH));
        return builder.toString();
    }

    private int collectStatusMatchers(Map<String, String> statusLineTokens, EasyReadRegexMatcher matcher, String[] lines, int startIdx) {
        statusLineTokens.put(matcher.getTokenPicked("name").trim(), matcher.getTokenPicked("value").trim());

        int count = 0;
        for (int i = startIdx; i < lines.length; i++) {
            String line = lines[i];
            matcher = statusLinePattern.matcher(line.trim());
            if (matcher.matches()) {
                statusLineTokens.put(matcher.getTokenPicked("name").trim(), matcher.getTokenPicked("value").trim());
                count++;
            } else {
                break;
            }
        }
        return count;
    }

}
