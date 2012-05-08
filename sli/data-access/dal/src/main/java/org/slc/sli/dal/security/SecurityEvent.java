package org.slc.sli.dal.security;

import java.util.Date;

public class SecurityEvent {
    
    private String tenantId; // Alpha MH

    private String user;

    private String targetEdOrg;

    private String actionUri; // Alpha MH

    private String appId; // Alpha MH

    private String origin;

    private String executedOn;

    private String credential; // Alpha MH

    private String userOrigin;

    private Date timeStamp; // Alpha MH
    
    private String processNameOrId;
    
    private String className;
    
    private String logLevel; // Alpha MH
    
    private String logMessage; // Alpha MH

    public SecurityEvent() {
    }

    public SecurityEvent(String tenantId, String user, String targetEdOrg, String actionUri, 
            String appId, String origin, String executedOn, String credential, String userOrigin, 
            Date timeStamp, String processNameOrId, String className, String logLevel, String logMessage) {
        this.tenantId = tenantId;
        this.user = user;
        this.targetEdOrg = targetEdOrg;
        this.actionUri = actionUri;
        this.appId = appId;
        this.origin = origin;
        this.executedOn = executedOn;
        this.credential = credential;
        this.userOrigin = userOrigin;
        this.timeStamp = timeStamp;
        this.processNameOrId = processNameOrId;
        this.className = className;
        this.logLevel = logLevel;
        this.logMessage = logMessage;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTargetEdOrg() {
        return targetEdOrg;
    }

    public void setTargetEdOrg(String targetEdOrg) {
        this.targetEdOrg = targetEdOrg;
    }

    public String getActionUri() {
        return actionUri;
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getExecutedOn() {
        return executedOn;
    }

    public void setExecutedOn(String executedOn) {
        this.executedOn = executedOn;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getUserOrigin() {
        return userOrigin;
    }

    public void setUserOrigin(String userOrigin) {
        this.userOrigin = userOrigin;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProcessNameOrId() {
        return processNameOrId;
    }

    public void setProcessNameOrId(String processNameOrId) {
        this.processNameOrId = processNameOrId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
    
    public String write(){
        String message;
        
        message = tenantId + "," +
                user + "," +
                targetEdOrg + "," +
                actionUri + "," +
                appId + "," +
                origin + "," +
                executedOn + "," +
                credential + "," +
                userOrigin + "," +
                timeStamp + "," +
                processNameOrId + "," +
                className + "," +
                logLevel + "," +
                logMessage;
         
        return message;
    }

}
