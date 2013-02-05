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

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

/**
 * Test class for PropertyUtils
 *
 * @author vmcglaughlin
 *
 */
public class PropertyUtilsTest {

    @Test
    public void testGetPropertiesDefault() throws ParseException {
        String[] args = {};
        Properties props = PropertyUtils.getProperties(args);
        Assert.assertEquals("test.publisher.agent", props.getProperty(PropertyUtils.KEY_AGENT_ID));
        Assert.assertEquals("http://10.163.6.73:50002/TestZone", props.getProperty(PropertyUtils.KEY_ZONE_URL));
        Assert.assertEquals("TestZone", props.getProperty(PropertyUtils.KEY_ZONE_ID));
        Assert.assertEquals("LEAInfoAdd", props.getProperty(PropertyUtils.KEY_SCRIPT));
        Assert.assertEquals(5000, ((Long) props.get(PropertyUtils.KEY_WAIT_TIME)).longValue());
        Assert.assertEquals("", props.getProperty(PropertyUtils.KEY_MESSAGE_FILE));
        Assert.assertEquals("ADD", props.getProperty(PropertyUtils.KEY_EVENT_ACTION));
    }

    @Test
    public void testGetPropertiesNonDefault() throws ParseException {
        String agentId = "agentId";
        String zoneUrl = "http://localhost:1337/zone";
        String zoneId = "MyZone";
        String script = "step1,step2,step3";
        long waitTime = 9999;
        String messageFile = "path/to/file";
        String eventAction = "DELETE";

        String[] args = {"-a", agentId, "-u", zoneUrl, "-z", zoneId, "-s", script, "-w", String.valueOf(waitTime),
                "-f", messageFile, "-e", eventAction};
        Properties props = PropertyUtils.getProperties(args);
        Assert.assertEquals(agentId, props.getProperty(PropertyUtils.KEY_AGENT_ID));
        Assert.assertEquals(zoneUrl, props.getProperty(PropertyUtils.KEY_ZONE_URL));
        Assert.assertEquals(zoneId, props.getProperty(PropertyUtils.KEY_ZONE_ID));
        Assert.assertEquals(script, props.getProperty(PropertyUtils.KEY_SCRIPT));
        Assert.assertEquals(waitTime, ((Long) props.get(PropertyUtils.KEY_WAIT_TIME)).longValue());
        Assert.assertEquals(messageFile, props.getProperty(PropertyUtils.KEY_MESSAGE_FILE));
        Assert.assertEquals(eventAction, props.getProperty(PropertyUtils.KEY_EVENT_ACTION));
    }

}
