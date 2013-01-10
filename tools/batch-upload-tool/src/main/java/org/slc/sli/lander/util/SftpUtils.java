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


package org.slc.sli.lander.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.slc.sli.lander.config.UploadProperties;

public class SftpUtils {
    
    private static final int TIMEOUT = 10000;
    private static final String TEMP_REMOTE_FILENAME = "ingestion-upload.zip.tmp";
    private static final String FINAL_REMOTE_FILENAME = "ingestion-upload.zip";
    
    public static void landFile(File fileToLand, UploadProperties properties) throws Exception {
        JSch jsch = new JSch();
        
        String host = properties.getSftpServer();
        String user = properties.getUser();
        String password = properties.getPassword();
        int port = properties.getPort();
        
        Session session = jsch.getSession(user, host, port);
        session.setOutputStream(System.out);
        
        session.setPassword(password);
        
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(TIMEOUT);
        
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp c = (ChannelSftp) channel;
        
        // delete any existing file with the target filename, so that rename will work
        @SuppressWarnings("unchecked")
        Vector<LsEntry> files = c.ls(".");
        for (LsEntry file : files) {
            if (FINAL_REMOTE_FILENAME.equals(file.getFilename())) {
                c.rm(FINAL_REMOTE_FILENAME);
            }
        }
        
        // transmit file, using temp remote name, so ingestion won't process file until complete
        c.put(new FileInputStream(fileToLand), TEMP_REMOTE_FILENAME, ChannelSftp.OVERWRITE);
        
        // rename remote file so ingestion can begin
        c.rename(TEMP_REMOTE_FILENAME, FINAL_REMOTE_FILENAME);
        
        c.disconnect();
        channel.disconnect();
        session.disconnect();
    }
    
}
