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


package org.slc.sli.api.validation.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        assertTrue("Should validate", queryStringValidator.validate(new URI("http://localhost:8080/test?"
                + URLEncoder.encode("key<value", "UTF-8"))));
        assertTrue("Should validate", queryStringValidator.validate(new URI("http://localhost:8080/test?"
                + URLEncoder.encode("key>value", "UTF-8"))));
    }
}
