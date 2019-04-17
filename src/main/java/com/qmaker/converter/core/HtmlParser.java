package com.qmaker.converter.core;

import com.qmaker.core.entities.Questionnaire;

public interface HtmlParser {
    Questionnaire parse(String html);
}
