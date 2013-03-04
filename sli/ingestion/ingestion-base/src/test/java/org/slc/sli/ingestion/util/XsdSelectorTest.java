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
package org.slc.sli.ingestion.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 *
 * Unit tests the XsdSelector class.
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/validation-context.xml" })
public class XsdSelectorTest {

    @Autowired
    private XsdSelector xsdSelector;

    @Test
    public void testLoadXsds() {
        Map<String, Resource> resources = xsdSelector.getXsdList();

        assertNotNull(resources.get("AssessmentMetadata"));
        assertNotNull(resources.get("EducationOrganization"));
        assertNotNull(resources.get("EducationOrgCalendar"));
        assertNotNull(resources.get("HSGeneratedStudentTranscript"));
        assertNotNull(resources.get("MasterSchedule"));
        assertNotNull(resources.get("StaffAssociation"));
        assertNotNull(resources.get("StudentAssessment"));
        assertNotNull(resources.get("Attendance"));
        assertNotNull(resources.get("StudentCohort"));
        assertNotNull(resources.get("StudentDiscipline"));
        assertNotNull(resources.get("StudentEnrollment"));
        assertNotNull(resources.get("StudentGrades"));
        assertNotNull(resources.get("StudentParent"));
        assertNotNull(resources.get("StudentProgram"));
        assertNotNull(resources.get("StudentCohort"));
        assertTrue(resources.get("AssessmentMetadata").exists());
        assertTrue(resources.get("EducationOrganization").exists());
        assertTrue(resources.get("EducationOrgCalendar").exists());
        assertTrue(resources.get("HSGeneratedStudentTranscript").exists());
        assertTrue(resources.get("MasterSchedule").exists());
        assertTrue(resources.get("StaffAssociation").exists());
        assertTrue(resources.get("StudentAssessment").exists());
        assertTrue(resources.get("Attendance").exists());
        assertTrue(resources.get("StudentCohort").exists());
        assertTrue(resources.get("StudentDiscipline").exists());
        assertTrue(resources.get("StudentEnrollment").exists());
        assertTrue(resources.get("StudentGrades").exists());
        assertTrue(resources.get("StudentParent").exists());
        assertTrue(resources.get("StudentProgram").exists());
        assertTrue(resources.get("StudentCohort").exists());
    }

    @Test
    public void testValidXmlFile() {
        final String xmlFileName = "SLI-InterchangeSchool.xml";
        final String parentDir = "dummylz";
        final String checksum = "e1e10";
        final String expectedXsdResource = "SLI-Interchange-EducationOrganization.xsd";
        IngestionFileEntry xmlFile = new IngestionFileEntry(parentDir, FileFormat.EDFI_XML,
                FileType.XML_EDUCATION_ORGANIZATION, xmlFileName, checksum);
        Resource xsdResource = xsdSelector.provideXsdResource(xmlFile);
        assertEquals("XSD resource mismatch", expectedXsdResource, xsdResource.getFilename());
    }

    @Test
    public void testInvalidXmlFile() {
        final String xmlFileName = "SLI-InterchangeSchool.xml";
        final String parentDir = "dummylz";
        final String checksum = "e1e10";
        IngestionFileEntry xmlFile = new IngestionFileEntry(parentDir, FileFormat.EDFI_XML,
                FileType.CONTROL, xmlFileName, checksum);
        Resource xsdResource = xsdSelector.provideXsdResource(xmlFile);
        assertNull("XSD resource should be null", xsdResource);
    }

}
