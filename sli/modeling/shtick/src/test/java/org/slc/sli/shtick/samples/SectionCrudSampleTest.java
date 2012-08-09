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

/* Sample CRUD test using shtick for Section
* @author chung
*/
public class SectionCrudSampleTest {
    private final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

//    @Test
    public void testCrud() {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);

        try {
            // CREATE
            final String sectionId = doPostSections(client);
            Assert.assertNotNull(sectionId);

            // READ
            Section section = doGetSectionsById(client, sectionId);

            // UPDATE
            section.setSequenceOfCourse(new Integer(2));
            Credits credits = section.getAvailableCredit();
            credits.setCreditType(CreditType.SEMESTER_HOUR_CREDIT);
            section.setAvailableCredit(credits);
            doPutSectionsById(client, sectionId, section);

            // DELETE
            doDeleteSectionsById(client, sectionId);
            try {
                doGetSectionsById(client, sectionId);
            } catch (StatusCodeException e) {
                Assert.assertEquals(e.getStatusCode(), 404);
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

        Credits credits = new Credits();
        credits.setCredit(3.0d);
        credits.setCreditType(CreditType.CARNEGIE_UNIT);
        section.setAvailableCredit(credits);

        // TODO: add references

        return client.postSections(TestingConstants.ROGERS_TOKEN, section);
    }

    private Section doGetSectionsById(Level3Client client, final String sectionId) throws IOException, StatusCodeException {
        List<Section> sections = client.getSectionsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(sectionId); }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(sections.size(), 1);
        Section section = sections.get(0);

        Assert.assertEquals(section.getSequenceOfCourse(), new Integer(1));

        Assert.assertEquals(section.getEducationalEnvironment(), EducationalEnvironmentType.CLASSROOM);

        Assert.assertEquals(section.getMediumOfInstruction(), MediumOfInstructionType.INDEPENDENT_STUDY);

        Credits credits = section.getAvailableCredit();
        Assert.assertEquals(credits.getCredit(), new Double(3.0d));
        Assert.assertEquals(credits.getCreditType(), CreditType.CARNEGIE_UNIT);

        // TODO: add references asserts

        return section;
    }

    private void doPutSectionsById(Level3Client client, final String sectionId, final Section section) throws IOException,
            StatusCodeException {
        client.putSectionsById(TestingConstants.ROGERS_TOKEN, sectionId, section);

        List<Section> sections = client.getSectionsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{ add(sectionId); }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(sections.size(), 1);
        Section updatedSection = sections.get(0);

        Assert.assertEquals(section.getSequenceOfCourse(), new Integer(2));

        Credits credits = section.getAvailableCredit();
        Assert.assertEquals(credits.getCreditType(), CreditType.SEMESTER_HOUR_CREDIT);
    }

    private void doDeleteSectionsById(Level3Client client, String sectionId) throws IOException, StatusCodeException {
        client.deleteSectionsById(TestingConstants.ROGERS_TOKEN, sectionId);
    }
}
