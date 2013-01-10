package org.slc.sli.shtick.samples;

import org.junit.Assert;
import org.junit.Test;
import org.slc.sli.shtick.JsonLevel1Client;
import org.slc.sli.shtick.Level2Client;
import org.slc.sli.shtick.Level3Client;
import org.slc.sli.shtick.StandardLevel2Client;
import org.slc.sli.shtick.StandardLevel3Client;
import org.slc.sli.shtick.StatusCodeException;
import org.slc.sli.shtick.TestingConstants;
import org.slc.sli.shtick.pojo.RepeatIdentifierType;
import org.slc.sli.shtick.pojo.StudentSectionAssociation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Sample CRUD test using shtick for StudentSectionAssociation
 *
 * @author srupasinghe
 *
 */
public class StudentSectionAssociationCrudSampleTest {

    private final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private static final String SECTION_ID = "1d345e41-f1c7-41b2-9cc4-9898c82faeda";
    private static final String STUDENT_ID = "2fab099f-47d5-4099-addf-69120db3b53b";

    @Test
    public void testCrud() {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);

        try {
            // CREATE
            final String associationId = doPostStudentSectionAssociations(client);
            Assert.assertNotNull(associationId);

            // READ
            StudentSectionAssociation association = doGetStudentSectionAssociationsById(client, associationId);

            assertEquals("Should match", associationId, association.getId());
            assertEquals("Should match", "2012-01-01", association.getBeginDate());
            assertEquals("Should match", "2012-06-06", association.getEndDate());
            assertEquals("Should match", true, association.getHomeroomIndicator());
            assertEquals("Should match", RepeatIdentifierType.NOT_REPEATED, association.getRepeatIdentifier());

            // UPDATE
            association.setEndDate("2012-08-03");
            association.setHomeroomIndicator(Boolean.FALSE);
            doPutStudentSectionAssociationsById(client, associationId, association);

            StudentSectionAssociation updatedAssociation = doGetStudentSectionAssociationsById(client, associationId);

            assertEquals("Should match", associationId, updatedAssociation.getId());
            assertEquals("Should match", "2012-01-01", updatedAssociation.getBeginDate());
            assertEquals("Should match", "2012-08-03", updatedAssociation.getEndDate());
            assertEquals("Should match", false, updatedAssociation.getHomeroomIndicator());
            assertEquals("Should match", RepeatIdentifierType.NOT_REPEATED, updatedAssociation.getRepeatIdentifier());

            // DELETE
            doDeleteStudentSectionAssociationsById(client, associationId);
            try {
                doGetStudentSectionAssociationsById(client, associationId);
                fail("Entity should be deleted");
            } catch (StatusCodeException e) {
                assertEquals(e.getStatusCode(), 404);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (StatusCodeException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage() + " - Status code = " + e.getStatusCode());
        }
    }

    private String doPostStudentSectionAssociations(Level3Client client) throws IOException, StatusCodeException {
        StudentSectionAssociation association = new StudentSectionAssociation();

        association.setBeginDate("2012-01-01");
        association.setEndDate("2012-06-06");
        association.setHomeroomIndicator(Boolean.TRUE);
        association.setRepeatIdentifier(RepeatIdentifierType.NOT_REPEATED);
        association.toMap().put("studentId", STUDENT_ID);
        association.toMap().put("sectionId", SECTION_ID);

        return client.postStudentSectionAssociations(TestingConstants.ROGERS_TOKEN, association);
    }

    private StudentSectionAssociation doGetStudentSectionAssociationsById(Level3Client client, final String associationId) throws IOException, StatusCodeException {
        List<StudentSectionAssociation> associations = client.getStudentSectionAssociationsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(associationId); }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(associations.size(), 1);
        StudentSectionAssociation association = associations.get(0);

        return association;
    }

    private void doPutStudentSectionAssociationsById(Level3Client client, final String associationId, final StudentSectionAssociation association) throws IOException,
            StatusCodeException {
        client.putStudentSectionAssociationsById(TestingConstants.ROGERS_TOKEN, associationId, association);
    }

    private void doDeleteStudentSectionAssociationsById(Level3Client client, String associationId) throws IOException, StatusCodeException {
        client.deleteStudentSectionAssociationsById(TestingConstants.ROGERS_TOKEN, associationId);
    }
}
