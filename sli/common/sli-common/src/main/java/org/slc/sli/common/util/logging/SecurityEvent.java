/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.common.util.logging;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

/**
 * class to represent a security event for logging
 *
 * @author dshaw
 *
 */
public class SecurityEvent {

    private static final String DELIMITER = ",";

    private String tenantId; // Alpha MH

    private String user;

    private String userEdOrg;

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

    private LogLevelType logLevel; // Alpha MH

    private String logMessage; // Alpha MH

    private List<String> roles;

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
    
    public void setUserEdOrg(String userEdOrg) {
        this.userEdOrg = userEdOrg;
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

    public LogLevelType getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevelType logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        String message;

        message = ((timeStamp == null) ? "" : timeStamp) + DELIMITER
                + ((processNameOrId == null) ? "" : processNameOrId) + DELIMITER
                + ((logLevel == null) ? LogLevelType.TYPE_INFO.getName() : logLevel.getName()) + DELIMITER
                + ((executedOn == null) ? "" : executedOn) + DELIMITER
                + ((appId == null) ? "" : appId) + DELIMITER
                + ((className == null) ? "" : className) + DELIMITER
                + ((tenantId == null) ? "" : tenantId) + DELIMITER
                + ((targetEdOrg == null) ? "" : targetEdOrg) + DELIMITER
                + ((userEdOrg == null) ? "" : userEdOrg) + DELIMITER
                + ((user == null) ? "" : user) + DELIMITER
                + ((userOrigin == null) ? "" : userOrigin) + DELIMITER
                + ((credential == null) ? "" : credential) + DELIMITER
                + ((actionUri == null) ? "" : actionUri) + DELIMITER
                + ((origin == null) ? "" : origin) + DELIMITER
                + ((roles == null) ? "" : roles) + DELIMITER
                + ((logMessage == null) ? "" : logMessage);

        return message;
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (tenantId != null) {
            dataMap.put("tenantId", tenantId);
        }
        if (user != null) {
            dataMap.put("user", user);
        }
        if (targetEdOrg != null) {
            dataMap.put("targetEdOrg", targetEdOrg);
        }
        
        if (userEdOrg != null) {
            dataMap.put("userEdOrg", userEdOrg);
        }
 
        if (actionUri != null) {
            dataMap.put("actionUri", actionUri);
        }
        if (appId != null) {
            dataMap.put("appId", appId);
        }
        if (origin != null) {
            dataMap.put("origin", origin);
        }
        if (executedOn != null) {
            dataMap.put("executedOn", executedOn);
        }
        if (credential != null) {
            dataMap.put("credential", credential);
        }
        if (userOrigin != null) {
            dataMap.put("userOrigin", userOrigin);
        }
        if (timeStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timeStamp);
            dataMap.put("timeStamp", DatatypeConverter.printDateTime(cal));
        }
        if (processNameOrId != null) {
            dataMap.put("processNameOrId", processNameOrId);
        }
        if (className != null) {
            dataMap.put("className", className);
        }
        if (logLevel != null) {
            dataMap.put("logLevel", logLevel.toString());
        }
        if (logMessage != null) {
            dataMap.put("logMessage", logMessage);
        }
        if (roles != null) {
            dataMap.put("roles", roles);
        }
        return dataMap;
    }
}
