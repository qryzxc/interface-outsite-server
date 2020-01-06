package com.finest.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.finest.app.config.ParamsConfig;
import com.finest.app.service.ImageService;
import com.finest.app.utils.RemoteSourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    RemoteFileServerImpl remoteFileServerImpl;

    @Autowired
    ParamsConfig paramsConfig;

    @Override
    public String getBase64(Map<String,Object> map){
        String fileName = "";
        String base64 = "";
        try {
            String request_uri = (String)map.get("servletPath");
            String[] temps = request_uri.split("/");
            fileName = temps[temps.length-1];
            String url = paramsConfig.getImagePath() + request_uri;
            URL url_ = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url_.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream is = conn.getInputStream();
            base64 = RemoteSourcesUtils.InputStreamTobase64(is);
        } catch (FileNotFoundException e)
        {
            logger.info("文件[{}]不存在",fileName);
            base64 = "文件["+fileName+"]不存在";
            JSONObject obj = new JSONObject();
            obj.put("success","false");
            obj.put("errorCode",null);
            obj.put("errorMsg",base64);
            base64 = obj.toJSONString();
        } catch (Exception e) {
            logger.error("文件不存在或错误",e);
            base64 = e.getCause().getMessage();
            JSONObject obj = new JSONObject();
            obj.put("success","false");
            obj.put("errorCode",null);
            obj.put("errorMsg",base64);
            base64 = obj.toJSONString();
        }
        return base64;
    }
}