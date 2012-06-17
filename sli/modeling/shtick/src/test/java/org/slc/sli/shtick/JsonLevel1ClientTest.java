package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;

/**
 * @author jstokes
 */
public class JsonLevel1ClientTest {

    private static final String URL_STUDENT_COLLECTION = TestingConstants.BASE_URL + "/students";

    private Level1Client level1Client;

    @Before
    public void setup() {
        level1Client = new JsonLevel1Client();
    }

    @Ignore("Provide data in Map.")
    public void testCrud() {
        try {
            final String studentUniqueStateId = "studentUniqueStateId";

            // POST
            final URL loc = postStudent();
            assertNotNull(loc);

            // GET
            final List<Entity> getStudent = getStudent(loc);
            assertNotNull(getStudent);
            assertEquals(1, getStudent.size());
            assertEquals("900000011", getStudent.get(0).getData().get(studentUniqueStateId));

            // PUT
            putStudent(loc);
            final List<Entity> putStudent = getStudent(loc);
            assertNotNull(putStudent);
            assertEquals(1, putStudent.size());
            assertEquals("900000012", putStudent.get(0).getData().get(studentUniqueStateId));

            // DELETE
            deleteStudent(loc);

            // GET (Should now fail)
            try {
                getStudent(loc);
                fail("An exception was not thrown for get non-existent student!");
            } catch (StatusCodeException e) {
                assertEquals(404, e.getStatusCode());
            }

        } catch (URISyntaxException e) {
            fail(e.getMessage());
        } catch (StatusCodeException e) {
            e.printStackTrace();
            fail("Status code: " + e.getStatusCode() + "\n" + e.getMessage());
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private URL postStudent() throws URISyntaxException, IOException, StatusCodeException {
        // final String readJsonFromFile = readJsonFromFile("/testStudent.json");
        final Map<String, Object> data = new HashMap<String, Object>();
        final Entity student = new Entity("student", data);
        return level1Client.postRequest(TestingConstants.ROGERS_TOKEN, student, new URL(URL_STUDENT_COLLECTION));
    }

    private void deleteStudent(URL loc) throws IOException, StatusCodeException, URISyntaxException {
        level1Client.deleteRequest(TestingConstants.ROGERS_TOKEN, loc);
    }

    private void putStudent(URL loc) throws IOException, StatusCodeException, URISyntaxException {
        final Map<String, Object> data = new HashMap<String, Object>();
        final Entity student = new Entity("student", data);
        // String readJsonFromFile = readJsonFromFile("/testStudentUpdated.json");
        level1Client.putRequest(TestingConstants.ROGERS_TOKEN, student, loc);
    }

    private List<Entity> getStudent(URL url) throws IOException, StatusCodeException, URISyntaxException {
        return level1Client.getRequest(TestingConstants.ROGERS_TOKEN, url);
    }

    @SuppressWarnings("unused")
    private String readJsonFromFile(String fileLoc) {
        InputStream in = this.getClass().getResourceAsStream(fileLoc);
        try {
            return IOUtils.toString(in);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        throw new RuntimeException();
    }
}
