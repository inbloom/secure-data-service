package org.slc.sli.api.validation.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 6/14/12
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SimpleURLValidatorTest {

    private SimpleURLValidator validator = new SimpleURLValidator();

    @Before
    public void setup() {

    }

    @Test
    public void testInvalidURL() throws URISyntaxException {
        assertFalse("Should not validate", validator.validate(new URI("http://localhost:8080/test/testtest")));
        assertFalse("Should not validate", validator.validate(new URI("http://localhost:8080/test%00")));
    }

    @Test
    public void testValidURL() throws URISyntaxException {
        assertTrue("Should validate", validator.validate(new URI("http://local.slidev.org:8080/test")));
        assertTrue("Should validate", validator.validate(new URI("http://local.slidev.org")));
    }

}
