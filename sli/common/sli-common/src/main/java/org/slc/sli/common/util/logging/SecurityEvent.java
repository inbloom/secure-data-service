package org.slc.sli.common.util.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * class to represent a security event for logging
 *
 * @author dshaw
 *
 */
public class SecurityEvent {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityEvent.class);

    private static final String DELIMITER = ",";

    private String tenantId; // Alpha MH

    private String user;

    private String userEdOrg;

    //private String targetEdOrg;

    private List<String> targetEdOrgList;

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

//    public String getTargetEdOrg() {					//@TA10431
//        return targetEdOrg;							//@TA10431
//    }													//@TA10431

//    public void setTargetEdOrg(String targetEdOrg) {	//@TA10431
//        this.targetEdOrg = targetEdOrg;				//@TA10431
//    }     											//@TA10431

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

    public String getUserEdOrg() {
    	return this.userEdOrg;
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

    public List<String> getTargetEdOrgList() {
        return targetEdOrgList;
    }
    
    /**
     * Add the given educational organization string to the list of target
     * ed organizations for this security event.  Null or empty String has
     * no effect.
     * @param edOrg String representing the target educational organization
     */
    public void addTargetEdOrg(String edOrg) //@TA10431
    {
    	if ((edOrg != null) && !edOrg.equals(""))
    	{
    		if (targetEdOrgList == null) { targetEdOrgList = new ArrayList<String>(); }
    		targetEdOrgList.add(edOrg);
    	}
    }
    
    /**
     * Add all the given educational organization strings to the list of target
     * ed organizations for this security event.  Null or empty list has no effect.
     * @param edOrg Collection of String representing the target educational organizations
     */
    public void addAllTargetEdOrg(Collection<String> edOrgs) //@TA10431
    {
    	if ((edOrgs != null) && (edOrgs.size() > 0))
    	{
    		if (targetEdOrgList == null) { targetEdOrgList = new ArrayList<String>(); }
    		targetEdOrgList.addAll(edOrgs);
    	}
    }

    /**
     * Sets the targetEdOrgList to the given collection; any existing list is lost.  
     * If adding a list to the existing list is needed, see addTargetEdOrg. 
     * @param targetEdOrgList
     * @see SecurityEvent#addTargetEdOrg(Collection<String> edOrgs)
     */
    public void setTargetEdOrgList(Collection<String> newList) {
            targetEdOrgList = new ArrayList<String>();
            targetEdOrgList.addAll(newList);
    }
    
    /**
     * Sets the targetEdOrgList to the given collection; any existing list is lost.
     * If targetEdOrg is null, then targetEdOrgList is set to null  
     * If adding a list to the existing list is desired, see addTargetEdOrg. 
     * @param targetEdOrgList
     * @see SecurityEvent#addTargetEdOrg(Collection<String> edOrgs)
     */
    public void setTargetEdOrgList(String targetEdOrg) 
    {
    	if (targetEdOrg == null) 
    		{ targetEdOrgList = null; 
    		}
    	else 
    		{	targetEdOrgList = new ArrayList<String>();
            	targetEdOrgList.add(targetEdOrg);
    		}
    }
    
    /**
     * Resets the targetEdOrgList to null.
     */
    public void resetTargetEdOrgList()
    {
    	targetEdOrgList = null;
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
                //@TA10431
                //+ ((targetEdOrg == null) ? "" : targetEdOrg) + DELIMITER
                + ((targetEdOrgList == null) ? "" : targetEdOrgList) + DELIMITER
                + ((userEdOrg == null) ? "" : userEdOrg) + DELIMITER
                + ((user == null) ? "" : "<censored>") + DELIMITER
                + ((userOrigin == null) ? "" : userOrigin) + DELIMITER
                + ((credential == null) ? "" : "<censored>") + DELIMITER
                + ((actionUri == null) ? "" : actionUri) + DELIMITER
                + ((origin == null) ? "" : origin) + DELIMITER
                + ((roles == null) ? "" : roles) + DELIMITER
                + ((logMessage == null) ? "" : logMessage);

        return message;
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (tenantId != null && !tenantId.isEmpty()) {
            dataMap.put("tenantId", tenantId);
        }
        if (user != null && !user.isEmpty()) {
            dataMap.put("user", user);
        }
        //if (targetEdOrg != null && !targetEdOrg.isEmpty()) {
        //    dataMap.put("targetEdOrg", targetEdOrg);
        //}

        if (targetEdOrgList != null && !targetEdOrgList.isEmpty()) {
            dataMap.put("targetEdOrgList", targetEdOrgList);
        }

        if (userEdOrg != null && !userEdOrg.isEmpty()) {
            dataMap.put("userEdOrg", userEdOrg);
        }
 
        if (actionUri != null && !actionUri.isEmpty()) {
            dataMap.put("actionUri", actionUri);
        }
        if (appId != null && !appId.isEmpty()) {
            dataMap.put("appId", appId);
        }
        if (origin != null && !origin.isEmpty()) {
            dataMap.put("origin", origin);
        }
        if (executedOn != null && !executedOn.isEmpty()) {
            dataMap.put("executedOn", executedOn);
        }
        if (credential != null && !credential.isEmpty()) {
            dataMap.put("credential", credential);
        }
        if (userOrigin != null && !userOrigin.isEmpty()) {
            dataMap.put("userOrigin", userOrigin);
        }
        if (timeStamp != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(timeStamp);
            String now = DatatypeConverter.printDateTime(cal);
            //LOG.info("SECURITY EVENT TS: " + ts);

            dataMap.put("timeStamp", now);
        }
        if (processNameOrId != null && !processNameOrId.isEmpty()) {
            dataMap.put("processNameOrId", processNameOrId);
        }
        if (className != null && !className.isEmpty()) {
            dataMap.put("className", className);
        }
        if (logLevel != null) {
            dataMap.put("logLevel", logLevel.toString());
        }
        if (logMessage != null && !logMessage.isEmpty()) {
            dataMap.put("logMessage", logMessage);
        }
        if (roles != null && !roles.isEmpty()) {
            dataMap.put("roles", roles);
        }
        return dataMap;
    }
}
