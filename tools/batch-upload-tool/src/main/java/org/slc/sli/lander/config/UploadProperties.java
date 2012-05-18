package org.slc.sli.lander.config;

public class UploadProperties {
    private String user;
    private String password;
    private String sftpServer;
    private String localDir;
    private int port;
    
    public UploadProperties(String user, String password, String sftpServer, String localDir, int port) {
        super();
        this.user = user;
        this.password = password;
        this.sftpServer = sftpServer;
        this.localDir = localDir;
        this.port = port;
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
    
    public String getLocalDir() {
        return localDir;
    }
    
    public int getPort() {
        return port;
    }
    
}
