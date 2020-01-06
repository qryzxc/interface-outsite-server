package com.finest.app.utils;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

public class FileUtils {

    public static String getFileKey(HttpServletRequest request)
    {
        List<String> list = new ArrayList<String>();
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest)request;
        MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();

        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            list.add(key);
        }

        return list.size()>0 ? list.get(0) : "";
    }

    public static List<MultipartFile> getFiles(HttpServletRequest request)
    {
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest)request;
        MultiValueMap<String, MultipartFile> map = req.getMultiFileMap();
        List<MultipartFile> files = map.get(getFileKey(request));
        return files;
    }

    public static List<byte[]> getFileBytes(HttpServletRequest request) throws IOException {
        List<byte[]> list = new ArrayList<byte[]>();
        List<MultipartFile> files = getFiles(request);
        for (int i = 0; i < files.size(); i++) {
            MultipartFile multipartFile = files.get(i);
            byte[] bytes = multipartFile.getBytes();
            list.add(bytes);
        }
        return list;
    }

    /**
     * 文件转base64字符串
     * @param file
     * @return
     */
    public static  String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes=new byte[(int)file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
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
        return base64;
    }

    public static BASE64DecodedMultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            b = decoder.decodeBuffer(baseStrs[1]);

            for(int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new BASE64DecodedMultipartFile(b, baseStrs[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File base64ToFile(String destPath,String base64, String fileName)
    {
        File file = base64ToFile_(destPath,base64,fileName);
        return file;
    }

    public static void deleteFile(String destPath,String fileName)
    {
        File file =new File(destPath+"//"+fileName);
        file.delete();
    }

    public static File base64ToFile_(String destPath,String base64, String fileName) {
        File file = null;
        //创建文件目录
        String filePath=destPath;
        File  dir=new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            file=new File(filePath+"/"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
}