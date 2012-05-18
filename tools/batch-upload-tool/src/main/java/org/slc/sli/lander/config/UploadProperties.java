package org.slc.sli.lander.config;

public class UploadProperties {
    private String user;
    private String password;
    private String sftpServer;
    private String remoteDir;
    private String localDir;
    
    public UploadProperties(String user, String password, String sftpServer, String remoteDir, String localDir) {
        super();
        this.user = user;
        this.password = password;
        this.sftpServer = sftpServer;
        this.remoteDir = remoteDir;
        this.localDir = localDir;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getSftpServer() {
        return sftpServer;
    }
    
    public String getRemoteDir() {
        return remoteDir;
    }
    
    public String getLocalDir() {
        return localDir;
    }
    
}
