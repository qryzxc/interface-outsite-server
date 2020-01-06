package com.finest.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.finest.app.config.ParamsConfig;
import com.finest.app.service.UrlService;
import com.finest.app.utils.FileTools;
import com.finest.app.utils.JsonFormat;
import com.finest.app.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class UrlServiceImpl implements UrlService {

    @Autowired
    ParamsConfig paramsConfig;

    public String getUrl(Map<String,Object> map)
    {
        String serverPath = (String)map.get("servletPath");
        if(StringUtils.isEmpty(map.get("method")+""))
        {
            return getHost(serverPath) + serverPath;
        }
        return getHost(serverPath) + serverPath + "?method=" + map.get("method");
    }

    @Override
    public String transUrl(HttpServletRequest request,String jsonSting)
    {
        String baseUrl = Util.getBaseUrl(request);
        String fileKey = FileTools.getFileKey(jsonSting);
        String serverPath = Util.getFieldValue(jsonSting,"servletPath");
        if(StringUtils.isNotEmpty(fileKey))
        {
            return getHost(serverPath) + serverPath + "?method=" + FileTools.getMethod(jsonSting);
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonSting);

        String urlParams = "";
        String flag = "&";
        boolean existMethod = false;
        String method = "";

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String temp = entry.getValue()+"";
            temp = JsonFormat.transStr(temp);
            temp = entry.getKey() + "=" + temp;
            urlParams = urlParams + flag + temp;
            if("method".endsWith(entry.getKey()+""))
            {
                existMethod = true;
                method = entry.getValue()+"";
            }
        }

        if(existMethod)
        {
            urlParams = "?method=" + method + urlParams;
        }else
        {
            urlParams = "?" + urlParams.substring(1);
        }
        return getHost(serverPath) + serverPath + urlParams;
    }

    private String getHost(String baseUrl)
    {
        String host = "";
        Map<String, String> map = paramsConfig.getParamsMapping();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(baseUrl.indexOf(entry.getKey()+"")>=0)
            {
                host = entry.getValue();
                break;
            }
        }
        return host;
    }
}