package org.slc.sli.api.validation.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 6/14/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class QueryStringValidatorTest {

    @Autowired
    private QueryStringValidator queryStringValidator; //class under test

    @Test
    public void testInvalidQueryString() throws URISyntaxException {
        assertFalse("Should not validate", queryStringValidator.validate(new URI("http://localhost:8080/test?%00=value")));
    }

    @Test
    public void testValidQueryString() throws URISyntaxException, UnsupportedEncodingException {
        assertTrue("Should validate", queryStringValidator.validate(new URI("http://localhost:8080/test?key=value")));
        assertTrue("Should validate", queryStringValidator.validate(new URI("http://localhost:8080/test?" +
                URLEncoder.encode("key<value", "UTF-8"))));
        assertTrue("Should validate", queryStringValidator.validate(new URI("http://localhost:8080/test?" +
                URLEncoder.encode("key>value", "UTF-8"))));
    }
}
