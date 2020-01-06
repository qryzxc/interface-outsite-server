package com.finest.app.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileTools {

    public static String getMethod(String requestBody)
    {
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        return jsonObject.getString("method");
    }

    public static String getFileKey(String requestBody)
    {
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        return jsonObject.getString("fileKey");
    }

    public static JSONObject getJSONObject(String requestBody)
    {
        return JSONObject.parseObject(requestBody);
    }

    public static Map<String,Object> getParams(JSONObject jsonObject,String path) throws IOException {
        Map<String,Object> map = new HashMap<String,Object>();
        MultipartFile[] files = null;
        String fileKey = jsonObject.getString("fileKey");
        if(StringUtils.isNotEmpty(fileKey))
        {
            map.put("fileKey",fileKey);
            String data = jsonObject.getString("file");
            JSONArray jsonlist = JSONArray.parseArray(data);
            files = new MultipartFile[jsonlist.size()];
            for (int i = 0; i < jsonlist.size(); i++) {
                String filename = jsonlist.getJSONObject(i).getString("filename")+"";
                String content = jsonlist.getJSONObject(i).getString("content");
                File file = FileUtils.base64ToFile(path,content,filename);
                InputStream inputStream = new FileInputStream(file);
                MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
                files[i] = multipartFile;
                //FileUtils.deleteFile(path,filename);
                //files[i] = FileUtils.base64ToMultipart(content);
            }
            map.put("file",files);

        }
        map.put("startTime",jsonObject.getString("startTime"));
        map.put("type",jsonObject.getString("type"));
        map.put("servletPath",jsonObject.getString("servletPath"));
        map.put("method",jsonObject.getString("method"));
        map.put("servletPath",jsonObject.getString("servletPath"));
        map.put("servletPath",jsonObject.getString("servletPath"));
        return map;
    }
}