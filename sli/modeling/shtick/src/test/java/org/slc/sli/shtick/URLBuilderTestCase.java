package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for URL builder
 */
public final class URLBuilderTestCase {

    @Test
    public void testIds() throws Exception {
        final URLBuilder builder = URLBuilder.baseUrl("http://localhost");
        final List<String> ids = Arrays.asList("1", "2", "3", "4", "5");
        builder.addPath("api/rest/v1").addPath("students").ids(ids);
        final URL url = builder.build();
        assertEquals("the URL should be http://localhost/api/rest/v1/students/1,2,3,4,5", "http://localhost/"
                + "api/rest/v1" + "/students/1,2,3,4,5", url.toString());
    }

    @Test(expected = MalformedURLException.class)
    public void testBuildException() throws Exception {
        final URLBuilder builder = URLBuilder.baseUrl("");
        builder.addPath("api/rest/v1").addPath("students");
        builder.build();
    }
}
