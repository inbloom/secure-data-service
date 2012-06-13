package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.api.client.Entity;

public class StandardLevel2ClientTest {

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, String> EMPTY_QUERY_ARGS = Collections.emptyMap();

    @Before
    public void setup() {
    }

    @Test
    public void testGetStudents() {
        final Level2Client client = new StandardLevel2Client(BASE_URL);
        try {
            final List<Entity> students = client.getStudents(TestingConstants.TESTING_TOKEN, EMPTY_QUERY_ARGS);
            assertNotNull(students);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final SLIDataStoreException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetStudentsById() {
        final Level2Client client = new StandardLevel2Client(BASE_URL);
        try {
            final List<Entity> students = client.getStudentsById(TestingConstants.TESTING_TOKEN,
                    TestingConstants.TEST_STUDENT_ID, EMPTY_QUERY_ARGS);

            assertNotNull(students);
            assertEquals(1, students.size());

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final SLIDataStoreException e) {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testDeleteRequest() {

    }

}
