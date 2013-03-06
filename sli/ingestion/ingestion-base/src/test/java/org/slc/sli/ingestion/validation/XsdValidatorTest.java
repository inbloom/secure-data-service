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

package org.slc.sli.ingestion.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdValidatorTest {

    private static final String REF_WARNING = "Attribute 'ref' is not allowed to appear in element '%s'.";
    private static final String INVALID_CONTENT_WARNING = "Invalid content was found starting with element '%s'.";
    private static final String INCOMPLETE_CONTENT_WARNING = "The content of element '%s' is not complete.";
    private static final String MISSING_DECLARATION_WARNING = "Cannot find the declaration of element '%s'.";

    private static final String MISSING_FILE_ERROR = "File %s: Not found.";
    private static final String FILE_IO_ERROR = "File %s: Problem reading file.";

    @Autowired
    private XsdValidator xsdValidator;

    @Autowired
    MemoryMessageReport memoryMessageReport;

    @Test
    public void testUnreadableXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Unreadable.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION, xmlFile.getName(), "");

        String resourceId = "theResourceId";
        JobSource jobSource = new JobSource(resourceId);
        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, jobSource);

        Assert.assertTrue(reportStats.hasErrors());

        List<String> errors = memoryMessageReport.getErrors();

        // Check StateOrganizationId content.
        String errorMessage = String.format(FILE_IO_ERROR, ife.getFileName());
        Assert.assertTrue("Should see error for XML file reading problem", containsStringPartial(errors, errorMessage));
    }

    @Test
    public void staffAssociationInterchangeShouldBeValid() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStaffAssociation.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STAFF_ASSOCIATION, xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertFalse(reportStats.hasWarnings());
    }

    @Test
    public void studentAttendanceInterchangeWarningReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentAttendance.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_ATTENDANCE, xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check LearningStandardId content.
        String errorMessage = String.format(INCOMPLETE_CONTENT_WARNING, "AttendanceEvent");
        Assert.assertTrue("Should see warning for incomplete AttendanceEvent",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void assessmentMetadataInterchangeWarningReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeAssessmentMetadata.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_ASSESSMENT_METADATA, xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check LearningStandardId content.
        String errorMessage = String.format(INVALID_CONTENT_WARNING, "LearningStandardId");
        Assert.assertTrue("Should see warning for invalid LearningStandardIdentity content",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void educationOrgCalendarInterchangeWarningReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeEducationOrgCalendar.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_EDUCATION_ORG_CALENDAR, xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check CalendarDateReference completeness.
        String errorMessage = String.format(INCOMPLETE_CONTENT_WARNING, "CalendarDateReference");
        Assert.assertTrue("Should see warning for incomplete CalendarDateReference",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void educationOrganizationInterchangeShouldBeValid() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeEducationOrganization.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_EDUCATION_ORGANIZATION,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertFalse(reportStats.hasWarnings());
    }

    @Test
    public void masterScheduleInterchangeWarningsReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeMasterSchedule.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_MASTER_SCHEDULE,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check CourseCode content.
        String errorMessage = String.format(INVALID_CONTENT_WARNING, "CourseCode");
        Assert.assertTrue("Should see warning for invalid CourseCode content",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void studentAssessmentInterchangeShouldBeValid() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentAssessment.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_ASSESSMENT,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertFalse(reportStats.hasWarnings());
    }

    @Test
    public void studentDisciplineInterchangeShouldBeValid() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentDiscipline.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_DISCIPLINE,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertFalse(reportStats.hasWarnings());
    }

    @Test
    public void studentEnrollmentInterchangeWarningsReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentEnrollment.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_ENROLLMENT,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check graduationPlan reference
        String invalidGraduationPlanRefMessage = String.format(REF_WARNING, "GraduationPlanReference");
        Assert.assertTrue("Should see warning for graduationPlan reference",
                containsStringPartial(errors, invalidGraduationPlanRefMessage));
    }

    @Test
    public void studentGradeInterchangeWarningsReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentGrade.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_GRADES,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check StudentIdentity content.
        String errorMessage = String.format(INVALID_CONTENT_WARNING, "StudentIdentity");
        Assert.assertTrue("Should see warning for invalid StudentIdentity content",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void studentProgramInterchangeWarningsReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentProgram.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PROGRAM,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check StateOrganizationId content.
        String errorMessage = String.format(INVALID_CONTENT_WARNING, "StateOrganizationId");
        Assert.assertTrue("Should see warning for invalid StateOrganizationId content",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void studentCohortInterchangeWarningsReportedAsError() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudentCohort.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_COHORT,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check StateOrganizationId content.
        String errorMessage = String.format(INVALID_CONTENT_WARNING, "StateOrganizationId");
        Assert.assertTrue("Should see warning for invalid StateOrganizationId content",
                containsStringPartial(errors, errorMessage));
    }

    private boolean containsStringPartial(List<String> messages, String expectedMessage) {
        for (String message : messages) {
            if (message.contains(expectedMessage)) {
                return true;
            }
        }

        Assert.assertEquals(expectedMessage, messages.toString());
        return false;
    }

    @Test
    public void testValidXml() throws IOException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Valid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getName(), "");

        Assert.assertTrue(xsdValidator.isValid(ife, Mockito.mock(AbstractMessageReport.class),
                Mockito.mock(ReportStats.class), null));
    }

    @Test
    public void testInValidXml() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-InValid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(xmlFile.getParentFile().getAbsolutePath(),
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getName(), "");

        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, null);

        Assert.assertTrue(reportStats.hasWarnings());

        List<String> errors = memoryMessageReport.getErrors();

        // Check for InterchangeStudent declaration.
        String errorMessage = String.format(MISSING_DECLARATION_WARNING, "InterchangeStudent");
        Assert.assertTrue("Should see warning for missing InterchangeStudent declaration",
                containsStringPartial(errors, errorMessage));
    }

    @Test
    public void testXmlNotExists() {
        IngestionFileEntry ife = new IngestionFileEntry("",
                FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                "XsdValidation/NoFile.xml", "");

        String resourceId = "theResourceId";
        JobSource jobSource = new JobSource(resourceId);
        ReportStats reportStats = new SimpleReportStats();

        xsdValidator.isValid(ife, memoryMessageReport, reportStats, jobSource);
        Assert.assertTrue(reportStats.hasErrors());

        List<String> errors = memoryMessageReport.getErrors();

        // Check StateOrganizationId content.
        String errorMessage = String.format(MISSING_FILE_ERROR, ife.getFileName());
        Assert.assertTrue("Should see error for missing XML file", containsStringPartial(errors, errorMessage));
    }

    @Component
    private static class MemoryMessageReport extends AbstractMessageReport {

        private final List<String> errors = new ArrayList<String>();
        private final List<String> warnings = new ArrayList<String>();

        @Override
        protected void reportError(ReportStats reportStats, Source source, MessageCode code, Object... args) {
            errors.add(getMessage(reportStats, source, code, args));
        }

        @Override
        protected void reportWarning(ReportStats reportStats, Source source, MessageCode code, Object... args) {
            warnings.add(getMessage(reportStats, source, code, args));
        }

        @Override
        protected void reportInfo(ReportStats reportStats, Source source, MessageCode code, Object... args) {
            // Do Nothing
        }

        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }

    }
}
