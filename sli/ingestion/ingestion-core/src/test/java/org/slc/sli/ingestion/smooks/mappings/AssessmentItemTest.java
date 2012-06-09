package org.slc.sli.ingestion.smooks.mappings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Smooks test for AssessmentItem
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AssessmentItemTest {

    private String validXmlTestData = "<InterchangeAssessmentMetadata xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-AssessmentMetadata.xsd\">"
            + "<AssessmentItem id='test-id'>"
            + "  <IdentificationCode>test-code</IdentificationCode>"
            + "  <ItemCategory>List Question</ItemCategory>"
            + "  <MaxRawScore>100</MaxRawScore>"
            + "  <CorrectResponse>Hello World!</CorrectResponse>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "      <LearningStandardId ContentStandardName='Common Core'>"
            + "        <IdentificationCode>id-code-1</IdentificationCode>"
            + "      </LearningStandardId>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <LearningStandardReference>"
            + "    <LearningStandardIdentity>"
            + "      <LearningStandardId ContentStandardName='Unusual Periphery'>"
            + "        <IdentificationCode>id-code-2</IdentificationCode>"
            + "      </LearningStandardId>"
            + "    </LearningStandardIdentity>"
            + "  </LearningStandardReference>"
            + "  <Nomenclature>nomen</Nomenclature>"
            + "</AssessmentItem>" + "</InterchangeAssessmentMetadata>";


    @SuppressWarnings("unchecked")
    @Test
    public void testLearningObjectiveXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeAssessmentMetadata/AssessmentItem";

        NeutralRecord nr = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, validXmlTestData);
        Map<String, Object> m = nr.getAttributes();
        Assert.assertEquals("test-id", nr.getLocalId());
        Assert.assertEquals("test-code", m.get("identificationCode"));
        Assert.assertEquals("List Question", m.get("itemCategory"));
        Assert.assertEquals(100, m.get("maxRawScore"));
        Assert.assertEquals("Hello World!", m.get("correctResponse"));

        List<Map<String, Object>> refs = (List<Map<String, Object>>) nr.getAttributes().get("learningStandards");
        Assert.assertNotNull(refs);
        Assert.assertEquals(2, refs.size());
        Assert.assertEquals("id-code-1", refs.get(0).get("identificationCode"));
        Assert.assertEquals("Common Core", refs.get(0).get("contentStandardName"));
        Assert.assertEquals("id-code-2", refs.get(1).get("identificationCode"));
        Assert.assertEquals("Unusual Periphery", refs.get(1).get("contentStandardName"));

        Assert.assertEquals("nomen", m.get("nomenclature"));
    }
}
