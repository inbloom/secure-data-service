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
import org.slc.sli.shtick.pojo.GradingPeriod;
import org.slc.sli.shtick.pojo.GradingPeriodIdentityType;
import org.slc.sli.shtick.pojo.GradingPeriodType;
import org.slc.sli.shtick.pojo.SchoolYearType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Sample CRUD test using shtick for GradingPeriod
 *
 * @author srupasinghe
 *
 */
public class GradingPeriodCrudSampleTest {
    private final String BASE_URL = "http://local.slidev.org:8080/api/rest/v1";
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    private static final String SCHOOL_ID = "6756e2b9-aba1-4336-80b8-4a5dde3c63fe";

    @Test
    public void testCrud() {
        final Level2Client inner = new StandardLevel2Client(BASE_URL, new JsonLevel1Client());
        final Level3Client client = new StandardLevel3Client(inner);

        try {
            // CREATE
            final String gradingPeriodId = doPostGradingPeriods(client);
            Assert.assertNotNull(gradingPeriodId);

            // READ
            GradingPeriod gradingPeriod = doGetGradingPeriodsById(client, gradingPeriodId);

            assertEquals("Should match", gradingPeriodId, gradingPeriod.getId());
            assertEquals("Should match", "2012-01-01", gradingPeriod.getBeginDate());
            assertEquals("Should match", "2012-06-06", gradingPeriod.getEndDate());
            assertEquals("Should match", new Integer(20), gradingPeriod.getTotalInstructionalDays());
            assertEquals("Should match", GradingPeriodType.FIFTH_SIX_WEEKS, gradingPeriod.getGradingPeriodIdentity().getGradingPeriod());
            assertEquals("Should match", SCHOOL_ID, gradingPeriod.getGradingPeriodIdentity().toMap().get("schoolId"));
            assertEquals("Should match", SchoolYearType.FUDGED_2000_HYPHEN_2001, gradingPeriod.getGradingPeriodIdentity().getSchoolYear());

            // UPDATE
            gradingPeriod.setBeginDate("2011-12-10");
            gradingPeriod.setTotalInstructionalDays(30);
            doPutGradingPeriodsById(client, gradingPeriodId, gradingPeriod);

            GradingPeriod updatedGradingPeriod = doGetGradingPeriodsById(client, gradingPeriodId);

            assertEquals("Should match", gradingPeriodId, updatedGradingPeriod.getId());
            assertEquals("Should match", "2011-12-10", updatedGradingPeriod.getBeginDate());
            assertEquals("Should match", "2012-06-06", updatedGradingPeriod.getEndDate());
            assertEquals("Should match", new Integer(30), gradingPeriod.getTotalInstructionalDays());
            assertEquals("Should match", GradingPeriodType.FIFTH_SIX_WEEKS, updatedGradingPeriod.getGradingPeriodIdentity().getGradingPeriod());
            assertEquals("Should match", SCHOOL_ID, gradingPeriod.getGradingPeriodIdentity().toMap().get("schoolId"));
            assertEquals("Should match", SchoolYearType.FUDGED_2000_HYPHEN_2001, updatedGradingPeriod.getGradingPeriodIdentity().getSchoolYear());

            // DELETE
            doDeleteGradingPeriodsById(client, gradingPeriodId);
            try {
                doGetGradingPeriodsById(client, gradingPeriodId);
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

    private String doPostGradingPeriods(Level3Client client) throws IOException, StatusCodeException {
        GradingPeriod gradingPeriod = new GradingPeriod();

        GradingPeriodIdentityType identityType = new GradingPeriodIdentityType();
        identityType.setGradingPeriod(GradingPeriodType.FIFTH_SIX_WEEKS);
        identityType.toMap().put("schoolId", SCHOOL_ID);
        identityType.setSchoolYear(SchoolYearType.FUDGED_2000_HYPHEN_2001);

        gradingPeriod.setBeginDate("2012-01-01");
        gradingPeriod.setEndDate("2012-06-06");
        gradingPeriod.setGradingPeriodIdentity(identityType);
        gradingPeriod.setTotalInstructionalDays(20);

        return client.postGradingPeriods(TestingConstants.ROGERS_TOKEN, gradingPeriod);
    }

    private GradingPeriod doGetGradingPeriodsById(Level3Client client, final String gradingPeriodId) throws IOException, StatusCodeException {
        List<GradingPeriod> gradingPeriods = client.getGradingPeriodsById(TestingConstants.ROGERS_TOKEN,
                new ArrayList<String>() {{
                    add(gradingPeriodId);
                }}, EMPTY_QUERY_ARGS);
        Assert.assertEquals(gradingPeriods.size(), 1);
        GradingPeriod gradingPeriod = gradingPeriods.get(0);

        return gradingPeriod;
    }

    private void doPutGradingPeriodsById(Level3Client client, final String gradingPeriodId, final GradingPeriod gradingPeriod) throws IOException,
            StatusCodeException {
        client.putGradingPeriodsById(TestingConstants.ROGERS_TOKEN, gradingPeriodId, gradingPeriod);
    }

    private void doDeleteGradingPeriodsById(Level3Client client, String gradingPeriodId) throws IOException, StatusCodeException {
        client.deleteGradingPeriodsById(TestingConstants.ROGERS_TOKEN, gradingPeriodId);
    }
}
