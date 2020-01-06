package com.finest.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class HttpClientUtil {
    private Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig requestConfig;

    public String getString(String url,JSONObject jsonObject,String encoding) throws IOException {
        logger.info("INTERFACE-OUSITE-SERVER -> 内网接口报文:{}",jsonObject.toJSONString());
        String result = "";
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        //封装请求参数
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = null;
        try {
            for(String key:jsonObject.keySet())
            {
                if("file".equals(key))
                {
                    continue;
                }
                ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
                StringBody stringBody = new StringBody(jsonObject.get(key) + "",contentType);
                list.add(new BasicNameValuePair(key, jsonObject.get(key)+""));
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,Consts.UTF_8);

            //5、设置参数
            httpPost.setEntity(entity);

            response = httpClient.execute(httpPost);
            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), encoding);
            }
        } catch (IOException e) {
            logger.error("发送内网错误",e);
            result = (null==e.getCause()) ? e.getMessage() : e.getCause().getMessage();
            JSONObject obj = new JSONObject();
            obj.put("success","false");
            obj.put("errorCode",null);
            obj.put("errorMsg",result);
            result = obj.toJSONString();

        } finally {
            // 释放链接
            if(null!=response)
                response.close();
        }

        return result;
    }
    public String send(String url,MultipartFile[] files,JSONObject jsonObject,String path)
    {
        logger.info("INTERFACE-OUSITE-SERVER -> 内网接口报文:{}",jsonObject.toJSONString());
        String result = "";
        List<String> filenames = new ArrayList<String>();
        CloseableHttpResponse response = null;
        try
        {
            //每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
            String boundary = UUID.randomUUID().toString();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            //设置请求头
            httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
            //HttpEntity builder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //字符编码
            builder.setCharset(Charset.forName("UTF-8"));
            //模拟浏览器
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //boundary
            builder.setBoundary(boundary);

            if(null!=files)
            {
                for (int i = 0; i < files.length; i++) {
                    filenames.add(files[i].getName());
                    builder.addPart(jsonObject.getString("fileKey"), new FileBody(multipartFileToFile(files[i],path)));
                }
            }
            for(String key:jsonObject.keySet()){

                if("file".equals(key))
                {
                    continue;
                }
                ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
                StringBody stringBody = new StringBody(jsonObject.get(key) + "",contentType);
                builder.addPart(key, stringBody);

            }
            //HttpEntity
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            // 执行提交
            response = httpClient.execute(httpPost);
            //响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            }

        }catch (IOException e) {
            logger.error("发送内网错误",e);
            result = e.getCause().getMessage();
            JSONObject obj = new JSONObject();
            obj.put("success","false");
            obj.put("errorCode",null);
            obj.put("errorMsg",result);
            result = obj.toJSONString();
        } catch (Exception e) {
            logger.error("发送内网错误",e);
            result = e.getCause().getMessage();
            JSONObject obj = new JSONObject();
            obj.put("success","false");
            obj.put("errorCode",null);
            obj.put("errorMsg",result);
            result = obj.toJSONString();
        } finally {
            if(filenames.size() > 0)
            {
                for (int i = 0; i < filenames.size(); i++) {
                    FileUtils.deleteFile(path,filenames.get(i));
                }
            }
            try {
                if(null!=response)
                  response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static File multipartFileToFile(MultipartFile file,String path) throws Exception
    {
        File f = multipartFileToFile_(file,path);
        return f;
    }
    public static File multipartFileToFile_(MultipartFile file,String path) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(path,file.getName());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}