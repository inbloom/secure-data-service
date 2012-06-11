package org.slc.sli.shtick;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author jstokes
 */
public class StandardLevel0ClientTest {

    private Level0Client client; // class under test

    // RRogers - IT Admin testing token
    private static final String TESTING_TOKEN = "cacd9227-5b14-4685-babe-31230476cf3b";

    // TODO: Should be pulling this out of a config file
    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";

    @Before
    public void setup() {
        this.client = new StandardLevel0Client();
    }

    @Test
    public void testGetRequest() {
        try {
            Response actualResponse = client.getRequest(TESTING_TOKEN,
                    new URL(BASE_URL + "/students"), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(Response.ok().build().getStatus(), actualResponse.getStatus());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (InvocationException e) {
            fail(e.getMessage());
        }
    }
}
