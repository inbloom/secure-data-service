package org.slc.sli.reporting;

import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.slc.sli.sif.reporting.PropertyUtils;

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
    }

    @Test
    public void testGetPropertiesNonDefault() throws ParseException {
        String agentId = "agentId";
        String zoneUrl = "http://localhost:1337/zone";
        String zoneId = "MyZone";
        String script = "step1,step2,step3";
        long waitTime = 9999;

        String[] args = {"-a", agentId, "-u", zoneUrl, "-z", zoneId, "-s", script, "-w", String.valueOf(waitTime)};
        Properties props = PropertyUtils.getProperties(args);
        Assert.assertEquals(agentId, props.getProperty(PropertyUtils.KEY_AGENT_ID));
        Assert.assertEquals(zoneUrl, props.getProperty(PropertyUtils.KEY_ZONE_URL));
        Assert.assertEquals(zoneId, props.getProperty(PropertyUtils.KEY_ZONE_ID));
        Assert.assertEquals(script, props.getProperty(PropertyUtils.KEY_SCRIPT));
        Assert.assertEquals(waitTime, ((Long) props.get(PropertyUtils.KEY_WAIT_TIME)).longValue());
    }

}
