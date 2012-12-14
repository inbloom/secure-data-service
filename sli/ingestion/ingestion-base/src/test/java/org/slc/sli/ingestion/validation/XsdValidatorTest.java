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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.reporting.SimpleSource;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdValidatorTest {

    private final String REF_ERROR_FORMAT = "Attribute 'ref' is not allowed to appear in element '%s'.";

    @Autowired
    private XsdValidator xsdValidator;

    @Autowired
    MemoryMessageReport memoryMessageReport;

    @Test
    public void studentParentInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentParent.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check student reference
        String invalidStudentRefMessage = String.format(REF_ERROR_FORMAT, "StudentReference");
        Assert.assertTrue("Should see warning for student reference",
                containsStringPartial(warnings, invalidStudentRefMessage));

        // Check parent reference
        String invalidParentRefMessage = String.format(REF_ERROR_FORMAT, "ParentReference");
        Assert.assertTrue("Should see warning for student reference",
                containsStringPartial(warnings, invalidParentRefMessage));
    }

    @Test
    public void staffAssociationInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStaffAssociation.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STAFF_ASSOCIATION,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check teacher reference
        String invalidTeacherRefMessage = String.format(REF_ERROR_FORMAT, "TeacherReference");
        Assert.assertTrue("Should see warning for teacher reference",
                containsStringPartial(warnings, invalidTeacherRefMessage));

        // Check school reference
        // TODO update to assertTrue once id-ref for school has been removed
        String invalidSchoolRefMessage = String.format(REF_ERROR_FORMAT, "SchoolReference");
        Assert.assertFalse("Should see warning for school reference",
                containsStringPartial(warnings, invalidSchoolRefMessage));

        // Check section reference
        String invalidSectionRefMessage = String.format(REF_ERROR_FORMAT, "SectionReference");
        Assert.assertTrue("Should see warning for section reference",
                containsStringPartial(warnings, invalidSectionRefMessage));

        // Check edorg reference
        // TODO update to assertTrue once id-ref for edorg has been removed
        String invalidEdOrgRefMessage = String.format(REF_ERROR_FORMAT, "EducationOrgReference");
        Assert.assertFalse("Should see warning for edorg reference",
                containsStringPartial(warnings, invalidEdOrgRefMessage));

        // Check staff reference
        String invalidStaffRefMessage = String.format(REF_ERROR_FORMAT, "StaffReference");
        Assert.assertTrue("Should see warning for section reference",
                containsStringPartial(warnings, invalidStaffRefMessage));
    }

    @Test
    public void assessmentMetadataInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeAssessmentMetadata.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_ASSESSMENT_METADATA,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check learningStandard reference
        // TODO update to assertTrue once id-ref for assessmentFamily has been removed
        String invalidLearningStandardRefMessage = String.format(REF_ERROR_FORMAT, "LearningStandardReference");
        Assert.assertFalse("Should see warning for learningStandard reference",
                containsStringPartial(warnings, invalidLearningStandardRefMessage));

        // Check objectiveAssessment reference
        String invalidObjectiveAssessmentRefMessage = String.format(REF_ERROR_FORMAT, "ObjectiveAssessmentReference");
        Assert.assertTrue("Should see warning for objectiveAssessment reference",
                containsStringPartial(warnings, invalidObjectiveAssessmentRefMessage));

        // Check assessmentFamily reference
        // TODO update to assertTrue once id-ref for assessmentFamily has been removed
        String invalidAssessmentFamilyRefMessage = String.format(REF_ERROR_FORMAT, "AssessmentFamilyReference");
        Assert.assertFalse("Should see warning for assessmentFamily reference",
                containsStringPartial(warnings, invalidAssessmentFamilyRefMessage));
    }

    @Test
    public void educationOrgCalendarInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeEducationOrgCalendar.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_EDUCATION_ORG_CALENDAR,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check gradingPeriod reference
        String invalidGradingPeriodRefMessage = String.format(REF_ERROR_FORMAT, "GradingPeriodReference");
        Assert.assertTrue("Should see warning for gradingPeriod reference",
                containsStringPartial(warnings, invalidGradingPeriodRefMessage));

        // Check calendarDate reference
        String invalidCalendarDateRefMessage = String.format(REF_ERROR_FORMAT, "CalendarDateReference");
        Assert.assertTrue("Should see warning for calendarDate reference",
                containsStringPartial(warnings, invalidCalendarDateRefMessage));

        // Check academicWeek reference
        String invalidAcademicWeekRefMessage = String.format(REF_ERROR_FORMAT, "AcademicWeekReference");
        Assert.assertTrue("Should see warning for academicWeek reference",
                containsStringPartial(warnings, invalidAcademicWeekRefMessage));
    }

    @Test
    public void educationOrganizationInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeEducationOrganization.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_EDUCATION_ORGANIZATION,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check stateEducationAgency reference
        // TODO update to assertTrue once id-ref for edorg has been removed
        String invalidStateEducationAgencyRefMessage = String.format(REF_ERROR_FORMAT, "StateEducationAgencyReference");
        Assert.assertFalse("Should see warning for stateEducationAgency reference",
                containsStringPartial(warnings, invalidStateEducationAgencyRefMessage));

        // Check localEducationAgency reference
        // TODO update to assertTrue once id-ref for edorg has been removed
        String invalidLocalEducationAgencyRefMessage = String.format(REF_ERROR_FORMAT, "LocalEducationAgencyReference");
        Assert.assertFalse("Should see warning for localEducationAgency reference",
                containsStringPartial(warnings, invalidLocalEducationAgencyRefMessage));
    }

    @Test
    public void masterScheduleInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeMasterSchedule.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_MASTER_SCHEDULE,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check session reference
        String invalidSessionRefMessage = String.format(REF_ERROR_FORMAT, "SessionReference");
        Assert.assertTrue("Should see warning for course reference",
                containsStringPartial(warnings, invalidSessionRefMessage));

        // Check course reference
        String invalidCourseRefMessage = String.format(REF_ERROR_FORMAT, "CourseReference");
        Assert.assertTrue("Should see warning for course reference",
                containsStringPartial(warnings, invalidCourseRefMessage));

        // Check courseOffering reference
        String invalidCourseOfferingRefMessage = String.format(REF_ERROR_FORMAT, "CourseOfferingReference");
        Assert.assertTrue("Should see warning for courseOffering reference",
                containsStringPartial(warnings, invalidCourseOfferingRefMessage));
    }

    @Test
    public void studentAssessmentInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentAssessment.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ASSESSMENT,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check assessment reference
        // TODO update to assertTrue once id-ref for assessment has been removed
        String invalidAssessmentRefMessage = String.format(REF_ERROR_FORMAT, "AssessmentReference");
        Assert.assertFalse("Should see warning for invalidAssessmentRefMessage reference",
                containsStringPartial(warnings, invalidAssessmentRefMessage));

        // Check studentAssessment reference
        String invalidStudentAssessmentRefMessage = String.format(REF_ERROR_FORMAT, "StudentAssessmentReference");
        Assert.assertTrue("Should see warning for studentAssessment reference",
                containsStringPartial(warnings, invalidStudentAssessmentRefMessage));

        // Check assessmentItem reference
        String invalidAssessmentItemRefMessage = String.format(REF_ERROR_FORMAT, "AssessmentItemReference");
        Assert.assertTrue("Should see warning for assessmentItem reference",
                containsStringPartial(warnings, invalidAssessmentItemRefMessage));
    }

    @Test
    public void studentDisciplineInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentDiscipline.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_DISCIPLINE,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check disciplineIncident reference
        String invalidDisciplineIncidentRefMessage = String.format(REF_ERROR_FORMAT, "DisciplineIncidentReference");
        Assert.assertTrue("Should see warning for disciplineIncident reference",
                containsStringPartial(warnings, invalidDisciplineIncidentRefMessage));
    }

    @Test
    public void studentEnrollmentInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentEnrollment.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_ENROLLMENT,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check graduationPlan reference
        String invalidGraduationPlanRefMessage = String.format(REF_ERROR_FORMAT, "GraduationPlanReference");
        Assert.assertTrue("Should see warning for graduationPlan reference",
                containsStringPartial(warnings, invalidGraduationPlanRefMessage));
    }

    @Test
    public void studentGradeInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentGrade.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_GRADES,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check reportCard reference
        String invalidReportCardRefMessage = String.format(REF_ERROR_FORMAT, "ReportCardReference");
        Assert.assertTrue("Should see warning for reportCard reference",
                containsStringPartial(warnings, invalidReportCardRefMessage));

        // Check studentAcademicRecord reference
        String invalidStudentAcademicRecordRefMessage = String.format(REF_ERROR_FORMAT,
                "StudentAcademicRecordReference");
        Assert.assertTrue("Should see warning for studentAcademicRecord reference",
                containsStringPartial(warnings, invalidStudentAcademicRecordRefMessage));

        // Check studentSectionAssociation reference
        String invalidStudentSectionAssociationRefMessage = String.format(REF_ERROR_FORMAT,
                "StudentSectionAssociationReference");
        Assert.assertTrue("Should see warning for studentSectionAssociation reference",
                containsStringPartial(warnings, invalidStudentSectionAssociationRefMessage));

        // Check studentCompetency reference
        String invalidStudentCompetencyRefMessage = String.format(REF_ERROR_FORMAT, "StudentCompetencyReference");
        Assert.assertTrue("Should see warning for studentCompetency reference",
                containsStringPartial(warnings, invalidStudentCompetencyRefMessage));

        // Check learningObjective reference
        String invalidLearningObjectiveRefMessage = String.format(REF_ERROR_FORMAT, "LearningObjectiveReference");
        Assert.assertTrue("Should see warning for learningObjective reference",
                containsStringPartial(warnings, invalidLearningObjectiveRefMessage));

        // Check gradebookEntry reference
        String invalidGradebookEntryRefMessage = String.format(REF_ERROR_FORMAT, "GradebookEntryReference");
        Assert.assertTrue("Should see warning for invalidGradebookEntryRefMessage reference",
                containsStringPartial(warnings, invalidGradebookEntryRefMessage));

        // Check grade reference
        String invalidGradeRefMessage = String.format(REF_ERROR_FORMAT, "GradeReference");
        Assert.assertTrue("Should see warning for grade reference",
                containsStringPartial(warnings, invalidGradeRefMessage));

        // Check studentCompetencyObjective reference
        String invalidStudentCompetencyObjectiveRefMessage = String.format(REF_ERROR_FORMAT,
                "StudentCompetencyObjectiveReference");
        Assert.assertTrue("Should see warning for studentCompetencyObjective reference",
                containsStringPartial(warnings, invalidStudentCompetencyObjectiveRefMessage));

        // Check diploma reference
        String invalidDiplomaRefMessage = String.format(REF_ERROR_FORMAT, "DiplomaReference");
        Assert.assertTrue("Should see warning for diploma reference",
                containsStringPartial(warnings, invalidDiplomaRefMessage));
    }

    @Test
    public void studentProgramInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentProgram.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PROGRAM,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check program reference
        String invalidRefMessage = String.format(REF_ERROR_FORMAT, "ProgramReference");
        Assert.assertTrue("Should see warning for invalidRefMessage reference",
                containsStringPartial(warnings, invalidRefMessage));
    }

    @Test
    public void studentCohortInterchangeRefsShouldResultInWarning() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentCohort.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_COHORT,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> warnings = memoryMessageReport.getWarnings();

        // Check cohort reference
        String invalidRefMessage = String.format(REF_ERROR_FORMAT, "CohortReference");
        Assert.assertTrue("Should see warning for cohort reference", containsStringPartial(warnings, invalidRefMessage));
    }

    private boolean containsStringPartial(List<String> messages, String expectedMessage) {
        for (String message : messages) {
            if (message.contains(expectedMessage)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testValidXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Valid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        Assert.assertTrue(xsdValidator.isValid(ife, Mockito.mock(AbstractMessageReport.class),
                Mockito.mock(ReportStats.class)));
    }

    @Test
    public void testInValidXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-InValid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getAbsolutePath(), "");
        ife.setFile(xmlFile);

        ReportStats reportStats = new SimpleReportStats(new SimpleSource(null, null, null));

        xsdValidator.isValid(ife, memoryMessageReport, reportStats);
        Assert.assertTrue(reportStats.hasWarnings());

    }

    @Test
    public void testXmlNotExists() {
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                "XsdValidation/NoFile.xml", "");
        Assert.assertFalse(xsdValidator.isValid(ife, Mockito.mock(AbstractMessageReport.class),
                Mockito.mock(ReportStats.class)));
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

    @Component
    private static class MemoryMessageReport extends AbstractMessageReport {

        private final List<String> errors = new ArrayList<String>();
        private final List<String> warnings = new ArrayList<String>();

        @Override
        protected void reportError(ReportStats reportStats, MessageCode code, Object... args) {
            errors.add(getMessage(code, args));
        }

        @Override
        protected void reportWarning(ReportStats reportStats, MessageCode code, Object... args) {
            warnings.add(getMessage(code, args));
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }
    }
}
