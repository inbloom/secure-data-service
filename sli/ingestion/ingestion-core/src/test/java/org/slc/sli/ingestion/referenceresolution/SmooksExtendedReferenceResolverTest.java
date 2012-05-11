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
import org.junit.Test;
import org.milyn.Smooks;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.IngestionTest;

/**
 *
 * @author tke
 *
 */
public class SmooksExtendedReferenceResolverTest {
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
    public void testResolution() throws IOException, SAXException {
        File input = IngestionTest.getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_input.xml");
        File expected = IngestionTest.getFile("idRefResolutionData/InterchangeAssessmentMetadataAssessmentAssessmentFamilyReference_expected.xml");

        Map<String, Smooks> config = new HashMap<String, Smooks>();
        config.put("/InterchangeStudentAssessment/StudentAssessment/AssessmentReference", new Smooks("idRefResolution/InterchangeStudentAssessment/StudentAssessment/AssessmentReference.xml"));

        referenceFactory.setIdRefConfigs(config);

        test(input, expected, "/InterchangeStudentAssessment/StudentAssessment/AssessmentReference");
    }
}
