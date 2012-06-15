package org.slc.sli.shtick;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManualTest {
    private Level2ClientManual client; // class under test

    private static final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";

    @Before
    public void setup() {
        client = new StandardLevel2ClientManual(BASE_URL);
    }

    @Test
    public void testCRUD() {
        try {
            // POST
            String studentId = postStudent();
            assertNotNull(studentId);

            // GET
            List<RestEntity> student = client.getStudentsByStudentId(TestingConstants.ROGERS_TOKEN,
                    Arrays.asList(studentId), Collections.EMPTY_MAP);
            assertNotNull(student);
            assertEquals(1, student.size());

            // PUT TODO


            // DELETE
            client.deleteStudentById(TestingConstants.ROGERS_TOKEN, studentId);

            // GET (Should fail)
            try {
                client.getStudentsByStudentId(TestingConstants.ROGERS_TOKEN,
                        Arrays.asList(studentId), Collections.EMPTY_MAP);
            } catch (RestException e) {
                assertEquals(404, e.getStatusCode());
            }
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (RestException e) {
            fail(e.getMessage());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    private String postStudent() throws IOException, RestException, URISyntaxException {
        final String studentData = readJsonFromFile("/testStudent.json");
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode studentJson = mapper.readValue(studentData, JsonNode.class);
        final RestEntity student = mapper.readValue(studentJson, RestEntity.class);

        return client.postStudent(TestingConstants.ROGERS_TOKEN, student);
    }

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
