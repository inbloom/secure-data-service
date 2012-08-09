/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
