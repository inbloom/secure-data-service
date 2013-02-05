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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

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
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherSchoolAssociationEntityTest {

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

    String xmlTestData = "<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StaffAssociation.xsd\">"
            + "<TeacherSchoolAssociation>"
            + "<TeacherReference>"
            + "<StaffIdentity>"
            + "<StaffUniqueStateId>333333332</StaffUniqueStateId>"
            + "</StaffIdentity>"
            + "</TeacherReference>"
            + "<SchoolReference>"
            + "<EducationalOrgIdentity>"
            + "<StateOrganizationId>123456111</StateOrganizationId>"
            + "</EducationalOrgIdentity>"
            + "</SchoolReference>"
            + "<ProgramAssignment>Title I-Academic</ProgramAssignment>"
            + "<InstructionalGradeLevels>"
            + "<GradeLevel>Second grade</GradeLevel>"
            + "<GradeLevel>Seventh grade</GradeLevel>"
            + "</InstructionalGradeLevels>"
            + "<AcademicSubjects>"
            + "<AcademicSubject>English</AcademicSubject>"
            + "<AcademicSubject>Mathematics</AcademicSubject>"
            + "</AcademicSubjects>"
            + "</TeacherSchoolAssociation>"
            + "</InterchangeStaffAssociation>";

    @Test
    public void testValidTeacherSchoolAssociationXML() throws IOException, SAXException {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";

        String targetSelector = "InterchangeStaffAssociation/TeacherSchoolAssociation";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                xmlTestData, recordLevelDeltaEnabledEntityNames, mockDIdStrategy, mockDIdResolver);

        checkValidTeacherSchoolAssociationNeutralRecord(neutralRecord);
    }

    @SuppressWarnings("unchecked")
    private void checkValidTeacherSchoolAssociationNeutralRecord(NeutralRecord neutralRecord) {

        Assert.assertEquals("RecordType not teacherSchoolAssociation", "teacherSchoolAssociation",
                neutralRecord.getRecordType());

        Assert.assertNotNull(neutralRecord.getAttributes().get("TeacherReference"));
        Map<String, Object> teacherRef =  (Map<String, Object>) neutralRecord.getAttributes().get("TeacherReference");
        Assert.assertNotNull(teacherRef.get("StaffIdentity"));
        Map<String, Object> staffIdentity =  (Map<String, Object>) teacherRef.get("StaffIdentity");
        Assert.assertEquals("333333332", staffIdentity.get("StaffUniqueStateId"));

        Map<String, Object> schoolRef = (Map<String, Object>) neutralRecord.getAttributes().get("SchoolReference");
        assertNotNull(schoolRef);
        Map<String, Object> schoolEdOrgId = (Map<String, Object>) schoolRef.get("EducationalOrgIdentity");
        assertNotNull(schoolEdOrgId);
        assertEquals("123456111", schoolEdOrgId.get("StateOrganizationId"));

        Assert.assertEquals("Title I-Academic", neutralRecord.getAttributes().get("programAssignment"));

        List<?> gradeLevels = (List<?>) neutralRecord.getAttributes().get("instructionalGradeLevels");
        Assert.assertNotNull("Null gradelevel list", gradeLevels);
        // Assert.assertEquals("gradelevel list does not contain one element", 1,
        // gradelevels.size());
        Assert.assertEquals("Second grade", gradeLevels.get(0));
        if (gradeLevels.size() > 1) {
            // TODO: remove if block when we support csv lists
            Assert.assertEquals("Seventh grade", gradeLevels.get(1));
        }

        List<?> academicSubjectList = (List<?>) neutralRecord.getAttributes().get("academicSubjects");
        Assert.assertNotNull("Null academiSubject list", academicSubjectList);
        Assert.assertEquals("English", academicSubjectList.get(0));
        if (academicSubjectList.size() > 1) {
            // TODO: remove if block when we support csv lists
            Assert.assertEquals("Mathematics", academicSubjectList.get(1));
        }
    }
}
