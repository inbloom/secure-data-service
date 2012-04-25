package org.slc.sli.ingestion.smooks.mappings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LearningObjectiveTest {

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<LearningObjective>"
            + "  <Objective>objective text</Objective>"
            + "  <Description>description</Description>"
            + "  <AcademicSubject>ELA</AcademicSubject>"
            + "  <ObjectiveGradeLevel>Twelfth grade</ObjectiveGradeLevel>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "      <Objective>objective</Objective>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "  <LearningObjectiveReference ref=\"lo-ref-1\"></LearningObjectiveReference>"
            + "  <LearningObjectiveReference>"
            + "    <LearningObjectiveIdentity>"
            + "      <LearningObjectiveId>"
            + "        <IdentificationCode ContentStandardName=\"standard_name\">objective_id</IdentificationCode>"
            + "      </LearningObjectiveId>"
            + "    </LearningObjectiveIdentity>"
            + "  </LearningObjectiveReference>"
            + "</LearningObjective>" + "</InterchangeAssessmentMetadata>";

    @SuppressWarnings("unchecked")
    @Test
    public void testLearningObjectiveXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/LearningObjective";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);
        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("objective text", m.get("objective"));
        Assert.assertEquals("description", m.get("description"));
        Assert.assertEquals("ELA", m.get("academicSubject"));
        Assert.assertEquals("Twelfth grade", m.get("objectiveGradeLevel"));
        List<Map<String, Object>> learningObjectives = (List<Map<String, Object>>) m.get("learningObjectiveRefs");
        Assert.assertNotNull(learningObjectives);
        Assert.assertEquals(3, learningObjectives.size());

        assertInList(learningObjectives, "ref", "lo-ref-1");
        assertInList(learningObjectives, "objective", "objective");
        assertInList(learningObjectives, "learningObjectiveId.identificationCode", "objective_id");
        assertInList(learningObjectives, "learningObjectiveId.contentStandardName", "standard_name");
    }

    @Test
    @Ignore
    public void testInvalidObjectiveAssessmentXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/LearningObjective";

        String invalidXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
                + "<ObjectiveAssessment id=\"TAKSReading3-4\">"
                + "<MaxRawScore>8</MaxRawScore>"
                + "<PercentOfAssessment>50</PercentOfAssessment>"
                + "<Nomenclature>nomenclature</Nomenclature>"
                + "</ObjectiveAssessment>" + "</InterchangeAssessmentMetadata>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlTestData);

    }


    private static void assertInList(List<Map<String, Object>> maps, String field, String value) {
        for (Map<String, Object> m : maps) {
            if (field.indexOf(".") > -1) {
                String v = getByPath(field, m);
                if (v != null && v.equals(value)) {
                    return;
                }
            } else if (value.equals(m.get(field))) {
                return;
            }
        }
        Assert.fail("Did not find " + field + "=" + value + " in list");
    }

    @SuppressWarnings("unchecked")
    private static String getByPath(String name, Map<String, Object> map) {
        // how many times have I written this code? Not enough, I say!
        String[] path = name.split("\\.");
        for (int i = 0; i < path.length; i++) {
            Object obj = map.get(path[i]);
            if (obj == null) {
                return null;
            } else if (i == path.length - 1 && obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof Map) {
                map = (Map<String, Object>) obj;
            } else {
                return null;
            }
        }
        return null;
    }
}
