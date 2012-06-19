package org.slc.sli.shtick;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManualTest {
    private Level2ClientManual client; // class under test

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    @Before
    public void setup() {
        client = new StandardLevel2ClientManual(BASE_URL);
    }

    @Test
    public void testCRUD() {
        try {
            // POST
            final String studentId = postStudent();
            assertNotNull(studentId);

            // GET
            final List<Entity> student = getStudent(studentId);
            assertNotNull(student);
            assertEquals(1, student.size());

            // PUT
            putStudent(studentId);
            final List<Entity> updatedStudent = getStudent(studentId);
            assertEquals(1, updatedStudent.size());
            assertEquals("900000012", updatedStudent.get(0).getData().get("studentUniqueStateId"));

            // DELETE
            client.deleteStudentById(TestingConstants.ROGERS_TOKEN, studentId);

            // GET (Should fail)
            try {
                getStudent(studentId);
            } catch (final StatusCodeException e) {
                assertEquals(404, e.getStatusCode());
            }
        } catch (final IOException e) {
            fail(e.getMessage());
        } catch (final StatusCodeException e) {
            fail(e.getMessage());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    private List<Entity> getStudent(final String studentId) throws IOException, StatusCodeException {
        return client.getStudentsByStudentId(TestingConstants.ROGERS_TOKEN, Arrays.asList(studentId), EMPTY_QUERY_ARGS);
    }

    private void putStudent(final String studentId) throws IOException, StatusCodeException, URISyntaxException {
        final String studentData = readJsonFromFile("/testStudentUpdated.json");
        final Entity student = deserialize(studentData);
        Map<String, Object> dataCopy = MapHelper.deepCopy(student.getData());
        dataCopy.put("id", studentId);

        client.putStudent(TestingConstants.ROGERS_TOKEN, new Entity("student", dataCopy));
    }

    private String postStudent() throws IOException, StatusCodeException, URISyntaxException {
        final String studentData = readJsonFromFile("/testStudent.json");
        final Entity student = deserialize(studentData);

        return client.postStudent(TestingConstants.ROGERS_TOKEN, student);
    }

    private Entity deserialize(final String studentData) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode studentJson = mapper.readValue(studentData, JsonNode.class);
        return mapper.readValue(studentJson, Entity.class);
    }

    private String readJsonFromFile(final String fileLoc) {
        InputStream in = this.getClass().getResourceAsStream(fileLoc);
        try {
            return IOUtils.toString(in);
        } catch (final IOException e) {
            fail(e.getMessage());
        }
        throw new RuntimeException();
    }
}
