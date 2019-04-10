package com.qmaker.converter.core;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import istat.android.base.tools.ToolKits;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void decodeTopGradeJsonString() throws FileNotFoundException {
        String source = ToolKits.Stream.streamToString(new FileInputStream("/home/istat/Temp/TopGradePseudoJson.json"));
        String htmlStringUtilEscape = StringEscapeUtils.unescapeHtml4(source);
        String htmlStringUtilDecode = StringEscapeUtils.escapeHtml4(htmlStringUtilEscape);
        assertEquals(source, htmlStringUtilDecode);
    }
}