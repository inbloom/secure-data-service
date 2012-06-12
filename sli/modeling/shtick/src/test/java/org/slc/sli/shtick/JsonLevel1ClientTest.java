package org.slc.sli.shtick;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.client.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author jstokes
 */
public class JsonLevel1ClientTest {

    private Level1Client level1Client;

    @Before
    public void setup() {
        level1Client = new JsonLevel1Client();
    }

    @Test
    public void testGetRequest() {
        final List<Entity> listResponse;
        final List<Entity> singleResponse;
        try {
            listResponse = level1Client.getRequest(TestingConstants.TESTING_TOKEN,
                    new URL(TestingConstants.BASE_URL + "/students"));
            assertNotNull(listResponse);

            singleResponse = level1Client.getRequest(TestingConstants.TESTING_TOKEN,
                    new URL(TestingConstants.BASE_URL + "/students/" + TestingConstants.TEST_STUDENT_ID));

            assertNotNull(singleResponse);
            assertEquals(1, singleResponse.size());

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (SLIDataStoreException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteRequest() {

    }

    @Test
    public void testCreate() {
        try {
            URL loc = level1Client.postRequest(TestingConstants.TESTING_TOKEN,
                    createStudentJson(),
                    new URL(TestingConstants.BASE_URL + "/students"));
            assertNotNull(loc);
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (SLIDataStoreException e) {
            fail(e.getMessage());
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private String createStudentJson() {
        InputStream in = this.getClass().getResourceAsStream("/testStudent.json");
        try {
            return IOUtils.toString(in);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        throw new RuntimeException();
    }
}
