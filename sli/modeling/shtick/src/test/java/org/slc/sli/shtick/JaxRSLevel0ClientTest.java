package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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
            RestResponse actualResponse = client.deleteRequest(TestingConstants.ROGERS_TOKEN, new URL(
                    TestingConstants.BASE_URL + "/students/" + TestingConstants.TEST_STUDENT_DELETE_ID),
                    MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(204, actualResponse.getStatusCode());

            RestResponse deletedResponse = client.getRequest(TestingConstants.ROGERS_TOKEN, new URL(
                    TestingConstants.BASE_URL + "/students/" + TestingConstants.TEST_STUDENT_DELETE_ID),
                    MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(404, deletedResponse.getStatusCode());

        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (RestException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetRequest() {
        try {
            RestResponse actualResponse = client.getRequest(TestingConstants.ROGERS_TOKEN, new URL(
                    TestingConstants.BASE_URL + "/students"), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(200, actualResponse.getStatusCode());

            actualResponse = client.getRequest(TestingConstants.ROGERS_TOKEN, new URL(TestingConstants.BASE_URL
                    + "/students/" + TestingConstants.TEST_STUDENT_ID), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(200, actualResponse.getStatusCode());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (InvocationException e) {
            fail(e.getMessage());
        } catch (RestException e) {
            fail(e.getMessage());
        }
    }

    @Ignore("Problem with invalid autorization token.")
    @Test
    public void testGetRequestWithBrokenToken() {
        try {
            RestResponse actualResponse = client.getRequest(TestingConstants.BROKEN_TOKEN, new URL(
                    TestingConstants.BASE_URL + "/students"), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(200, actualResponse.getStatusCode());

            actualResponse = client.getRequest(TestingConstants.BROKEN_TOKEN, new URL(TestingConstants.BASE_URL
                    + "/students/" + TestingConstants.TEST_STUDENT_ID), MediaType.APPLICATION_JSON);
            assertNotNull(actualResponse);
            assertEquals(401, actualResponse.getStatusCode());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (InvocationException e) {
            fail(e.getMessage());
        } catch (RestException e) {
            fail(e.getMessage());
        }
    }
}
