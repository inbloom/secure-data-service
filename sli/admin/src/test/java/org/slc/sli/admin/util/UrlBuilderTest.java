package org.slc.sli.admin.util;

import org.junit.Test;
import junit.framework.Assert;

/**
 *
 * @author pwolf
 *
 */
public class UrlBuilderTest {

    @Test
    public void testAppendWithoutSlash() {
        UrlBuilder builder = new UrlBuilder("http://www.example.com/foo");
        builder.addPath("bar");
        Assert.assertEquals("http://www.example.com/foo/bar", builder.toString());
    }

    @Test
    public void testAppendWithSlash() {
        UrlBuilder builder = new UrlBuilder("http://www.example.com/foo/");
        builder.addPath("bar");
        Assert.assertEquals("http://www.example.com/foo/bar", builder.toString());
    }

    @Test
    public void testAddParmWithSlash() {
        UrlBuilder builder = new UrlBuilder("http://www.example.com/foo/");
        builder.addQueryParam("ocho", "cinco");
        Assert.assertEquals("http://www.example.com/foo?ocho=cinco", builder.toString());
    }

    @Test
    public void testAddParmWithoutSlash() {
        UrlBuilder builder = new UrlBuilder("http://www.example.com/foo");
        builder.addQueryParam("ocho", "cinco");
        Assert.assertEquals("http://www.example.com/foo?ocho=cinco", builder.toString());
    }

    @Test
    public void testAddSecondParm() {
        UrlBuilder builder = new UrlBuilder("http://www.example.com/foo?ocho=cinco");
        builder.addQueryParam("pina", "colada");
        Assert.assertEquals("http://www.example.com/foo?ocho=cinco&pina=colada", builder.toString());
    }

}
