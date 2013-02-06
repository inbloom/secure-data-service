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

import static org.mockito.Matchers.any;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/recordLvlHash-context.xml" })
public class TeacherSectionAssociationEntityTest {

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

    String xmlTestData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">"

            + "<TeacherSectionAssociation>"
            + "<TeacherReference>"
            + "<StaffIdentity>"
            + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
            + "<Name>"
            + "<PersonalTitlePrefix>Ms</PersonalTitlePrefix>"
            + "<FirstName>Jane</FirstName>"
            + "<MiddleName>Sarah</MiddleName>"
            + "<LastSurname>Smith</LastSurname>"
            + "<GenerationCodeSuffix>III</GenerationCodeSuffix>"
            + "<MaidenName>Jimenez</MaidenName>"
            + "</Name>"
            + "<OtherName OtherNameType=\"Alias\">"
            + "<PersonalTitlePrefix>Ms</PersonalTitlePrefix>"
            + "<FirstName>Jo</FirstName>"
            + "<MiddleName>Gannon</MiddleName>"
            + "<LastSurname>Grant</LastSurname>"
            + "<GenerationCodeSuffix>II</GenerationCodeSuffix>"
            + "</OtherName>"
            + "<Sex>Female</Sex>"
            + "<BirthDate>1999-07-12</BirthDate>"
            + "<HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>"
            + "<Race>"
            + "<RacialCategory>White</RacialCategory>"
            + "</Race>"
            + "<Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">"
            + "<TelephoneNumber>410-555-0248</TelephoneNumber>"
            + "</Telephone>"
            + "<ElectronicMail EmailAddressType=\"Home/Personal\">"
            + "<EmailAddress>sjsmith@email.com</EmailAddress>"
            + "</ElectronicMail>"
            + "<StaffIdentificationCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
            + "<Id>23</Id>"
            + "</StaffIdentificationCode>"
            + "</StaffIdentity>"
            + "</TeacherReference>"
            + "<SectionReference>"
            + "<SectionIdentity>"
            + "<UniqueSectionCode>123456111</UniqueSectionCode>"
            + "<LocalCourseCode>MATH1</LocalCourseCode>"
            + "<SchoolYear>2010-2011</SchoolYear>"
            + "<Term>Summer Semester</Term>"
            + "<ClassPeriodName>A03</ClassPeriodName>"
            + "<Location>CC100</Location>"
            + "<CourseCode IdentificationSystem=\"NCES Pilot SNCCS course code\" AssigningOrganizationCode=\"ELU\">"
            + "<Id>23</Id>"
            + "</CourseCode>"
            + "</SectionIdentity>"
            + "</SectionReference>"
            + "<ClassroomPosition>Teacher of Record</ClassroomPosition>"
            + "<BeginDate>1998-01-01</BeginDate>"
            + "<EndDate>2008-01-01</EndDate>"
            + "<HighlyQualifiedTeacher>true</HighlyQualifiedTeacher>"
            + "</TeacherSectionAssociation>" + "</InterchangeStaffAssociation>";

    @Test
    public void testValidTeacherSectionAssociationXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeStaffAssociation/TeacherSectionAssociation";

        Mockito.when(mockDIdStrategy.generateId(any(NaturalKeyDescriptor.class))).thenReturn("mockId");

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidTeacherSectionAssociationNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidTeacherSectionAssociationNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertNotNull(entity.get("TeacherReference"));
        Map<String, Object> teacherRef =  (Map<String, Object>) entity.get("TeacherReference");
        Assert.assertNotNull(teacherRef.get("StaffIdentity"));
        Map<String, Object> staffIdentity =  (Map<String, Object>) teacherRef.get("StaffIdentity");
        Assert.assertEquals("333333332", staffIdentity.get("StaffUniqueStateId"));

        Assert.assertNotNull(entity.get("SectionReference"));
        Assert.assertNotNull(((Map<String, Object>) entity.get("SectionReference")).get("SectionIdentity"));

        Assert.assertEquals("123456111", ((Map<String, Object>) ((Map<String, Object>) entity.get("SectionReference"))
                .get("SectionIdentity")).get("UniqueSectionCode"));

        Assert.assertEquals("Teacher of Record", entity.get("classroomPosition"));
        Assert.assertEquals("1998-01-01", entity.get("beginDate"));
        Assert.assertEquals("2008-01-01", entity.get("endDate"));
        Assert.assertEquals("true", entity.get("highlyQualifiedTeacher").toString());

    }
}
