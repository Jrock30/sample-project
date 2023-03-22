package com.sample.project.common.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;

@RequiredArgsConstructor
public class JsonEscapeUtils {

    public static String JsonHtmlEscape(String content){
        return StringEscapeUtils.escapeHtml4(content);
    }

    public static String JsonHtmlUnEscape(String content){
        return StringEscapeUtils.unescapeHtml4(content);
    }

}
