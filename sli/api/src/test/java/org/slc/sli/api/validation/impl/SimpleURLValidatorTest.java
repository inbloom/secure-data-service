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

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
