package org.slc.sli.lander.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slc.sli.lander.config.UploadProperties;
import org.slc.sli.lander.exception.MissingConfigException;

public class PropertyUtils {
    
    private static final String FLAG_USER = "u";
    private static final String FLAG_PASSWORD = "p";
    private static final String FLAG_SFTP_SERVER = "s";
    private static final String FLAG_LOCAL_DIRECTORY = "d";
    private static final String FLAG_REMOTE_DIRECTORY = "r";
    
    public static final String KEY_USER = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SFTP_SERVER = "remote_server";
    public static final String KEY_LOCAL_DIRECTORY = "local_directory";
    public static final String KEY_REMOTE_DIRECTORY = "remote_directory";
    
    final public static Options OPTIONS = new Options();
    
    static {
        OPTIONS.addOption(FLAG_USER, true, KEY_USER);
        OPTIONS.addOption(FLAG_PASSWORD, true, KEY_PASSWORD);
        OPTIONS.addOption(FLAG_SFTP_SERVER, true, KEY_SFTP_SERVER);
        OPTIONS.addOption(FLAG_LOCAL_DIRECTORY, true, KEY_LOCAL_DIRECTORY);
        OPTIONS.addOption(FLAG_REMOTE_DIRECTORY, true, KEY_REMOTE_DIRECTORY);
    }
    
    private final CommandLineParser parser;
    
    public PropertyUtils(CommandLineParser parser) {
        this.parser = parser;
    }
    
    public UploadProperties getUploadProperties(String[] args) throws ParseException, MissingConfigException {
        
        CommandLine cmd = parser.parse(OPTIONS, args);
        
        String user = cmd.getOptionValue(FLAG_USER);
        if (user == null) {
            throw new MissingConfigException(FLAG_USER);
        }
        String password = cmd.getOptionValue(FLAG_PASSWORD);
        if (password == null) {
            throw new MissingConfigException(FLAG_PASSWORD);
        }
        String server = cmd.getOptionValue(FLAG_SFTP_SERVER);
        if (server == null) {
            throw new MissingConfigException(FLAG_SFTP_SERVER);
        }
        String localDir = cmd.getOptionValue(FLAG_LOCAL_DIRECTORY);
        if (localDir == null) {
            throw new MissingConfigException(FLAG_LOCAL_DIRECTORY);
        }
        String remoteDir = cmd.getOptionValue(FLAG_REMOTE_DIRECTORY);
        if (remoteDir == null) {
            throw new MissingConfigException(FLAG_REMOTE_DIRECTORY);
        }
        
        return new UploadProperties(user, password, server, remoteDir, localDir);
    }
}
