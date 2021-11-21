package com.github.httpflowlabs.httpflow.support.easyread.regex;

import com.github.httpflowlabs.httpflow.support.easyread.regex.split.TwoSplitToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EasyReadRegexBuilderTest {

    @Test
    public void testSplitter() {
        TwoSplitToken splitter = EasyReadRegexBuilder.splitIntoTwo("Location: http://www.hello.com", ":");
        Assertions.assertEquals("Location", splitter.get(0));
        Assertions.assertEquals(" http://www.hello.com", splitter.get(1));
        Assertions.assertEquals("", splitter.get(2));
    }

    @Test
    public void testSplitter2() {
        TwoSplitToken splitter = EasyReadRegexBuilder.splitIntoTwo("Location: http://www.hello.com", ":");
        Assertions.assertEquals("Location", splitter.getLeft());
        Assertions.assertEquals(" http://www.hello.com", splitter.getRight());
    }

    @Test
    public void testSplitter3() {
        TwoSplitToken splitter = EasyReadRegexBuilder.splitIntoTwo("Location: http://www.hello.com", "\n");
        Assertions.assertEquals("Location: http://www.hello.com", splitter.get(0));
        Assertions.assertEquals("", splitter.get(1));
    }

    @Test
    public void testSplitter4() {
        TwoSplitToken splitter = EasyReadRegexBuilder.splitIntoTwo("a", "=");
        Assertions.assertEquals("a", splitter.get(0));
        Assertions.assertEquals("", splitter.get(1));
    }

}
