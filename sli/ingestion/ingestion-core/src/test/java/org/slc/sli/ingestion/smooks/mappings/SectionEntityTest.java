/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.DummyEntityRepository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionEntityTest {

    @Value("${sli.ingestion.recordLevelDeltaEntities}")
    private String recordLevelDeltaEnabledEntityNames;

    @Autowired
    private EntityValidator validator;

    @Autowired
    private DummyEntityRepository repo;

    private Entity makeDummyEntity(final String type, final String id) {
        return new Entity() {

            @Override
            public String getType() {
                return type;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
            }

            @Override
            public String getEntityId() {
                return id;
            }

            @Override
            public Map<String, Object> getBody() {
                return new HashMap<String, Object>();
            }

            @Override
            public CalculatedData<String> getCalculatedValues() {
                return null;
            }

            @Override
            public CalculatedData<Map<String, Integer>> getAggregates() {
                return null;
            }

            @Override
            public Map<String, List<Entity>> getEmbeddedData() {
                return null;
            }

            @Override
            public Map<String, List<Map<String, Object>>> getDenormalizedData() {
                return null;
            }

            @Override
            public String getStagedEntityId() {
                return null;
            }
        };
    }

    String validXmlTestData = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
            + "   <Section>                                                                                    "
            + "       <UniqueSectionCode>UniqueSectionCode0</UniqueSectionCode>                                "
            + "       <SequenceOfCourse>4</SequenceOfCourse>                                                   "
            + "       <EducationalEnvironment>Classroom</EducationalEnvironment>                               "
            + "       <MediumOfInstruction>Televised</MediumOfInstruction>                                     "
            + "       <PopulationServed>Regular Students</PopulationServed>                                    "
            + "       <AvailableCredit CreditType=\"Carnegie unit\" CreditConversion=\"0\">                                                                        "
            + "           <Credit>50.00</Credit>                                                               "
            + "       </AvailableCredit>                                                                       "
            + "       <CourseOfferingReference id=\"ID003\" ref=\"ID001\">                                                                "
            + "           <CourseOfferingIdentity>                                                             "
            + "               <LocalCourseCode>LocalCourseCode0</LocalCourseCode>                              "
            + "               <CourseCode IdentificationSystem=\"CSSC course code\" AssigningOrganizationCode=\"AssigningOrganizationCode1\">                             "
            + "                   <ID>ID0</ID>                                                                 "
            + "               </CourseCode>                                                                    "
            + "               <CourseCode IdentificationSystem=\"CSSC course code\" AssigningOrganizationCode=\"AssigningOrganizationCode3\">                             "
            + "                   <ID>ID1</ID>                                                                 "
            + "               </CourseCode>                                                                    "
            + "               <Term>Fall Semester</Term>                                                       "
            + "               <SchoolYear>1996-1997</SchoolYear>                                               "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID2</ID>                                                                 "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID3</ID>                                                                 "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <StateOrganizationId>StateOrganizationId0</StateOrganizationId>                  "
            + "           </CourseOfferingIdentity>                                                            "
            + "       </CourseOfferingReference>                                                               "
            + "       <SchoolReference id=\"ID005\" ref=\"ID005\">                                                                        "
            + "           <EducationalOrgIdentity>                                                             "
            + "               <StateOrganizationId>StateOrganizationId1</StateOrganizationId>                  "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID4</ID>                                                                 "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID5</ID>                                                                 "
            + "               </EducationOrgIdentificationCode>                                                "
            + "           </EducationalOrgIdentity>                                                            "
            + "       </SchoolReference>                                                                       "
            + "       <SessionReference >                                                                       "
            + "           <SessionIdentity>                                                                    "
            + "               <EducationalOrgReference>                                                         "
            + "                 <EducationalOrgIdentity>                                                              "
            + "                     <StateOrganizationId>StateOrganizationId1</StateOrganizationId>                       "
            + "                 </EducationalOrgIdentity>                                                    "
            + "               </EducationalOrgReference>                                                    "
            + "               <SessionName>SessionName0</SessionName>                                          "
            + "           </SessionIdentity>                                                                   "
            + "       </SessionReference>                                                                      "
            + "       <LocationReference id=\"ID009\" ref=\"ID000\">                                                                      "
            + "           <LocationIdentity>                                                                   "
            + "               <ClassroomIdentificationCode>ClassroomIdentificat</ClassroomIdentificationCode>  "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID10</ID>                                                                "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID11</ID>                                                                "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID12</ID>                                                                "
            + "               </EducationOrgIdentificationCode>                                                "
            + "               <EducationOrgIdentificationCode IdentificationSystem=\"School\">                   "
            + "                   <ID>ID13</ID>                                                                "
            + "               </EducationOrgIdentificationCode>                                                "
            + "           </LocationIdentity>                                                                  "
            + "       </LocationReference>                                                                     "
            + "       <ClassPeriodReference id=\"ID011\" ref=\"ID008\">                                                                   "
            + "           <ClassPeriodIdentity>                                                                "
            + "               <ClassPeriodName>ClassPeriodName0</ClassPeriodName>                              "
            + "               <StateOrganizationId>StateOrganizationId2</StateOrganizationId>                  "
            + "               <StateOrganizationId>StateOrganizationId3</StateOrganizationId>                  "
            + "           </ClassPeriodIdentity>                                                               "
            + "       </ClassPeriodReference>                                                                  "
            + "       <ProgramReference>                                            "
            + "           <ProgramIdentity>                                                                    "
            + "               <ProgramType>Adult/Continuing Education</ProgramType>                            "
            + "           </ProgramIdentity>                                                                   "
            + "       </ProgramReference>                                                                      "
            + "       <ProgramReference>                                            "
            + "           <ProgramIdentity>                                                                    "
            + "               <ProgramId>ProgramId0</ProgramId>                                                "
            + "           </ProgramIdentity>                                                                   "
            + "       </ProgramReference>                                                                      "
            + "   </Section>                                                                                   "
            + "</InterchangeMasterSchedule>";

    public void testValidSection() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String edFiToSliConfig = "smooksEdFi2SLI/section.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData, recordLevelDeltaEnabledEntityNames);
        neutralRecord.setAttributeField("courseOfferingId", "1bce2323211dfds");
        neutralRecord.setAttributeField("SessionReference", "430982345345_id");
        neutralRecord.setAttributeField("schoolId", "StateOrganizationId1");
        SimpleEntity entity = EntityTestUtils.smooksGetSingleSimpleEntity(edFiToSliConfig, neutralRecord);

        Assert.assertNotNull(neutralRecord.getAttributes().get("courseOfferingId"));
        Assert.assertNotNull(neutralRecord.getAttributes().get("schoolId"));
        Assert.assertNotNull(neutralRecord.getAttributes().get("SessionReference"));
        Assert.assertNotNull(neutralRecord.getAttributes().get("ProgramReference"));

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        repo.addEntity("educationOrganization", "StateOrganizationId1",
                makeDummyEntity("educationOrganization", "StateOrganizationId1"));
        repo.addEntity("session", "SessionName0", makeDummyEntity("session", "SessionName0"));
        repo.addEntity("courseOffering", "LocalCourseCode0", makeDummyEntity("courseOffering", "LocalCourseCode0"));
        repo.addEntity("program", "ProgramId0", makeDummyEntity("program", "ProgramId0"));

        PrivateAccessor.setField(validator, "validationRepo", repo);

        EntityTestUtils.mapValidation(entity.getBody(), "section", validator);
    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingUniqueSectionCode() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingUniqueSectionCode = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<CourseOfferingReference>"
                + "<CourseOfferingIdentity>"
                + "<LocalCourseCode>ELA4</LocalCourseCode>"
                + "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>152901001</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                + "<SessionIdentity>"
                + "<SessionName>223</SessionName>"
                + "</SessionIdentity>" + "</SessionReference>" + "</Section>" + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingUniqueSectionCode, recordLevelDeltaEnabledEntityNames);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingSequenceOfCourse() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingSequenceOfCourse = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<CourseOfferingReference>"
                + "<CourseOfferingIdentity>"
                + "<LocalCourseCode>ELA4</LocalCourseCode>"
                + "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>152901001</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                + "<SessionIdentity>"
                + "<SessionName>223</SessionName>"
                + "</SessionIdentity>" + "</SessionReference>" + "</Section>" + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSequenceOfCourse, recordLevelDeltaEnabledEntityNames);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingCourseOfferingReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingCourseOfferingReference = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>152901001</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                + "<SessionIdentity>"
                + "<SessionName>223</SessionName>"
                + "</SessionIdentity>" + "</SessionReference>" + "</Section>" + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingCourseOfferingReference, recordLevelDeltaEnabledEntityNames);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionMissingSchoolReference() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlMissingSchoolReference = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<CourseOfferingReference>"
                + "<CourseOfferingIdentity>"
                + "<LocalCourseCode>ELA4</LocalCourseCode>"
                + "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SessionReference>"
                + "<SessionIdentity>"
                + "<SessionName>223</SessionName>"
                + "</SessionIdentity>"
                + "</SessionReference>"
                + "</Section>"
                + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlMissingSchoolReference, recordLevelDeltaEnabledEntityNames);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Test(expected = EntityValidationException.class)
    public void testInvalidSectionIncorrectEnum() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        String invalidXmlIncorrectEnum = "<InterchangeMasterSchedule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "<Section> "
                + "<UniqueSectionCode>A-ELA4</UniqueSectionCode>"
                + "<SequenceOfCourse>1</SequenceOfCourse>"
                + "<EducationalEnvironment>Mainstrean (Special Education) </EducationalEnvironment>"
                + "<CourseOfferingReference>"
                + "<CourseOfferingIdentity>"
                + "<LocalCourseCode>ELA4</LocalCourseCode>"
                + "</CourseOfferingIdentity>"
                + "</CourseOfferingReference>"
                + "<SchoolReference>"
                + "<EducationalOrgIdentity>"
                + "<StateOrganizationId>152901001</StateOrganizationId>"
                + "</EducationalOrgIdentity>"
                + "</SchoolReference>"
                + "<SessionReference>"
                + "<SessionIdentity>"
                + "<SessionName>223</SessionName>"
                + "</SessionIdentity>"
                + "</SessionReference>"
                + "</Section>"
                + "</InterchangeMasterSchedule>";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                invalidXmlIncorrectEnum, recordLevelDeltaEnabledEntityNames);

        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(neutralRecord.getAttributes());
        when(e.getType()).thenReturn("section");

        validator.validate(e);

    }

    @Ignore
    @Test
    public void testValidSectionCSV() throws Exception {

        String smooksConfig = "smooks_conf/smooks-section-csv.xml";
        String targetSelector = "csv-record";

        String csvTestData = "UniqueSectionCode0,4,Classroom,Televised,Regular Students,Carnegie unit,0.0,50.0,LocalCourseCode0,1,1996-1997,NCES Pilot SNCCS course code,ELU,23,StateOrganizationId1,NCES Pilot SNCCS course code,23,SessionName0,2,1997-1998,ELU,,ProgramId0,Bilingual";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                csvTestData, recordLevelDeltaEnabledEntityNames);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    @Test
    public void testValidSectionXML() throws Exception {
        String smooksConfig = "smooks_conf/smooks-all-xml.xml";
        String targetSelector = "InterchangeMasterSchedule/Section";

        NeutralRecord neutralRecord = EntityTestUtils.smooksGetSingleNeutralRecord(smooksConfig, targetSelector,
                validXmlTestData, recordLevelDeltaEnabledEntityNames);

        checkValidSectionNeutralRecord(neutralRecord);

    }

    @SuppressWarnings("unchecked")
    private void checkValidSectionNeutralRecord(NeutralRecord record) {
        Map<String, Object> entity = record.getAttributes();

        Assert.assertEquals("UniqueSectionCode0", entity.get("uniqueSectionCode"));
        Assert.assertEquals("4", entity.get("sequenceOfCourse").toString());

        Assert.assertEquals("Classroom", entity.get("educationalEnvironment"));
        Assert.assertEquals("Televised", entity.get("mediumOfInstruction"));
        Assert.assertEquals("Regular Students", entity.get("populationServed"));

        Map<String, Object> availableCredit = (Map<String, Object>) entity.get("availableCredit");
        Assert.assertTrue(availableCredit != null);
        Assert.assertEquals("Carnegie unit", availableCredit.get("creditType"));
        Assert.assertEquals("0.0", availableCredit.get("creditConversion").toString());
        Assert.assertEquals("50.0", availableCredit.get("Credit").toString());

        Assert.assertEquals("LocalCourseCode0", ((Map<String, Object>) ((Map<String, Object>) entity
                .get("courseOfferingReference")).get("courseOfferingIdentity")).get("localCourseCode"));

        @SuppressWarnings("unchecked")
        Map<String, Object> schoolRef = (Map<String, Object>) entity.get("SchoolReference");
        assertNotNull(schoolRef);
        @SuppressWarnings("unchecked")
        Map<String, Object> schoolEdOrgId = (Map<String, Object>) schoolRef.get("EducationalOrgIdentity");
        assertNotNull(schoolEdOrgId);
        assertEquals("StateOrganizationId1", schoolEdOrgId.get("StateOrganizationId"));

        Assert.assertEquals("SessionName0", ((Map<String, Object>) ((Map<String, Object>) entity
                .get("SessionReference")).get("SessionIdentity")).get("SessionName"));

/*        List<String> programReferenceList = (List<String>) entity.get("ProgramReference");
        Assert.assertTrue(programReferenceList != null);
        Assert.assertEquals("ProgramId0", programReferenceList.get(0));
*/    }
}
