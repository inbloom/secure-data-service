package org.slc.sli.lander.util;

import java.io.File;
import java.util.Properties;

import javax.swing.ProgressMonitor;

import org.slc.sli.lander.SimpleUserInfo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

public class SftpUtils {

    public static void landFile(File fileToLand, Properties properties) throws Exception {
        JSch jsch = new JSch();

        String host = properties.getProperty(PropertyUtils.KEY_SFTP_SERVER);
        String user = properties.getProperty(PropertyUtils.KEY_USER);
        String password = properties.getProperty(PropertyUtils.KEY_PASSWORD);
        String remoteDirectory = properties.getProperty(PropertyUtils.KEY_REMOTE_DIRECTORY);
        String localFile = fileToLand.getAbsolutePath();
        int port = 22;

        Session session = jsch.getSession(user, host, port);
        session.setOutputStream(System.out);

       // UserInfo ui = new SimpleUserInfo(password);
        //session.setUserInfo(ui);

        session.setPassword(password);

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp c = (ChannelSftp) channel;

//        c.setExtOutputStream(System.out);


        c.put(localFile, remoteDirectory, ChannelSftp.OVERWRITE);

        c.disconnect();
        channel.disconnect();
        session.disconnect();
    }
}
