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
import org.slc.sli.shtick.pojo.CreditType;
import org.slc.sli.shtick.pojo.Credits;
import org.slc.sli.shtick.pojo.EducationalEnvironmentType;
import org.slc.sli.shtick.pojo.MediumOfInstructionType;
import org.slc.sli.shtick.pojo.Section;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * Sample CRUD test using shtick for Section
 * @author chung
 *
**/
public class SectionCrudSampleTest {
    private final String BASE_URL = TestingConstants.BASE_URL;
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private static final String UNIQUE_SECTION_CODE = "Test Section";
    private static final String SCHOOL_ID = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe";
    private static final String COURSE_OFFERING_ID = "00291269-33e0-415e-a0a4-833f0ef38189";

    @Test
    public void testCrud() {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);

        try {
            // CREATE
            final String sectionId = doPostSections(client);
            Assert.assertNotNull(sectionId);

            // READ
            Section section = doGetSectionsById(client, sectionId);

            assertEquals("Should match", sectionId, section.getId());
            assertEquals("Should match", new Integer(1), section.getSequenceOfCourse());
            assertEquals("Should match", EducationalEnvironmentType.CLASSROOM, section.getEducationalEnvironment());
            assertEquals("Should match", MediumOfInstructionType.INDEPENDENT_STUDY, section.getMediumOfInstruction());
            assertEquals("Should match", UNIQUE_SECTION_CODE, section.getUniqueSectionCode());
            assertEquals("Should match", SCHOOL_ID, section.toMap().get("schoolId"));
            assertEquals("Should match", COURSE_OFFERING_ID, section.toMap().get("courseOfferingId"));
            assertEquals("Should match", new Double(3.0), section.getAvailableCredit().getCredit());
            assertEquals("Should match", CreditType.CARNEGIE_UNIT, section.getAvailableCredit().getCreditType());

            // UPDATE
            section.setSequenceOfCourse(new Integer(2));
            Credits credits = section.getAvailableCredit();
            credits.setCreditType(CreditType.SEMESTER_HOUR_CREDIT);
            section.setAvailableCredit(credits);
            doPutSectionsById(client, sectionId, section);

            Section updatedSection = doGetSectionsById(client, sectionId);

            assertEquals("Should match", new Integer(2), updatedSection.getSequenceOfCourse());
            assertEquals("Should match", EducationalEnvironmentType.CLASSROOM, updatedSection.getEducationalEnvironment());
            assertEquals("Should match", MediumOfInstructionType.INDEPENDENT_STUDY, updatedSection.getMediumOfInstruction());
            assertEquals("Should match", UNIQUE_SECTION_CODE, updatedSection.getUniqueSectionCode());
            assertEquals("Should match", SCHOOL_ID, updatedSection.toMap().get("schoolId"));
            assertEquals("Should match", COURSE_OFFERING_ID, updatedSection.toMap().get("courseOfferingId"));
            assertEquals("Should match", new Double(3.0), updatedSection.getAvailableCredit().getCredit());
            assertEquals("Should match", CreditType.SEMESTER_HOUR_CREDIT, updatedSection.getAvailableCredit().getCreditType());

            // DELETE
            doDeleteSectionsById(client, sectionId);
            try {
                doGetSectionsById(client, sectionId);
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

    private String doPostSections(Level3Client client) throws IOException, StatusCodeException {
        Section section = new Section();

        section.setSequenceOfCourse(new Integer(1));
        section.setEducationalEnvironment(EducationalEnvironmentType.CLASSROOM);
        section.setMediumOfInstruction(MediumOfInstructionType.INDEPENDENT_STUDY);
        section.setUniqueSectionCode(UNIQUE_SECTION_CODE);

        //can't do setSchool here - bug in the pojos
        section.toMap().put("schoolId", SCHOOL_ID);
        section.toMap().put("courseOfferingId", COURSE_OFFERING_ID);

        Credits credits = new Credits();
        credits.setCredit(3.0d);
        credits.setCreditType(CreditType.CARNEGIE_UNIT);
        section.setAvailableCredit(credits);

        return client.postSections(TestingConstants.ROGERS_TOKEN, section);
    }

    private Section doGetSectionsById(Level3Client client, final String sectionId) throws IOException, StatusCodeException {
        List<Section> sections = client.getSectionsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(sectionId); }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(sections.size(), 1);
        Section section = sections.get(0);

        return section;
    }

    private void doPutSectionsById(Level3Client client, final String sectionId, final Section section) throws IOException,
            StatusCodeException {
        client.putSectionsById(TestingConstants.ROGERS_TOKEN, sectionId, section);
    }

    private void doDeleteSectionsById(Level3Client client, String sectionId) throws IOException, StatusCodeException {
        client.deleteSectionsById(TestingConstants.ROGERS_TOKEN, sectionId);
    }
}
