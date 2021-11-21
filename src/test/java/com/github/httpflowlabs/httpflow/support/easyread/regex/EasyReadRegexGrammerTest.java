package com.github.httpflowlabs.httpflow.support.easyread.regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class EasyReadRegexGrammerTest {

    @Test
    public void testStr() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string("regex");
        assertMatchesAllTrue(builder, "regex");
        assertMatchesAllFalse(builder, "_regex_");

        builder = EasyReadRegexBuilder.newBuilder()
                .string("regex", "*");
        assertMatchesAllTrue(builder, "regexregex", "");
        assertMatchesAllFalse(builder, "regexregex_");

        builder = EasyReadRegexBuilder.newBuilder()
                .string("a", "+");
        assertMatchesAllTrue(builder, "a", "aa");
        assertMatchesAllFalse(builder, "");
    }

    @Test
    public void testStr2() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string("a", 4);
        assertMatchesAllTrue(builder, "aaaa");
        assertMatchesAllFalse(builder, "aaa", "bbbb");

        builder = EasyReadRegexBuilder.newBuilder()
                .string("ab", 2);
        assertMatchesAllTrue(builder, "abab");
        assertMatchesAllFalse(builder, "ab");

        builder = EasyReadRegexBuilder.newBuilder()
                .string("ab", "2");
        assertMatchesAllTrue(builder, "abab");
        assertMatchesAllFalse(builder, "ab");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(".", 2);
        assertMatchesAllTrue(builder, "..");
        assertMatchesAllFalse(builder, "...", "11", "aa", "++");
    }

    @Test
    public void testStr3() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string(new String[]{"a", "b"}, 2);
        assertMatchesAllTrue(builder, "aa");
        assertMatchesAllTrue(builder, "ab");
        assertMatchesAllTrue(builder, "ba");
        assertMatchesAllTrue(builder, "bb");
        assertMatchesAllFalse(builder, "ac");

        builder = EasyReadRegexBuilder.newBuilder()
                .subBuilder(regex -> regex.string("a").or().string("b"), 2);
        assertMatchesAllTrue(builder, "aa");
        assertMatchesAllTrue(builder, "ab");
        assertMatchesAllTrue(builder, "ba");
        assertMatchesAllTrue(builder, "bb");
        assertMatchesAllFalse(builder, "ac");
    }

    @Test
    public void testStr4() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string("POST");
        assertMatchesAllTrue(builder, "POST");
        assertMatchesAllFalse(builder, "post");

        builder = EasyReadRegexBuilder.newBuilder()
                .string("POST").ignoreCase();
        assertMatchesAllTrue(builder, "POST");
        assertMatchesAllTrue(builder, "post");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(new String[]{"POST", "GET"});
        assertMatchesAllTrue(builder, "POST");
        assertMatchesAllTrue(builder, "GET");
        assertMatchesAllFalse(builder, "post");
        assertMatchesAllFalse(builder, "get");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(new String[]{"POST", "GET"}).ignoreCase();
        assertMatchesAllTrue(builder, "POST");
        assertMatchesAllTrue(builder, "GET");
        assertMatchesAllTrue(builder, "post");
        assertMatchesAllTrue(builder, "get");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(new String[]{"POST", "GET"}, 2).ignoreCase();
        assertMatchesAllTrue(builder, "POSTPOST");
        assertMatchesAllTrue(builder, "POSTpost");
        assertMatchesAllTrue(builder, "postPOST");
        assertMatchesAllTrue(builder, "postpost");
    }

    @Test
    public void testCharacter() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .character('a', 2);
        assertMatchesAllTrue(builder, "aa");
        assertMatchesAllFalse(builder, "a");

        builder = EasyReadRegexBuilder.newBuilder()
                .character("a", 2);
        assertMatchesAllTrue(builder, "aa");
        assertMatchesAllFalse(builder, "a");

        builder = EasyReadRegexBuilder.newBuilder()
                .character(new char[]{'a', 'b'}, 2);
        assertMatchesAllTrue(builder, "aa");
        assertMatchesAllTrue(builder, "ab");
        assertMatchesAllTrue(builder, "ba");
        assertMatchesAllTrue(builder, "bb");
        assertMatchesAllFalse(builder, "ac");
    }

    @Test
    public void testAny() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar();
        assertMatchesAllTrue(builder, "a", "1", ".", "=");
        assertMatchesAllFalse(builder, "", "aa", "11", "..", "==");
    }

    @Test
    public void testAny2() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .anychar("*");
        assertMatchesAllTrue(builder, "", "a", "ab", "1", "12", ".", ".3a?");

        builder = EasyReadRegexBuilder.newBuilder()
                .anychar("+");
        assertMatchesAllTrue(builder, "a", "ab", "1", "12", ".", ".3a?");
        assertMatchesAllFalse(builder, "");

        builder = EasyReadRegexBuilder.newBuilder()
                .anychar(2);
        assertMatchesAllTrue(builder, "aa", "ab", "12", "..", ".?");
        assertMatchesAllFalse(builder, "a", "124", ".");
    }

    @Test
    public void testOr() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .string("GET").or().string("POST").or().string("PUT").or().string("DELETE");
        assertMatchesAllTrue(builder, "GET", "POST", "PUT", "DELETE");
        assertMatchesAllFalse(builder, "OPTION");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(new String[]{"GET", "POST", "PUT", "DELETE"});
        assertMatchesAllTrue(builder, "GET", "POST", "PUT", "DELETE");
        assertMatchesAllFalse(builder, "OPTION");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(Arrays.asList(new String[]{"GET", "POST", "PUT", "DELETE"}));
        assertMatchesAllTrue(builder, "GET", "POST", "PUT", "DELETE");
        assertMatchesAllFalse(builder, "OPTION");
    }

    @Test
    public void testOr2() {
        EasyReadRegexBuilder subA = EasyReadRegexBuilder.newBuilder()
                .string("START").digit("*").string("END");

        EasyReadRegexBuilder subB = EasyReadRegexBuilder.newBuilder()
                .string("//").anychar("*");

        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .subBuilder(subA).or().subBuilder(subB);

        Assertions.assertTrue(builder.matcher("START12341231END").matches());
        Assertions.assertTrue(builder.matcher("START98765531END").matches());
        Assertions.assertTrue(builder.matcher("//comment line").matches());
        Assertions.assertFalse(builder.matcher("/*invalid comment line").matches());
    }

    @Test
    public void testRegex() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .regex(".");
        assertMatchesAllTrue(builder, "a", "1", ".", "=");

        builder = EasyReadRegexBuilder.newBuilder()
                .anychar();
        assertMatchesAllTrue(builder, "a", "1", ".", "=");

        builder = EasyReadRegexBuilder.newBuilder()
                .string(".");
        assertMatchesAllTrue(builder, ".");
        assertMatchesAllFalse(builder, "a", "1", "=");

        builder = EasyReadRegexBuilder.newBuilder()
                .regex(".*");
        assertMatchesAllTrue(builder, "", "a", "ab", "1", "12", ".", ".3a?");
    }

    @Test
    public void testAlias() {
        EasyReadRegexBuilder builder = EasyReadRegexBuilder.newBuilder()
                .whitechar();
        assertMatchesAllTrue(builder, " ");
        assertMatchesAllFalse(builder, "a");

        builder = EasyReadRegexBuilder.newBuilder()
                .alphabet();
        assertMatchesAllTrue(builder, "a", "z", "A", "Z");
        assertMatchesAllFalse(builder, "1", "0", "=");

        builder = EasyReadRegexBuilder.newBuilder()
                .digit();
        assertMatchesAllTrue(builder, "0", "1", "9");
        assertMatchesAllFalse(builder, "01", "a", "=");

        builder = EasyReadRegexBuilder.newBuilder()
                .alphaDigit();
        assertMatchesAllTrue(builder, "a", "z", "A", "Z", "0", "1", "9");
        assertMatchesAllFalse(builder, "=", "_", "$");

        builder = EasyReadRegexBuilder.newBuilder()
                .identifier();
        assertMatchesAllTrue(builder, "a", "Test1234", "_Test1234");
        assertMatchesAllFalse(builder, "1234", "$name", "_", "");
    }


    private void assertMatchesAllTrue(EasyReadRegexBuilder builder, String... targetStrList) {
        for (String targetStr : targetStrList) {
            EasyReadRegexMatcher matcher = builder.matcher(targetStr);
            Assertions.assertTrue(matcher.matches());
        }
    }

    private void assertMatchesAllFalse(EasyReadRegexBuilder builder, String... targetStrList) {
        for (String targetStr : targetStrList) {
            EasyReadRegexMatcher matcher = builder.matcher(targetStr);
            Assertions.assertFalse(matcher.matches());
        }
    }

}
