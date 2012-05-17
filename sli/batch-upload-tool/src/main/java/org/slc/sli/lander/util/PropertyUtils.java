package org.slc.sli.lander.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class PropertyUtils {

    private static final String FLAG_USER = "u";
    private static final String FLAG_PASSWORD = "p";
    private static final String FLAG_SFTP_SERVER = "s";
    private static final String FLAG_LOCAL_DIRECTORY = "d";
    private static final String FLAG_REMOTE_DIRECTORY = "r";
    private static final String FLAG_MONITOR_INTERVAL = "i";
    private static final String FLAG_CONFIG_FILE = "c";

    public static final String KEY_USER = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SFTP_SERVER = "remote_server";
    public static final String KEY_LOCAL_DIRECTORY = "local_directory";
    public static final String KEY_REMOTE_DIRECTORY = "remote_directory";
    public static final String KEY_MONITOR_INTERVAL = "monitor_interval";
    public static final String KEY_CONFIG_FILE = "configuration_file";

    private static Map<String, String> flagToKeyMap = null;

    static {
        flagToKeyMap = new HashMap<String, String>();
        flagToKeyMap.put(FLAG_USER, KEY_USER);
        flagToKeyMap.put(FLAG_PASSWORD, KEY_PASSWORD);
        flagToKeyMap.put(FLAG_SFTP_SERVER, KEY_SFTP_SERVER);
        flagToKeyMap.put(FLAG_LOCAL_DIRECTORY, KEY_LOCAL_DIRECTORY);
        flagToKeyMap.put(FLAG_REMOTE_DIRECTORY, KEY_REMOTE_DIRECTORY);
        flagToKeyMap.put(FLAG_MONITOR_INTERVAL, KEY_MONITOR_INTERVAL);
        flagToKeyMap.put(FLAG_CONFIG_FILE, KEY_CONFIG_FILE);
    }

    private static Options options = null;

    static {
        options = new Options();
        options.addOption(FLAG_USER, true, KEY_USER);
        options.addOption(FLAG_PASSWORD, true, KEY_PASSWORD);
        options.addOption(FLAG_SFTP_SERVER, true, KEY_SFTP_SERVER);
        options.addOption(FLAG_LOCAL_DIRECTORY, true, KEY_LOCAL_DIRECTORY);
        options.addOption(FLAG_REMOTE_DIRECTORY, true, KEY_REMOTE_DIRECTORY);
        options.addOption(FLAG_MONITOR_INTERVAL, true, KEY_MONITOR_INTERVAL);
        options.addOption(FLAG_CONFIG_FILE, true, KEY_CONFIG_FILE);
    }

    public static Properties parseArguments(String[] args) throws Exception {
        Properties properties = new Properties();
        CommandLine cmd = null; // Command Line arguments

        CommandLineParser parser = new PosixParser();
        cmd = parser.parse(options, args);
        if (cmd.hasOption(FLAG_CONFIG_FILE)) {
            String configFilePath = cmd.getOptionValue(FLAG_CONFIG_FILE);
            File configFile = new File(configFilePath);
            if (configFile.exists() && configFile.canRead()) {
                properties.load(new FileInputStream(configFile));
            }
        }

        for (Option option : cmd.getOptions()) {
            String arg = option.getOpt();
            String value = cmd.getOptionValue(arg);
            String key = flagToKeyMap.get(arg);
            properties.put(key, value);
        }

        return properties;
    }
}
