package com.github.httpflowlabs.httpflow.support.easyread.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class EasyReadRegexMatcherTest {

    @Test
    public void testTokenPicker() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*").string("name: ")
                .pick("name", regex -> regex.alphabet("+"))
                .anychar("*").string("number: ")
                .pick("number", regex -> regex.digit("+"))
                .anychar("*");

        String strToSearch = "[INFO]12:12 Hello world. name: Foo number: 1234 zipcode: 55444";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("Foo", matcher.getTokenPicked("name"));
        Assertions.assertEquals("1234", matcher.getTokenPicked("number"));
    }

    @Test
    public void testTokenPicker2() {
        EasyReadRegexBuilder inner = EasyReadRegexBuilder.newBuilder()
                .alphabet("+");
        EasyReadRegexBuilder inner2 = EasyReadRegexBuilder.newBuilder()
                .digit("+").string("-", "?");

        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*").string("name: ")
                .pick("name", inner)
                .anychar("*").string("number: ")
                .pick("number", inner2, "+")
                .anychar("*");

        String strToSearch = "[INFO]12:12 Hello world. name: Foo number: 1234-5678  zipcode: 55444";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("Foo", matcher.getTokenPicked("name"));
        Assertions.assertEquals("1234-5678", matcher.getTokenPicked("number"));
    }

    @Test
    public void testTokenPicker3() {
        EasyReadRegexBuilder inner = EasyReadRegexBuilder.newBuilder()
                .string("{id:").digit("+").string(",type:'").alphaDigit("*").string("'}").string(",", "?");

        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*?")
                .pick("items", inner, "+")
                .anychar("*");

        String strToSearch = "[INFO]12:12 {id:1,type:'foo'},{id:2,type:'bar'}";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("{id:1,type:'foo'},{id:2,type:'bar'}", matcher.getTokenPicked("items"));
    }

    @Test
    public void testTokenPicker4() {
        EasyReadRegexBuilder inner = EasyReadRegexBuilder.newBuilder()
                .string("{id:").digit("+").string(",type:'").alphaDigit("*").string("'}").string(",", "?");

        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*?")
                .pick("items", inner, "+")
                .anychar("*");

        String strToSearch = "[INFO]12:12 {id:1,type:'foo'},{id:2,type:'bar'}";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());

        List<String> items = matcher.getTokenPickedAsList("items");
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("{id:1,type:'foo'},", items.get(0));
        Assertions.assertEquals("{id:2,type:'bar'}", items.get(1));

        List<String> itemsNoDelimiter = matcher.getTokenPickedAsList("items", ",");
        Assertions.assertEquals(2, itemsNoDelimiter.size());
        Assertions.assertEquals("{id:1,type:'foo'}", itemsNoDelimiter.get(0));
        Assertions.assertEquals("{id:2,type:'bar'}", itemsNoDelimiter.get(1));
    }

    @Test
    public void testTokenPicker5() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string("COMMAND").whitechar("*")
                .pick("number", regex -> regex.digit("+"), "?");

        String strToSearch = "COMMAND 1";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
        Assertions.assertEquals("1", matcher.getTokenPicked("number"));

        strToSearch = "COMMAND";
        matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
        Assertions.assertNull(matcher.getTokenPicked("number"));
    }

    @Test
    public void testTokenSubBuilder() {
        EasyReadRegexBuilder inner = EasyReadRegexBuilder.newBuilder()
                .alphabet("+");
        EasyReadRegexBuilder inner2 = EasyReadRegexBuilder.newBuilder()
                .digit("+").string("-", "?");

        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*").string("name: ")
                .subBuilder(regex -> regex.alphabet("+"), "+")
                .anychar("*").string("number: ")
                .subBuilder(inner2, "+")
                .anychar("*");

        String strToSearch = "[INFO]12:12 Hello world. name: Foo bar number: 1234-5678  zipcode: 55444";
        EasyReadRegexMatcher matcher = builder.matcher(strToSearch);
        Assertions.assertTrue(matcher.matches());
    }

    @Test
    public void testPatternFlags() {
        EasyReadRegexBuilder pattern = EasyReadRegexBuilder.newBuilder(Pattern.DOTALL)
                .regex("(?m)SELECT.*");
        EasyReadRegexMatcher matcher = pattern.matcher("SELECT NAME \nFROM PETS");

        Assertions.assertTrue(matcher.matches());

        pattern = EasyReadRegexBuilder.newBuilder()
                .ignoreNewline()
                .regex("(?m)SELECT.*");
        matcher = pattern.matcher("SELECT NAME \nFROM PETS");

        Assertions.assertTrue(matcher.matches());
    }

}
