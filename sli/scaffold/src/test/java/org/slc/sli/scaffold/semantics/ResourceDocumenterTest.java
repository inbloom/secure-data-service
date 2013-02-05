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


package org.slc.sli.scaffold.semantics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ResourceDocumenter
 * @author jstokes
 *
 */
public class ResourceDocumenterTest {
    
    private ResourceDocumenter testResource = new ResourceDocumenter(); //class under test
    
    private static final String EXPECTED_OUTPUT = "This is my test";
    
    @Before
    public void setup() {
        
    }
    
    @Test
    public void testReadFile() {
       URL testFile = this.getClass().getResource("/application.html");
       try {
           String out = testResource.readFile(new File(testFile.toURI()));
           assertEquals("output should match", out, EXPECTED_OUTPUT);
       } catch (URISyntaxException e) {
           fail(e.getMessage());
       } 
    }
    
    @Test
    public void testCreateLink() {
        testResource.readPropertiesFile();
        
        final String expectedLink = "<a href=\"" + testResource.getBaseUrl()
                + "endpoint#anchor\">test</a>";
        
        String key = "test";
        String value = "endpoint#anchor";
        String out = testResource.createLink(key, value);
        
        assertEquals("output should match", out, expectedLink);
    }
}
