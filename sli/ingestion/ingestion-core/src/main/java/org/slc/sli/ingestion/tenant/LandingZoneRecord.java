package org.slc.sli.ingestion.tenant;

/**
 * Container class for landing zone data inside tenants
 * 
 * @author jtully
 */
public class LandingZoneRecord {
    private String district;
    private String server;
    private String ingestionServer;
    private String path;
    private String userName;
    private String desc;

    public String getDistrict() {
        return district;
    }
        
    public void setDistrict(String district) {
        this.district = district;
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
