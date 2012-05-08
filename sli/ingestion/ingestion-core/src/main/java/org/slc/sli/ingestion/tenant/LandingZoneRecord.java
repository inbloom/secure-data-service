package org.slc.sli.ingestion.tenant;

import java.util.List;


/**
 * Container class for landing zone data inside tenants
 * 
 * @author jtully
 */
public class LandingZoneRecord {
    private String educationOrganization;
    private String ingestionServer;
    private String path;
    private List<String> userNames;
    private String desc;

    public String getEducationOrganization() {
        return educationOrganization;
    }
        
    public void setEducationOrganization(String educationOrganization) {
        this.educationOrganization = educationOrganization;
    }
    
    public String getIngestionServer() {
        return ingestionServer;
    }
        
    public void setIngestionServer(String ingestionServer) {
        this.ingestionServer = ingestionServer;
    }
    
    public String getPath() {
        return path;
    }
        
    public void setPath(String path) {
        this.path = path;
    }
    
    public List<String> getUserNames() {
        return userNames;
    }
        
    public void setUserName(List<String> userNames) {
        this.userNames = userNames;
    }
    
    public String getDesc() {
        return desc;
    }
        
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
