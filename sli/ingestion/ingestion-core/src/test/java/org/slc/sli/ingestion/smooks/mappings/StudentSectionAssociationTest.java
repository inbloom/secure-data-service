/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.smooks.mappings;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 *
 * @author mpatel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSectionAssociationTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    String xmlTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSectionAssociation>"

            + "<SectionReference>"
            + "<SectionIdentity>"
            + "<EducationalOrgReference>"
            + "<EducationalOrgIdentity>"
            + "<StateOrganizationId>State Organization Id</StateOrganizationId>"
            + "</EducationalOrgIdentity>"
            + "</EducationalOrgReference>"
            + "<UniqueSectionCode>MT100</UniqueSectionCode>"
            + "</SectionIdentity>"
            + "</SectionReference>"

            + "<StudentReference>"
            + "<StudentIdentity>"
            + "<StudentUniqueStateId>111220001</StudentUniqueStateId>"
            + "</StudentIdentity>"
            + "</StudentReference>"

            + " <BeginDate>2009-09-15</BeginDate>"
            + " <EndDate>2010-06-02</EndDate>"
            + " <HomeroomIndicator>false</HomeroomIndicator>" + " <RepeatIdentifier>Not repeated</RepeatIdentifier>"

            + "</StudentSectionAssociation></InterchangeStudentEnrollment>";

    String invalidXMLTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSectionAssociation>"
            + "<SectionReference>"
            + "<SectionIdentity>"
            + "<EducationalOrgReference>"
            + "<EducationalOrgIdentity>"
            + "<StateOrganizationId>State Organization Id</StateOrganizationId>"
            + "</EducationalOrgIdentity>"
            + "</EducationalOrgReference>"
            + "<UniqueSectionCode>MT100</UniqueSectionCode>"
            + "</SectionIdentity>"
            + "</SectionReference>"
            + " <BeginDate>2009-09-15</BeginDate>"
            + " <HomeroomIndicator>false</HomeroomIndicator>"
            + "</StudentSectionAssociation></InterchangeStudentEnrollment>";

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXMLTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);
        checkInValidSectionNeutralRecord(record);
    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSectionAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);
        checkValidSectionNeutralRecord(record);
    }

    @SuppressWarnings("unchecked")
    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals("111220001", ((Map<String, Object>) ((Map<String, Object>) entity.get("StudentReference"))
                .get("StudentIdentity")).get("StudentUniqueStateId"));

        Assert.assertNotNull(entity.get("SectionReference"));
        Assert.assertNotNull(((Map<String, Object>) entity.get("SectionReference")).get("SectionIdentity"));
        Assert.assertEquals("MT100", ((Map<String, Object>) ((Map<String, Object>) entity.get("SectionReference"))
                .get("SectionIdentity")).get("UniqueSectionCode"));

        Assert.assertEquals("2009-09-15", entity.get("beginDate"));
        Assert.assertEquals("2010-06-02", entity.get("endDate"));
        Assert.assertEquals("false", entity.get("homeroomIndicator").toString());
        Assert.assertEquals("Not repeated", entity.get("repeatIdentifier"));
    }

    @SuppressWarnings("unchecked")
    private void checkInValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();
        Assert.assertEquals(null, entity.get("StudentReference"));
        Assert.assertNotNull(entity.get("SectionReference"));
        Assert.assertNotNull(((Map<String, Object>) entity.get("SectionReference")).get("SectionIdentity"));

        Assert.assertEquals("MT100", ((Map<String, Object>) ((Map<String, Object>) entity.get("SectionReference"))
                .get("SectionIdentity")).get("UniqueSectionCode"));
        Assert.assertEquals("2009-09-15", entity.get("beginDate"));
        Assert.assertEquals(null, entity.get("endDate"));
        Assert.assertEquals("false", entity.get("homeroomIndicator").toString());
    }
}
