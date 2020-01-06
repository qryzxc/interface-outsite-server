package com.finest.app.config.aop;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSONObject;
import com.finest.app.config.ParamsConfig;
import com.finest.app.utils.FileTools;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 切面类
 */
@Aspect
@Component
public class ControllerAspect {

    private Logger logger= (Logger) LoggerFactory.getLogger(ControllerAspect.class);

    @Autowired
    ParamsConfig paramsConfig;

    /**
     * Pointcut定义切点
     * public修饰符的   返回值任意  com.cy.controller包下面的任意类的任意方法任意参数
     */
    @Pointcut( "execution(* com.finest.app.controller..*(..))")
    public void log(){

    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        try {
            Object[] temps = joinPoint.getArgs();
            JSONObject jsonObject = FileTools.getJSONObject((String)temps[1]);
            Map<String,Object> map = FileTools.getParams(jsonObject,paramsConfig.getUploadPath());
            MDC.put("THREAD_ID", (map.get("method") == null) ? "nomethod" : map.get("method")+"");
        } catch (IOException e) {
            logger.info("拦截出现异常,不影响业务",e);
        }
    }

    @After("log()")
    public void doAfter(JoinPoint joinPoint){
        MDC.remove("THREAD_ID");
    }

    @AfterReturning(returning="result", pointcut="log()")
    public void doAfterReturnint(Object result){
        logger.info("方法返回值：" + result);
    }
}