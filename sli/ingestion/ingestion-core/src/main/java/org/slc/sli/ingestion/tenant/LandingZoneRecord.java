package org.slc.sli.ingestion.tenant;


/**
 * Container class for landing zone data inside tenants
 * 
 * @author jtully
 */
public class LandingZoneRecord {
	
	private String educationOrganizationId;
    private String server;
    private String ingestionServer;
    private String path;
    private String userName;
    private String desc;

    public String getEducationOrganizationId() {
        return educationOrganizationId;
    }
        
    public void setEducationOrganizationId(String district) {
        this.educationOrganizationId = district;
    }
    
    public String getServer() {
        return server;
    }
        
    public void setServer(String server) {
        this.server = server;
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
    
    public String getUserName() {
        return userName;
    }
        
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getDesc() {
        return desc;
    }
        
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
