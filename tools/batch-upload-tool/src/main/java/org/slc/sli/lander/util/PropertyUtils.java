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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slc.sli.lander.config.UploadProperties;
import org.slc.sli.lander.exception.MissingConfigException;

public class PropertyUtils {
    
    private static final String FLAG_USER = "u";
    private static final String FLAG_PASSWORD = "pass";
    private static final String FLAG_SFTP_SERVER = "s";
    private static final String FLAG_LOCAL_DIRECTORY = "d";
    private static final String FLAG_PORT = "port";
    
    public static final String KEY_USER = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SFTP_SERVER = "remote_server";
    public static final String KEY_LOCAL_DIRECTORY = "local_directory";
    public static final String KEY_PORT = "port";
    
    final public static Options OPTIONS = new Options();
    
    static {
        OPTIONS.addOption(FLAG_USER, true, KEY_USER);
        OPTIONS.addOption(FLAG_PASSWORD, true, KEY_PASSWORD);
        OPTIONS.addOption(FLAG_SFTP_SERVER, true, KEY_SFTP_SERVER);
        OPTIONS.addOption(FLAG_LOCAL_DIRECTORY, true, KEY_LOCAL_DIRECTORY);
        OPTIONS.addOption(KEY_PORT, true, KEY_PORT);
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
        int port;
        try {
            port = Integer.parseInt(cmd.getOptionValue(FLAG_PORT));
        } catch (NumberFormatException e) {
            throw new MissingConfigException(FLAG_PORT);
        }
        
        return new UploadProperties(user, password, server, localDir, port);
    }
}
