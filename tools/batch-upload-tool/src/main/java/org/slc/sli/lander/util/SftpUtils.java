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
