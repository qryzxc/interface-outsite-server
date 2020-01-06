package com.finest.app.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class Util {

    public static String getBaseUrl(HttpServletRequest request)
    {
        return request.getServletPath();
    }

    public static String getFieldValue(String requestBody,String fieldName)
    {
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        return jsonObject.getString(fieldName);
    }

    public static String getMappingUrl(HttpServletRequest request)
    {
        String baseUrl = request.getServletPath();
        baseUrl = baseUrl.replaceAll("/","");
        return baseUrl;
    }


}