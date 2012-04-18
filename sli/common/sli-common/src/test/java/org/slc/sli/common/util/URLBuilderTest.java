package org.slc.sli.common.util;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.PathConstants;

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
        builder.addPath(PathConstants.API_SERVER_PATH).addPath(ResourceNames.STUDENTS).ids(ids);
        URL url = builder.build();
        assertEquals("the URL should be http://localhost/api/rest/v1/students/1,2,3,4,5", "http://localhost/"
                + PathConstants.API_SERVER_PATH + "/students/1,2,3,4,5", url.toString());
    }

    @Test(expected = MalformedURLException.class)
    public void testBuildException() throws Exception {
        URLBuilder builder = URLBuilder.create("");
        builder.addPath(PathConstants.API_SERVER_PATH).addPath(ResourceNames.STUDENTS);
        builder.build();
    }
}
