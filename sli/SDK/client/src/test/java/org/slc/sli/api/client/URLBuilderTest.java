package org.slc.sli.api.client;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for URL builder
 * 
 * 
 */
public class URLBuilderTest {

    @Test
    public void testIds() throws Exception {
        URLBuilder builder = URLBuilder.create("http://localhost");
        List<String> ids = Arrays.asList("1", "2", "3", "4", "5");
        builder.addPath(Constants.API_SERVER_PATH).addPath(EntityType.STUDENTS.getResource()).ids(ids);
        URL url = builder.build();
        assertEquals("the URL should be http://localhost/api/rest/v1/students/1,2,3,4,5", "http://localhost/"
                + Constants.API_SERVER_PATH + "/students/1,2,3,4,5", url.toString());
    }
    
    @Test(expected = MalformedURLException.class)
    public void testBuildException() throws Exception {
        URLBuilder builder = URLBuilder.create("");
        builder.addPath(Constants.API_SERVER_PATH).addPath(EntityType.STUDENTS.getResource());
        builder.build();
    }
}
