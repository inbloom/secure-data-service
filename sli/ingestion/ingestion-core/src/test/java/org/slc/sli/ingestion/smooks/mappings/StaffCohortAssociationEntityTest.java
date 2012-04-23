package org.slc.sli.ingestion.smooks.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 * Test the smooks mappings for StaffCohortAssociation entity.
 *
 * @author slee
 *
 */
public class StaffCohortAssociationEntityTest {

    /**
     * Test that Ed-Fi staffCohortAssociation is correctly mapped to a NeutralRecord.
     *
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStudentCohort/StaffCohortAssociation";

        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/StaffCohortAssociationEntity.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksXmlConfigFilePath,
                targetSelector, edfiXml);

        checkValidNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidNeutralRecord(NeutralRecord neutralRecord) {
        assertEquals("Expecting different record type", "staffCohortAssociation", neutralRecord.getRecordType());

        assertEquals("Expected 0 local parent ids", 0, neutralRecord.getLocalParentIds().size());

        Map<String, Object> attributes = neutralRecord.getAttributes();
        assertEquals("Expected different number of attributes", 5, attributes.size());
        
        assertEquals("Expected different beginDate", "2011-01-01", attributes.get("beginDate"));
        assertEquals("Expected different endDate", "2011-01-01", attributes.get("endDate"));
        assertEquals("Expected different studentRecordAccess", Boolean.TRUE, attributes.get("studentRecordAccess"));        

        Map<String, Object> staffId = ((List<Map<String, Object>>) attributes.get("staffId")).get(0);
        assertNotNull("Expected non-null staffId", staffId);
        Map<String, Object> staffIdentity = (Map<String, Object>) staffId.get("staffIdentity");
        assertNotNull("Expected non-null staffIdentity", staffIdentity);
        assertEquals("Expected different staffUniqueStateId", "100000000", staffIdentity.get("staffUniqueStateId"));

        Map<String, Object> cohortId = ((List<Map<String, Object>>) attributes.get("cohortId")).get(0);
        assertNotNull("Expected non-null cohortId", cohortId);
        Map<String, Object> cohortIdentity = (Map<String, Object>) cohortId.get("cohortIdentity");
        assertNotNull("Expected non-null cohortIdentity", cohortIdentity);
        assertEquals("Expected different cohortIdentifier", "ACC-TEST-COH-1", cohortIdentity.get("cohortIdentifier"));
        List<Object> stateOrganizationId = (List<Object>) cohortIdentity.get("stateOrganizationId");
        assertEquals("Exepcted different number of stateOrganizationId", 1, stateOrganizationId.size());
        assertEquals("Expected different stateOrganizationId", "IL", stateOrganizationId.get(0));
    }

}