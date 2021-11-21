package com.github.httpflowlabs.httpflow.resource.parser.preprocess;

import java.util.List;
import java.util.function.Consumer;

public class HttpFlowLines {

    private List<HttpFlowLine> lines;
    private int nextIndex;

    public HttpFlowLines(List<HttpFlowLine> lines) {
        this.lines = lines;
    }

    public boolean hasNextLine() {
        return nextIndex < lines.size();
    }

    public HttpFlowLine nextLine() {
        return lines.get(nextIndex++);
    }

    public HttpFlowLine preview() {
        return lines.get(nextIndex);
    }

    public void consumeLines(Consumer<HttpFlowLine> function, boolean stopOnEmptyLine) {
        while (this.hasNextLine()) {
            if (this.preview().isStatusLine()) {
                break;
            }

            HttpFlowLine currLine = this.nextLine();
            if (stopOnEmptyLine && currLine.isEmpty()) {
                break;
            }

            function.accept(currLine);
        }
    }

}
