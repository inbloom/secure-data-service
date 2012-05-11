package org.slc.sli.ingestion.referenceresolution;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    SmooksExtendedReferenceResolver referenceFactory;

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
    public void testResolutionAllFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_input.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_expected.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference");
    }

    @Test
    public void testResolutionSomeFields() throws IOException {
        File input = IngestionTest.getFile("idRefResolutionData/AssessmentFamilyReference_inputMissingData.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/AssessmentFamilyReference_expectedMissingData.xml");
        test(input, expected, "/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference");
    }
}
