package com.finest.app.params;

public class ResponseParam {

    private String code;
    private String invokTime;
    private String message;
    private Descript serviceDescript;
    private String timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInvokTime() {
        return invokTime;
    }

    public void setInvokTime(String invokTime) {
        this.invokTime = invokTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Descript getServiceDescript() {
        return serviceDescript;
    }

    public void setServiceDescript(Descript serviceDescript) {
        this.serviceDescript = serviceDescript;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}