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
    private static final String LINK_HTML = "<a href=\"$LINK\">$TYPE</a>";
    
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
