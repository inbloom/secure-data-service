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
public class StudentSchoolAssociationEntityTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    String xmlTestData = "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "<StudentSchoolAssociation>"
            + " <StudentReference>"
            + "  <StudentIdentity>"
            + "      <StudentUniqueStateId>900000001</StudentUniqueStateId>"
            + "  </StudentIdentity>"
            + " </StudentReference>"
            + " <SchoolReference>"
            + "  <EducationalOrgIdentity>"
            + "      <StateOrganizationId>990000001</StateOrganizationId>"
            + "  </EducationalOrgIdentity>"
            + " </SchoolReference>"
            + " <EntryDate>2012-01-17</EntryDate>"
            + " <EntryGradeLevel>Eighth grade</EntryGradeLevel>"
            + " <EntryType>Next year school</EntryType>"
            + " <GraduationPlan>Distinguished</GraduationPlan>"
            + " <RepeatGradeIndicator>false</RepeatGradeIndicator>"
            + " <SchoolChoiceTransfer>true</SchoolChoiceTransfer>"
            + " <ExitWithdrawDate>2011-09-12</ExitWithdrawDate>"
            + " <ExitWithdrawType>End of school year</ExitWithdrawType>"
            + "</StudentSchoolAssociation>" + "</InterchangeStudentEnrollment>";

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    private void checkValidSSANeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Map<String, Object> studentRef = (Map<String, Object>) entity.get("StudentReference");
        Assert.assertNotNull(studentRef);
        Map<String, Object> studentIdentity = (Map<String, Object>) studentRef.get("StudentIdentity");
        Assert.assertNotNull(studentIdentity);
        Assert.assertEquals("900000001", studentIdentity.get("StudentUniqueStateId"));

        Map<String, Object> schoolRef = (Map<String, Object>) entity.get("SchoolReference");
        Assert.assertNotNull(schoolRef);
        Map<String, Object> schoolIdentity = (Map<String, Object>) schoolRef.get("EducationalOrgIdentity");
        Assert.assertNotNull(schoolIdentity);
        Assert.assertEquals("990000001", schoolIdentity.get("StateOrganizationId"));

        Assert.assertEquals("Eighth grade", entity.get("EntryGradeLevel"));
        Assert.assertEquals("2012-01-17", entity.get("EntryDate"));
        Assert.assertEquals("Next year school", entity.get("EntryType"));
        Assert.assertEquals("false", entity.get("RepeatGradeIndicator").toString());
        Assert.assertEquals("2011-09-12", entity.get("ExitWithdrawDate"));
        Assert.assertEquals("End of school year", entity.get("ExitWithdrawType"));
        Assert.assertEquals("true", entity.get("SchoolChoiceTransfer").toString());

    }

    @Test
    public void testValidStudentSchoolAssociationXML() throws Exception {

        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStudentEnrollment/StudentSchoolAssociation";

        NeutralRecord record = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector, xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);
        checkValidSSANeutralRecord(record);
    }

}
