package com.finest.app.utils;

import org.apache.commons.lang.StringUtils;

public class JsonFormat {

    public static String transStr(String jsonSting)
    {
        if(StringUtils.isEmpty(jsonSting))
        {
            return "";
        }
//        jsonSting = jsonSting.replaceAll("\\{","%7B");
//        jsonSting = jsonSting.replaceAll("\\}","%7D");
//        jsonSting = jsonSting.replaceAll("\"","%22");
        jsonSting = URIEncoder.encodeURIComponent(jsonSting);

        return jsonSting;
    }
}