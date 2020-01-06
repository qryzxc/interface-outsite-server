package com.finest.app.utils;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RemoteSourcesUtils {

    public static String InputStreamTobase64(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.encodeBase64(data));
    }

    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            byte[] bytes = decoder.decodeBuffer(base64);
            stream = new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream;
    }
}