package com.finest.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.finest.app.config.ParamsConfig;
import com.finest.app.params.Descript;
import com.finest.app.params.ResponseParam;
import com.finest.app.service.impl.ImageServiceImpl;
import com.finest.app.service.impl.UrlServiceImpl;
import com.finest.app.utils.FileTools;
import com.finest.app.utils.HttpClientUtil;
import com.finest.app.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(value = "/terminal")
public class TerminalControllerByAll {

    private Logger logger = LoggerFactory.getLogger(TerminalControllerByAll.class);

    @Autowired
    HttpClientUtil httpClientUtil;

    @Autowired
    UrlServiceImpl urlServiceImpl;

    @Autowired
    ImageServiceImpl imageServiceImpl;

    @Autowired
    ParamsConfig paramsConfig;

    @RequestMapping(value = "test.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public String test(HttpServletRequest request)
    {
        return paramsConfig.getUploadPath();
    }

    @RequestMapping(value = "business.do",method = RequestMethod.POST)
    @ResponseBody
    public String process(HttpServletRequest request,@RequestBody String requestBody){
        String result = "";
        Long endTime = System.currentTimeMillis();
        String startTime = "";
        try {
            JSONObject jsonObject = FileTools.getJSONObject(requestBody);
            Map<String,Object> map = FileTools.getParams(jsonObject, paramsConfig.getUploadPath());
            startTime = map.get("startTime")+"";
            logger.info("二类网 -> 三类网 -> INTERFACE-OUSITE-SERVER共耗时:" + (endTime - Long.parseLong(startTime)) + " ms");
            //MultipartFile[] files = FileTools.getFiles(requestBody, paramsConfig.getUploadPath());
            result = send(jsonObject,map);
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
    }

    public String send(JSONObject jsonObject,Map<String,Object> map) throws IOException {
        String result = "";
        logger.info("");
        logger.info("三类网 -> INTERFACE-OUSITE-SERVER报文:{}", jsonObject.toJSONString());
        Long startTime = System.currentTimeMillis();
        String type = map.get("type")+"";
        if("image".equals(type))
        {
            result = imageServiceImpl.getBase64(map);
        }else
        {
            String url = urlServiceImpl.getUrl(map);

            if(map.get("file") == null)
            {
                result = httpClientUtil.getString(url,jsonObject,"utf-8");
            }else
            {
                MultipartFile[] files = (MultipartFile[])map.get("file");
                result = httpClientUtil.send(url, files, jsonObject, paramsConfig.getUploadPath());
            }
        }
        Long endTime = System.currentTimeMillis();
        logger.info("INTERFACE-OUSITE-SERVER -> 内网接口 -> INTERFACE-OUSITE-SERVER共耗时:" + (endTime - startTime)  + " ms");
        logger.info("内网接口 -> INTERFACE-OUSITE-SERVER报文:{}", result);

        if(StringUtils.isNotEmpty(result))
        {
            if(!"image".equals(type))
            {
                JSONObject obj = JSONObject.parseObject(result);
                obj.put("startTime",startTime+"");
                result = obj.toJSONString();
            }

        }else
        {
            JSONObject obj = new JSONObject();
            obj.put("startTime",startTime+"");
            result = obj.toJSONString();
        }
        ResponseParam rsp = new ResponseParam();
        Descript descript_rsp = new Descript();
        descript_rsp.setData(result);

        rsp.setServiceDescript(descript_rsp);
        rsp.setCode("SS1000");
        rsp.setInvokTime((endTime - startTime)+"");
        rsp.setMessage("服务请求成功");
        rsp.setTimestamp(new Date().getTime() + "");

        String res = JSON.toJSONString(rsp);

        logger.info("INTERFACE-OUSITE-SERVER -> 三类网报文:{}",res);
        logger.info("");
        return res;
    }
}