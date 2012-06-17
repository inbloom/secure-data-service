package org.slc.sli.shtick;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.InvocationException;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jstokes
 */
public class JaxRSLevel0ClientTest {

    private Level0Client client; // class under test

    @Before
    public void setup() {
        this.client = new SpringLevel0Client();
    }

    @Test
    @Ignore
    public void testDeleteRequest() {
        try {
            client.delete(TestingConstants.ROGERS_TOKEN, new URI(TestingConstants.BASE_URL + "/students/"
                    + TestingConstants.TEST_STUDENT_DELETE_ID), MediaType.APPLICATION_JSON);

            final String getResponse = client.get(TestingConstants.ROGERS_TOKEN, new URI(
                    TestingConstants.BASE_URL + "/students/" + TestingConstants.TEST_STUDENT_DELETE_ID),
                    MediaType.APPLICATION_JSON);
            assertNotNull(getResponse);
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        } catch (final IOException e) {
            fail(e.getMessage());
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetRequest() {
        try {
            String actualResponse = client.get(TestingConstants.ROGERS_TOKEN, new URI(TestingConstants.BASE_URL
                    + "/students"), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);

            actualResponse = client.get(TestingConstants.ROGERS_TOKEN, new URI(TestingConstants.BASE_URL
                    + "/students/" + TestingConstants.TEST_STUDENT_ID), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        } catch (final InvocationException e) {
            fail(e.getMessage());
        } catch (final IOException e) {
            fail(e.getMessage());
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }

    @Ignore("Problem with invalid autorization token.")
    @Test
    public void testGetRequestWithBrokenToken() {
        try {
            String actualResponse = client.get(TestingConstants.BROKEN_TOKEN, new URI(TestingConstants.BASE_URL
                    + "/students"), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);

            actualResponse = client.get(TestingConstants.BROKEN_TOKEN, new URI(TestingConstants.BASE_URL
                    + "/students/" + TestingConstants.TEST_STUDENT_ID), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        } catch (final InvocationException e) {
            fail(e.getMessage());
        } catch (final IOException e) {
            fail(e.getMessage());
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        }
    }
}
