/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.sif.reporting;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Utility class to handle command ling arguments
 *
 * @author vmcglaughlin
 *
 */
public class PropertyUtils {

    private static final String FLAG_ZONE_URL = "u";
    private static final String FLAG_ZONE_ID = "z";
    private static final String FLAG_AGENT_ID = "a";
    private static final String FLAG_SCRIPT = "s";
    private static final String FLAG_WAIT_TIME = "w";
    private static final String FLAG_MESSAGE_FILE = "f";
    private static final String FLAG_EVENT_ACTION = "e";

    public static final String KEY_ZONE_URL = "zone_url";
    public static final String KEY_ZONE_ID = "zone_id";
    public static final String KEY_AGENT_ID = "agent_id";
    public static final String KEY_SCRIPT = "script";
    public static final String KEY_WAIT_TIME = "wait_time";
    public static final String KEY_MESSAGE_FILE = "message_file";
    public static final String KEY_EVENT_ACTION = "event_action";

    public static final Options OPTIONS = new Options();

    private static final String DEFAULT_AGENT_ID = "test.publisher.agent";
    private static final String DEFAULT_ZONE_URL = "http://10.163.6.73:50002/TestZone";
    private static final String DEFAULT_ZONE_ID = "TestZone";
    private static final String DEFAULT_SCRIPT = "LEAInfoAdd";
    private static final String DEFAULT_WAIT_TIME = "5000";
    private static final String DEFAULT_MESSAGE_FILE = "";
    private static final String DEFAULT_EVENT_ACTION = "ADD";

    static {
        OPTIONS.addOption(FLAG_ZONE_URL, true, KEY_ZONE_URL);
        OPTIONS.addOption(FLAG_ZONE_ID, true, KEY_ZONE_ID);
        OPTIONS.addOption(FLAG_AGENT_ID, true, KEY_AGENT_ID);
        OPTIONS.addOption(FLAG_SCRIPT, true, KEY_SCRIPT);
        OPTIONS.addOption(FLAG_WAIT_TIME, true, KEY_WAIT_TIME);
        OPTIONS.addOption(FLAG_MESSAGE_FILE, true, KEY_MESSAGE_FILE);
        OPTIONS.addOption(FLAG_EVENT_ACTION, true, KEY_EVENT_ACTION);
    }

    public static Properties getProperties(String[] args) throws ParseException {
        Properties props = new Properties();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(OPTIONS, args);

        String zoneUrl = cmd.getOptionValue(FLAG_ZONE_URL, DEFAULT_ZONE_URL);
        String zoneId = cmd.getOptionValue(FLAG_ZONE_ID, DEFAULT_ZONE_ID);
        String agentId = cmd.getOptionValue(FLAG_AGENT_ID, DEFAULT_AGENT_ID);
        String script = cmd.getOptionValue(FLAG_SCRIPT, DEFAULT_SCRIPT);
        String waitTimeStr = cmd.getOptionValue(FLAG_WAIT_TIME, "DEFAULT_WAIT_TIME");
        long waitTime;
        try {
            waitTime = Long.parseLong(waitTimeStr);
        } catch (NumberFormatException e) {
            waitTime = Long.parseLong(DEFAULT_WAIT_TIME);
        }

        props.put(KEY_ZONE_URL, zoneUrl);
        props.put(KEY_ZONE_ID, zoneId);
        props.put(KEY_AGENT_ID, agentId);
        props.put(KEY_SCRIPT, script);
        props.put(KEY_WAIT_TIME, waitTime);

        String messageFile = cmd.getOptionValue(FLAG_MESSAGE_FILE, DEFAULT_MESSAGE_FILE);
        props.put(KEY_MESSAGE_FILE, messageFile);

        String eventAction = cmd.getOptionValue(FLAG_EVENT_ACTION, DEFAULT_EVENT_ACTION);
        props.put(KEY_EVENT_ACTION, eventAction);

        return props;
    }
}
