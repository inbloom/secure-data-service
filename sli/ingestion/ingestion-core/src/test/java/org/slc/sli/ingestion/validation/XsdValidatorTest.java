package org.slc.sli.ingestion.validation;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    public void testValidXml() throws IOException{
        Resource xmlFile = new ClassPathResource("XsdValidation/InterchangeStudent-Valid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT, xmlFile.getFile().getAbsolutePath(), "");
        Assert.assertTrue(xsdValidator.isValid(ife, Mockito.mock(ErrorReport.class)));
    }

    @Test
    public void testInValidXml() throws IOException{
        Resource xmlFile = new ClassPathResource("XsdValidation/InterchangeStudent-InValid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT, xmlFile.getFile().getAbsolutePath(), "");
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
        Assert.assertNotNull(resources.get("Student"));
        Assert.assertNotNull(resources.get("StudentAssessment"));
        Assert.assertNotNull(resources.get("Attendance"));
        Assert.assertNotNull(resources.get("StudentCohort"));
        Assert.assertNotNull(resources.get("StudentDiscipline"));
        Assert.assertNotNull(resources.get("StudentEnrollment"));
        Assert.assertNotNull(resources.get("StudentGrades"));
        Assert.assertNotNull(resources.get("Parent"));
        Assert.assertNotNull(resources.get("StudentProgram"));

        try {
            resources.get("AssessmentMetadata").getFile();
            resources.get("EducationOrganization").getFile();
            resources.get("EducationOrgCalendar").getFile();
            resources.get("HSGeneratedStudentTranscript").getFile();
            resources.get("MasterSchedule").getFile();
            resources.get("StaffAssociation").getFile();
            resources.get("Student").getFile();
            resources.get("StudentAssessment").getFile();
            resources.get("Attendance").getFile();
            resources.get("StudentCohort").getFile();
            resources.get("StudentDiscipline").getFile();
            resources.get("StudentEnrollment").getFile();
            resources.get("StudentGrades").getFile();
            resources.get("Parent").getFile();
            resources.get("StudentProgram").getFile();
        } catch (IOException e) {

            Assert.fail();
        }

    }
}
