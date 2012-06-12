package org.slc.sli.shtick;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.client.Entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author jstokes
 */
public class StandardLevel1ClientTest {

    private Level1Client level1Client;

    @Before
    public void setup() {
        level1Client = new StandardLevel1Client();
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
}
