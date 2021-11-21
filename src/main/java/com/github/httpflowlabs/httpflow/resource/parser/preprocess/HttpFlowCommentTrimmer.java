package com.github.httpflowlabs.httpflow.resource.parser.preprocess;

import java.util.ArrayList;
import java.util.List;

public class HttpFlowCommentTrimmer {

    public List<HttpFlowLines> trimComment(HttpFlowLine[] lines) {
        List<HttpFlowLines> linesList = new ArrayList<>();
        List<HttpFlowLine> newLines = new ArrayList<>();
        HttpFlowLine prevHttpFlowLine = new HttpFlowLine("Dummy First Line", -1);

        for (int i = 0; i < lines.length; i++) {
            HttpFlowLine currHttpFlowLine = lines[i];

            if (currHttpFlowLine.isHfdDelimiter()) {
                linesList.add(new HttpFlowLines(newLines));
                newLines = new ArrayList<>();
                continue;

            } else if (currHttpFlowLine.isComment() || (i < lines.length - 1 && prevHttpFlowLine.isEmpty() && currHttpFlowLine.isEmpty())) {
                continue;
            }

            if (prevHttpFlowLine != null) {
                prevHttpFlowLine.setNext(currHttpFlowLine);
                currHttpFlowLine.setPrev(prevHttpFlowLine);
            }
            prevHttpFlowLine = currHttpFlowLine;
            newLines.add(currHttpFlowLine);
        }

        linesList.add(new HttpFlowLines(newLines));
        return linesList;
    }

    private String trimIfLineIsEmpty(String line) {
        if ("".equals(line.trim())) {
            line = line.trim();
        }
        return line;
    }
    
}
