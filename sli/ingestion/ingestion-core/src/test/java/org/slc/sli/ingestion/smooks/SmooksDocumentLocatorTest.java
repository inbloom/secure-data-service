package org.slc.sli.ingestion.smooks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * Tests for SliSmooks DocumentLocator
 *
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksDocumentLocatorTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;

    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test that Ed-Fi program is correctly mapped to a NeutralRecord.
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public final void mapEdfiXmlToNeutralRecordTest() throws IOException, SAXException {

        String smooksXmlConfigFilePath = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/StaffEducationOrgAssignmentAssociation";
        String edfiXml = null;

        try {
            edfiXml = EntityTestUtils.readResourceAsString("smooks/unitTestData/InterchangeStaffAssociation.xml");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            Assert.fail();
        }

        List<NeutralRecord> nrs = EntityTestUtils
                .smooksGetNeutralRecords(smooksXmlConfigFilePath,
                        targetSelector, edfiXml, recordLevelDeltaEnabledEntityNames, 
                        mockDIdStrategy, mockDIdResolver);

        Assert.assertEquals(2, nrs.size());
        NeutralRecord nr = nrs.get(0);
        Assert.assertEquals(72579, nr.getVisitBeforeLineNumber());
        Assert.assertEquals(45, nr.getVisitBeforeColumnNumber());
        Assert.assertEquals(72592, nr.getVisitAfterLineNumber());
        Assert.assertEquals(46, nr.getVisitAfterColumnNumber());
        nr = nrs.get(1);
        Assert.assertEquals(72593, nr.getVisitBeforeLineNumber());
        Assert.assertEquals(45, nr.getVisitBeforeColumnNumber());
        Assert.assertEquals(72606, nr.getVisitAfterLineNumber());
        Assert.assertEquals(46, nr.getVisitAfterColumnNumber());
    }

}
