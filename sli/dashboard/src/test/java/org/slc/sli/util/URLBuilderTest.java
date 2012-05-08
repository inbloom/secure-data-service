/**
 * 
 */
package org.slc.sli.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author tosako
 * 
 */
public class URLBuilderTest {
    
    @Test
    public void test() {
        String actualUrl = "http://www.wirelessgeneration.com/test_path/test_path2?key1=fake_value1&key2=fake%20value2";
        URLBuilder url = new URLBuilder("http://www.wirelessgeneration.com");
        url.addPath("test_path/");
        url.addPath("test_path2");
        url.addQueryParam("key1", "fake_value1");
        url.addQueryParam("key2", "fake value2");
        assertEquals("URL should be \"" + actualUrl + "\"", actualUrl, url.toString());
    }
    
}
