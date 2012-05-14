package org.slc.sli.ingestion.referenceresolution;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.milyn.Smooks;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.IngestionTest;

/**
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksExtendedReferenceResolverTest {

    @Autowired
    SmooksExtendedReferenceResolver referenceFactory = new SmooksExtendedReferenceResolver();

    private void test(File content, File expected, String xpath) throws IOException {
        File result = null;
        try {
            result = referenceFactory.resolve(xpath, content);

            String expectedXML = readFromFile(expected);
            String actualXML = readFromFile(result);

            expectedXML = expectedXML.replaceAll("\\n", "");
            expectedXML = expectedXML.replaceAll("\\s+", "");
            actualXML = actualXML.replaceAll("\\n", "");
            actualXML = actualXML.replaceAll("\\s+", "");

            Assert.assertEquals(expectedXML, actualXML);
        } finally {
            if (result != null) {
                result.delete();
            }
        }
    }

    private String readFromFile(File file) throws IOException {
        FileReader reader = null;

        try {
            reader = new FileReader(file);

            List<String> lines = IOUtils.readLines(reader);

            return StringUtils.join(lines, '\n');
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Test
    @Ignore
    public void testResolutionAllFields() throws IOException, SAXException {
        File input = IngestionTest
                .getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_input.xml");
        File expected = IngestionTest
                .getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_expected.xml");

        Map<String, Smooks> config = new HashMap<String, Smooks>();
        config.put("/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference", new Smooks(
                "idRefResolution/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference.xml"));

        referenceFactory.setIdRefConfigs(config);

        test(input, expected, "/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference");
    }

    @Test
    @Ignore
    public void testAssessmentFamilyReferenceResolutionSomeFields() throws IOException, SAXException {
        File input = IngestionTest.getFile("idRefResolutionData/AssessmentFamilyReference/AssessmentFamilyReference_inputMissingData.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/AssessmentFamilyReference/AssessmentFamilyReference_expectedMissingData.xml");

        Map<String, Smooks> config = new HashMap<String, Smooks>();
        config.put("/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference", new Smooks(
                "idRefResolution/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference.xml"));

        referenceFactory.setIdRefConfigs(config);

        test(input, expected, "/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference");
    }

    @Test
    public void testLearningStandardReferenceResolutionAllFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/LearningStandardReference/LearningStandardReference_input.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/LearningStandardReference/LearningStandardReference_expected.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/AssessmentItem/LearningStandardReference");
    }

    @Test
    public void testLearningStandardReferenceResolutionSomeFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/LearningStandardReference/LearningStandardReference_inputMissingData.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/LearningStandardReference/LearningStandardReference_expectedMissingData.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/AssessmentItem/LearningStandardReference");
    }

    @Test
    public void testLearningObjectiveReferenceResolutionAllFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/LearningObjectiveReference/LearningObjectiveReference_input.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/LearningObjectiveReference/LearningObjectiveReference_expected.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/LearningObjective/LearningObjectiveReference");
    }

    @Test
    public void testLearningObjectiveReferenceResolutionSomeFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/LearningObjectiveReference/LearningObjectiveReference_inputMissingData.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/LearningObjectiveReference/LearningObjectiveReference_expectedMissingData.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/LearningObjective/LearningObjectiveReference");
    }

    @Test
    public void testGradingPeriodReferenceResolutionAllFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/GradingPeriodReference/GradingPeriodReference_input.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/GradingPeriodReference/GradingPeriodReference_expected.xml");
        test(input, expected, "/InterchangeEducationOrgCalender/Session/GradingPeriodReference");
    }

    @Test
    public void testGradingPeriodReferenceResolutionSomeFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/GradingPeriodReference/GradingPeriodReference_inputMissingData.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/GradingPeriodReference/GradingPeriodReference_expectedMissingData.xml");
        test(input, expected, "/InterchangeEducationOrgCalender/Session/GradingPeriodReference");
    }
}
