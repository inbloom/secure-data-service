package org.slc.sli.lander.util;

import java.io.File;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.slc.sli.lander.config.UploadProperties;

public class SftpUtils {
    
    public static void landFile(File fileToLand, UploadProperties properties) throws Exception {
        JSch jsch = new JSch();
        
        String host = properties.getSftpServer();
        String user = properties.getUser();
        String password = properties.getPassword();
        String remoteDirectory = properties.getRemoteDir();
        String localFile = fileToLand.getAbsolutePath();
        int port = 22;
        
        Session session = jsch.getSession(user, host, port);
        session.setOutputStream(System.out);
        
        // UserInfo ui = new SimpleUserInfo(password);
        // session.setUserInfo(ui);
        
        session.setPassword(password);
        
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp c = (ChannelSftp) channel;
        
        // c.setExtOutputStream(System.out);
        
        c.put(localFile, remoteDirectory, ChannelSftp.OVERWRITE);
        
        c.disconnect();
        channel.disconnect();
        session.disconnect();
    }
}
