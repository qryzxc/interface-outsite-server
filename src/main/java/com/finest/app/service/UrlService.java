package com.finest.app.service;

import javax.servlet.http.HttpServletRequest;

public interface UrlService {
    String transUrl(HttpServletRequest request,String jsonString);
}