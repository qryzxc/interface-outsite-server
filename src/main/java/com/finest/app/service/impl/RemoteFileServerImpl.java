package com.finest.app.service.impl;

import com.finest.app.config.ParamsConfig;
import com.finest.app.service.RemoteFileServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoteFileServerImpl implements RemoteFileServer {

    @Autowired
    private ParamsConfig paramsConfig;

    @Override
    public boolean isImage(String fileSuff) {
        if(paramsConfig.getImageFormat().indexOf(fileSuff.toUpperCase()) >= 0)
        {
            return true;
        }
        return false;
    }
}