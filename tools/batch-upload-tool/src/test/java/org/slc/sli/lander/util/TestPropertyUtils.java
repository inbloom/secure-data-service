package org.slc.sli.lander.util;

import junit.framework.Assert;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.lander.config.UploadProperties;
import org.slc.sli.lander.exception.MissingConfigException;

public class TestPropertyUtils {
    
    private PropertyUtils propUtils;
    
    @Before
    public void setup() {
        propUtils = new PropertyUtils(new PosixParser());
    }
    
    @Test(expected = MissingConfigException.class)
    public void missingUser() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-pass", "password", "-s", "server", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingArgumentException.class)
    public void missingUserValue() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "-pass", "password", "-s", "server", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingConfigException.class)
    public void missingPassword() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-s", "server", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingArgumentException.class)
    public void missingPasswordValue() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "-s", "server", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingConfigException.class)
    public void missingServer() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingArgumentException.class)
    public void missingServerValue() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "-d", "localDir", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingConfigException.class)
    public void missingLocalDir() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "server", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingArgumentException.class)
    public void missingLocalDirValue() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "server", "-d", "-port", "22" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingConfigException.class)
    public void missingPort() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "server", "-d", "localDir" };
        propUtils.getUploadProperties(args);
    }
    
    @Test(expected = MissingArgumentException.class)
    public void missingPortValue() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "server", "-d", "localDir", "-port" };
        propUtils.getUploadProperties(args);
    }
    
    @Test
    public void parseProperties() throws ParseException, MissingConfigException {
        String[] args = new String[] { "-u", "user", "-pass", "password", "-s", "server", "-d", "localDir", "-port",
                "22" };
        UploadProperties props = propUtils.getUploadProperties(args);
        Assert.assertNotNull("UploadProperties should not be null", props);
        Assert.assertEquals("user", props.getUser());
        Assert.assertEquals("password", props.getPassword());
        Assert.assertEquals("server", props.getSftpServer());
        Assert.assertEquals("localDir", props.getLocalDir());
    }
    
}
