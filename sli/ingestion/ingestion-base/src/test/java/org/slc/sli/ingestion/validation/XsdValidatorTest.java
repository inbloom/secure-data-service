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


package org.slc.sli.ingestion.validation;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdValidatorTest {

    @Autowired
    private XsdValidator xsdValidator;

    @Test
    public void testValidXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Valid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);
        Assert.assertTrue(xsdValidator.isValid(ife, Mockito.mock(ErrorReport.class)));
    }

    @Test
    public void testInValidXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-InValid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);
        FaultsReport faultsReport = new FaultsReport();
        xsdValidator.isValid(ife, faultsReport);
        Assert.assertFalse(faultsReport.getFaults().isEmpty());
    }

    @Test
    public void testXmlNotExists() {
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, "XsdValidation/NoFile.xml", "");
        Assert.assertFalse(xsdValidator.isValid(ife, Mockito.mock(ErrorReport.class)));
    }

    @Test
    public void testLoadXsds() {
        Map<String, Resource> resources = xsdValidator.getXsd();

        Assert.assertNotNull(resources.get("AssessmentMetadata"));
        Assert.assertNotNull(resources.get("EducationOrganization"));
        Assert.assertNotNull(resources.get("EducationOrgCalendar"));
        Assert.assertNotNull(resources.get("HSGeneratedStudentTranscript"));
        Assert.assertNotNull(resources.get("MasterSchedule"));
        Assert.assertNotNull(resources.get("StaffAssociation"));
        Assert.assertNotNull(resources.get("StudentAssessment"));
        Assert.assertNotNull(resources.get("Attendance"));
        Assert.assertNotNull(resources.get("StudentCohort"));
        Assert.assertNotNull(resources.get("StudentDiscipline"));
        Assert.assertNotNull(resources.get("StudentEnrollment"));
        Assert.assertNotNull(resources.get("StudentGrades"));
        Assert.assertNotNull(resources.get("StudentParent"));
        Assert.assertNotNull(resources.get("StudentProgram"));
        Assert.assertNotNull(resources.get("StudentCohort"));
        Assert.assertTrue(resources.get("AssessmentMetadata").exists());
        Assert.assertTrue(resources.get("EducationOrganization").exists());
        Assert.assertTrue(resources.get("EducationOrgCalendar").exists());
        Assert.assertTrue(resources.get("HSGeneratedStudentTranscript").exists());
        Assert.assertTrue(resources.get("MasterSchedule").exists());
        Assert.assertTrue(resources.get("StaffAssociation").exists());
        Assert.assertTrue(resources.get("StudentAssessment").exists());
        Assert.assertTrue(resources.get("Attendance").exists());
        Assert.assertTrue(resources.get("StudentCohort").exists());
        Assert.assertTrue(resources.get("StudentDiscipline").exists());
        Assert.assertTrue(resources.get("StudentEnrollment").exists());
        Assert.assertTrue(resources.get("StudentGrades").exists());
        Assert.assertTrue(resources.get("StudentParent").exists());
        Assert.assertTrue(resources.get("StudentProgram").exists());
        Assert.assertTrue(resources.get("StudentCohort").exists());
    }
}
