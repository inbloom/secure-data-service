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


package org.slc.sli.test.generators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.AcademicHonor;
import org.slc.sli.test.edfi.entities.AcademicHonorsType;
import org.slc.sli.test.edfi.entities.AdditionalCreditType;
import org.slc.sli.test.edfi.entities.AdditionalCredits;
import org.slc.sli.test.edfi.entities.ClassRanking;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptorType;
import org.slc.sli.test.edfi.entities.CourseAttemptResultType;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.CourseRepeatCodeType;
import org.slc.sli.test.edfi.entities.CourseTranscript;
import org.slc.sli.test.edfi.entities.CreditType;
import org.slc.sli.test.edfi.entities.Credits;
import org.slc.sli.test.edfi.entities.Diploma;
import org.slc.sli.test.edfi.entities.DiplomaLevelType;
import org.slc.sli.test.edfi.entities.DiplomaType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.Grade;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeType;
import org.slc.sli.test.edfi.entities.GradebookEntry;
import org.slc.sli.test.edfi.entities.GradingPeriod;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.MethodCreditEarnedType;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.Recognition;
import org.slc.sli.test.edfi.entities.RecognitionType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.ReportCard;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.StudentAcademicRecord;
import org.slc.sli.test.edfi.entities.StudentCompetency;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentGradebookEntry;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationIdentityType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationReferenceType;

public class StudentGradeGenerator {

    private static Random rand = new Random();
    private static String thisDay, oneYearAgo, oneYearHence;
    private static int idCount = 0;

    private static final int MAX_DAYS_ABSENT = 10;
    private static final int MAX_DAYS_ATTENDANCE = 100;
    private static final int MAX_DAYS_TARDY = 20;

    private static final String[] GRADES = {"A", "B", "C", "D", "E", "F"};

    static {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        thisDay = dateFormatter.format(new Date());
        oneYearAgo = dateFormatter.format(new Date(new Date().getTime() - 365 * 24 * 60 * 60 * 1000));
        oneYearHence = dateFormatter.format(new Date(new Date().getTime() + 365 * 24 * 60 * 60 * 1000));
    }

    private static int getRand() {
        int num = rand.nextInt();
        return num < 0 ? -1 * num : num;
    }

    public static StudentSectionAssociationReferenceType getStudentSectionAssociationReference(
            StudentReferenceType student, SectionReferenceType section) {
        StudentSectionAssociationReferenceType ssaRef = new StudentSectionAssociationReferenceType();
        StudentSectionAssociationIdentityType ssaIdentity = new StudentSectionAssociationIdentityType();
        ssaRef.setStudentSectionAssociationIdentity(ssaIdentity);
        ssaIdentity.setStudentIdentity(student.getStudentIdentity());
        ssaIdentity.setSectionIdentity(section.getSectionIdentity());
        return ssaRef;
    }

    public static StudentAcademicRecord getStudentAcademicRecord(StudentReferenceType studentRef,
            SessionReferenceType sessionRef, List<ReferenceType> reportCardRef, ReferenceType diplomaRef) {
        StudentAcademicRecord sar = new StudentAcademicRecord();
        Credits earned = new Credits();
        sar.setCumulativeCreditsEarned(earned);
        earned.setCredit(new BigDecimal(3));
        earned.setCreditType(CreditType.CARNEGIE_UNIT);
        earned.setCreditConversion(new BigDecimal(1));

        Credits attempted = new Credits();
        sar.setCumulativeCreditsAttempted(attempted);
        attempted.setCredit(new BigDecimal(3));
        attempted.setCreditType(CreditType.CARNEGIE_UNIT);
        attempted.setCreditConversion(new BigDecimal(1));

        sar.setCumulativeGradePointsEarned(new BigDecimal(getRand() % 3));
        sar.setCumulativeGradePointAverage(new BigDecimal(getRand() % 3));
        sar.setGradeValueQualifier("GradeValueQualifier 90-100%=A, 80-90%=B,  70-80%=C 60-70%=d");

        ClassRanking ranking = new ClassRanking();
        sar.setClassRanking(ranking);
        ranking.setClassRank(getRand() % 20);
        ranking.setClassRankingDate(thisDay);
        ranking.setPercentageRanking(getRand() % 70);
        ranking.setTotalNumberInClass(30);

        AcademicHonor honors = new AcademicHonor();
        sar.getAcademicHonors().add(honors);
        honors.setAcademicHonorsType(AcademicHonorsType.ATTENDANCE_AWARD);
        honors.setHonorsDescription("Good Attendance Honor");
        honors.setHonorAwardDate(oneYearAgo);

        Recognition recognitions = new Recognition();
        sar.getRecognitions().add(recognitions);
        recognitions.setRecognitionType(RecognitionType.ATHLETIC_AWARDS);
        recognitions.setRecognitionDescription("Best Athlete Recognition");
        recognitions.setRecognitionAwardDate(thisDay);

        sar.setProjectedGraduationDate(oneYearHence);

        if (studentRef != null)
            sar.setStudentReference(studentRef);
        if (sessionRef != null)
            sar.setSessionReference(sessionRef);
        if (reportCardRef != null)
            sar.getReportCardReference().addAll(reportCardRef);
        if (diplomaRef != null)
            sar.setDiplomaReference(diplomaRef);
        return sar;
    }

    public static ReportCard getReportCard(StudentReferenceType studentRef,
            GradingPeriodReferenceType gradingPeriodRef, List<ReferenceType> gradeReference,
            List<ReferenceType> scReference) {
        ReportCard reportCard = new ReportCard();
        if (gradeReference != null)
            reportCard.getGradeReference().addAll(gradeReference);
        if (scReference != null)
            reportCard.getStudentCompetencyReference().addAll(scReference);
        if (studentRef != null)
            reportCard.setStudentReference(studentRef);
        if (gradeReference != null)
            reportCard.setGradingPeriodReference(gradingPeriodRef);
        reportCard.setGPAGivenGradingPeriod(new BigDecimal(1));
        reportCard.setGPACumulative(new BigDecimal(1));
        reportCard.setNumberOfDaysAbsent(new BigDecimal(getRand() % MAX_DAYS_ABSENT));
        reportCard.setNumberOfDaysInAttendance(new BigDecimal(getRand() % MAX_DAYS_ATTENDANCE));
        reportCard.setNumberOfDaysTardy(new Integer(getRand() % MAX_DAYS_TARDY));
        return reportCard;
    }

    public static Grade getGrade(StudentSectionAssociationReferenceType ssaRef,
            GradingPeriodReferenceType gradingPeriodRef) {
        
        Grade grade = new Grade();
        grade.setLetterGradeEarned(GRADES[rand.nextInt(GRADES.length)]);
        grade.setNumericGradeEarned(new BigInteger("3"));
        grade.setDiagnosticStatement("Grade Exam Taken");
        grade.setGradeType(GradeType.EXAM);
        grade.setPerformanceBaseConversion(PerformanceBaseType.BASIC);
        if (ssaRef != null)
            grade.setStudentSectionAssociationReference(ssaRef);
        if (gradingPeriodRef != null)
            grade.setGradingPeriodReference(gradingPeriodRef);
        return grade;
    }

    public static StudentCompetency getStudentCompetency(StudentSectionAssociationReferenceType ssaRef,
            LearningObjectiveReferenceType learningObjectiveRef, StudentCompetencyObjectiveReferenceType scoRef) {
        StudentCompetency studentCompetancy = new StudentCompetency();
        CompetencyLevelDescriptorType cl = new CompetencyLevelDescriptorType();
        studentCompetancy.setCompetencyLevel(cl);
//        cl.getCodeValueOrDescription()
//                .add(objectFactory
//                        .createCompetencyLevelDescriptorTypeCodeValue("Competency Level Descriptor Type Code or Value"));
        cl.setCodeValue("Competency Level Descriptor Code Value");
        cl.setDescription("Competency Level Description");
        studentCompetancy.setDiagnosticStatement("DiagnosticStatement for Student Competancy");
        if (ssaRef != null)
            studentCompetancy.setStudentSectionAssociationReference(ssaRef);
        if (learningObjectiveRef != null)
            studentCompetancy.setLearningObjectiveReference(learningObjectiveRef);
        if (scoRef != null)
            studentCompetancy.setStudentCompetencyObjectiveReference(scoRef);
        return studentCompetancy;
    }

    public static Diploma getDiploma(EducationalOrgReferenceType schoolRef) {
        Diploma diploma = new Diploma();
        diploma.setDiplomaAwardDate(thisDay);
        diploma.setDiplomaAwardDate("2003-02-01");
        diploma.setDiplomaLevel(DiplomaLevelType.DISTINGUISHED);
        diploma.setDiplomaType(DiplomaType.CERTIFICATE_OF_ATTENDANCE);
        diploma.setCTECompleter(true);

        AcademicHonor honors = new AcademicHonor();
        diploma.getAcademicHonors().add(honors);
        honors.setAcademicHonorsType(AcademicHonorsType.ATTENDANCE_AWARD);
        honors.setHonorsDescription("HonorsDescription");
        honors.setHonorAwardDate(thisDay);

        Recognition recognitions = new Recognition();
        diploma.getRecognitions().add(recognitions);
        recognitions.setRecognitionType(RecognitionType.AWARDING_OF_UNITS_OF_VALUE);
        recognitions.setRecognitionDescription("RecognitionDescription");
        recognitions.setRecognitionAwardDate(thisDay);

        if (diploma != null)
            diploma.setSchoolReference(schoolRef);
        return diploma;
    }

    public static CourseTranscript getCourseTranscript(CourseReferenceType courseRef, ReferenceType academicRecordRef,
            EducationalOrgReferenceType school) {
        CourseTranscript courseTranscript = new CourseTranscript();
        courseTranscript.setCourseAttemptResult(CourseAttemptResultType.PASS);

        Credits creditsAttempted = new Credits();
        courseTranscript.setCreditsAttempted(creditsAttempted);
        creditsAttempted.setCredit(new BigDecimal(300));
        creditsAttempted.setCreditType(CreditType.CARNEGIE_UNIT);
        creditsAttempted.setCreditConversion(new BigDecimal(1));

        Credits creditsEarned = new Credits();
        courseTranscript.setCreditsEarned(creditsEarned);
        creditsEarned.setCredit(new BigDecimal(100));
        creditsEarned.setCreditType(CreditType.CARNEGIE_UNIT);
        creditsEarned.setCreditConversion(new BigDecimal(5));

        AdditionalCredits additionalCredits = new AdditionalCredits();
        courseTranscript.getAdditionalCreditsEarned().add(additionalCredits);
        additionalCredits.setCredit(new BigDecimal(100));
        additionalCredits.setAdditionalCreditType(AdditionalCreditType.AP);

        courseTranscript.setGradeLevelWhenTaken(GradeLevelType.EIGHTH_GRADE);
        courseTranscript.setMethodCreditEarned(MethodCreditEarnedType.ADULT_EDUCATION_CREDIT);
        courseTranscript.setFinalLetterGradeEarned(GRADES[rand.nextInt(GRADES.length)]);
        courseTranscript.setCourseRepeatCode(CourseRepeatCodeType.REPEAT_COUNTED);

        courseTranscript.setCourseReference(courseRef);
        courseTranscript.getEducationOrganizationReference().add(school);

        courseTranscript.setStudentAcademicRecordReference(academicRecordRef);
        return courseTranscript;
    }

    public static GradebookEntry getGradeBookEntry(GradingPeriodReferenceType gradingPeriodRef,
            SectionReferenceType sectionRef) {
        idCount++;
        GradebookEntry gbe = new GradebookEntry();
        gbe.setDateAssigned(thisDay);

        String gradeBookEntry = "Quiz Test".split(" ")[rand.nextInt(2)] + " " + idCount;
        gbe.setDescription(gradeBookEntry);
        gbe.setGradebookEntryType(gradeBookEntry);
        gbe.setGradingPeriodReference(gradingPeriodRef);
        gbe.setSectionReference(sectionRef);
        return gbe;
    }


    public static StudentGradebookEntry getStudentGradebookEntry(SectionReferenceType section,
            StudentReferenceType student) {
        StudentGradebookEntry sgbe = new StudentGradebookEntry();
        sgbe.setDateFulfilled(thisDay);
        sgbe.setLetterGradeEarned(GRADES[rand.nextInt(GRADES.length)]);
        sgbe.setNumericGradeEarned(BigInteger.ONE);

        CompetencyLevelDescriptorType cld = new CompetencyLevelDescriptorType();
        sgbe.setCompetencyLevel(cld);
        sgbe.setDiagnosticStatement("Diagnostic Statement");

        StudentSectionAssociationReferenceType ssRef = new StudentSectionAssociationReferenceType();
        StudentSectionAssociationIdentityType ssIdentity = new StudentSectionAssociationIdentityType();
        ssRef.setStudentSectionAssociationIdentity(ssIdentity);
        ssIdentity.setSectionIdentity(section.getSectionIdentity());
        ssIdentity.setStudentIdentity(student.getStudentIdentity());

        sgbe.setStudentSectionAssociationReference(ssRef);
        return sgbe;

    }

    // public static void main(String [] args) throws Exception
    // {
    // /**
    // * Elements that need to be serialized
    // * sea
    // * lea
    // * school
    // * courses
    // * sessions
    // * sections
    // * learningObjective
    // * scObjectives
    // * gradingPeriod
    // * gradeBookEntries
    // * #### grade and studentCompetancy is contained inside reportCard so they do not have to be
    // serialized seperately
    // * reportCard
    // * diploma
    // * courseTranscript
    // * academicRecord
    // * courseTranscript
    // */
    //
    // EducationAgencyGenerator edOrgGenerator = new EducationAgencyGenerator();
    // StateEducationAgency sea = edOrgGenerator.getSEA("NewYorkEdOrg");
    // EducationalOrgReferenceType seaRef = edOrgGenerator.getEducationalOrgReferenceType(sea);
    // LocalEducationAgency lea = edOrgGenerator.getLEA("ManhattanEdOrg");
    // EducationalOrgReferenceType leaRef = edOrgGenerator.getEducationalOrgReferenceType(lea);
    // lea.setStateEducationAgencyReference(seaRef);
    // SchoolGenerator schoolGenerator = new SchoolGenerator(StateAbbreviationType.NY);
    // School school = schoolGenerator.getSchool("School-Id");
    // EducationalOrgReferenceType schoolRef =
    // edOrgGenerator.getEducationalOrgReferenceType(school);
    // EducationOrgIdentificationCode edOrgCode =
    // (EducationOrgIdentificationCode)schoolRef.getEducationalOrgIdentity().
    // getStateOrganizationIdOrEducationOrgIdentificationCode().get(0);
    //
    // StudentReferenceType studentRef = StudentGenerator.getStudentReferenceType("9822389841");
    //
    // CourseGenerator courseGenerator = new CourseGenerator(GradeLevelType.EIGHTH_GRADE);
    // int COURSE_COUNT = 1;
    // Course [] courses = new Course[COURSE_COUNT];
    // CourseReferenceType [] courseRefs = new CourseReferenceType[COURSE_COUNT];
    // for(int i = 0; i < COURSE_COUNT; i++){
    // courses[i] = courseGenerator.getCourse("Course" + i);
    // courses[i].setEducationOrganizationReference(schoolRef);
    // courseRefs[i] = courseGenerator.getCourseReferenceType(courses[i]);
    // }
    //
    // SessionGenerator sessionGenerator = new SessionGenerator();
    // int SESSION_COUNT = 1;
    // Session [] sessions = new Session[SESSION_COUNT];
    // SessionReferenceType [] sessionRefs = new SessionReferenceType[SESSION_COUNT];
    // List<String> stateId = new ArrayList<String>();
    // stateId.add("New-York-Ed-Org");
    // for(int i = 0; i < SESSION_COUNT; i++){
    // sessions[i] = sessionGenerator.sessionGenerator(stateId);
    // sessions[i].setEducationOrganizationReference(schoolRef);/**Changed School For Session**/
    //
    // sessionRefs[i] = SessionGenerator.getSessinReferenceType(sessions[i]);
    // }
    //
    // int SECTION_COUNT = 1;
    // Section [] sections = new Section[SECTION_COUNT];
    // SectionReferenceType [] sectionRefs = new SectionReferenceType[SECTION_COUNT];
    // for(int i = 0; i < SECTION_COUNT; i++){
    // sections[i] = SectionGenerator.generate("sectionCode " + i, 1, "School-Id");
    // sections[i].setSchoolReference(schoolRef); /**Changed School For Section**/
    //
    // sectionRefs[i] = SectionGenerator.getSectionReference(sections[i]);
    // }
    //
    // LearningObjectiveGenerator learningObGenerator = new LearningObjectiveGenerator();
    // int LEARNING_OBJECTIVE_COUNT = 1;
    // LearningObjective [] learningObjectives = new LearningObjective[LEARNING_OBJECTIVE_COUNT];
    // LearningObjectiveReferenceType [] learningObjectiveRefs = new
    // LearningObjectiveReferenceType[LEARNING_OBJECTIVE_COUNT];
    // for(int i = 0; i < LEARNING_OBJECTIVE_COUNT ; i++){
    // learningObjectives[i] = learningObGenerator.getLearningObjective("LOID" + i);
    // learningObjectiveRefs[i] =
    // learningObGenerator.getLearningObjectiveReferenceType(learningObjectives[i]);
    // }
    //
    // StudentCompetancyObjectiveGenerator scoGenerator = new StudentCompetancyObjectiveGenerator();
    // int STUDENT_COMPETANCY_COUNT = 1;
    // StudentCompetencyObjective[] scObjectives = new
    // StudentCompetencyObjective[STUDENT_COMPETANCY_COUNT];
    // StudentCompetencyObjectiveReferenceType[] scObjectiveRefs = new
    // StudentCompetencyObjectiveReferenceType[STUDENT_COMPETANCY_COUNT];
    // for(int i = 0; i < STUDENT_COMPETANCY_COUNT; i++){
    // scObjectives[i] = scoGenerator.getStudentCompetencyObjective("SCOID" + i, schoolRef);
    // scObjectiveRefs[i] =
    // scoGenerator.getStudentCompetencyObjectiveReferenceType(scObjectives[i]);
    // }
    //
    // StudentGradeGenerator studentGradeGenerator = new StudentGradeGenerator();
    // GradingPeriod gradingPeriod = studentGradeGenerator.getGradingPeriod();
    // GradingPeriodReferenceType gradingPeriodRef =
    // studentGradeGenerator.getGradingPeriodReferenceType(gradingPeriod, edOrgCode );
    // int GRADE_BOOK_ENTRY_COUNT = 1;
    // GradebookEntry [] gradeBookEntries = new GradebookEntry[GRADE_BOOK_ENTRY_COUNT];
    // for(int i = 0; i < GRADE_BOOK_ENTRY_COUNT; i++){
    // gradeBookEntries[i] = studentGradeGenerator.getGradeBookEntry(gradingPeriodRef,
    // sectionRefs[0]);
    // }
    //
    // StudentSectionAssociationReferenceType ssaRef =
    // studentGradeGenerator.getStudentSectionAssociationReference(studentRef, sectionRefs[0]);
    //
    // Grade grade = studentGradeGenerator.getGrade(ssaRef, gradingPeriodRef);
    // grade.setId("grade1Id");
    // ReferenceType gradeReference = new ReferenceType();
    // gradeReference.setRef(grade);
    //
    // StudentCompetency studentCompetancy = studentGradeGenerator.getStudentCompetency(ssaRef,
    // learningObjectiveRefs[0], scObjectiveRefs[0]);
    // studentCompetancy.setId("studentCompetancy1Id");
    // ReferenceType scoReference = new ReferenceType();
    // scoReference.setRef(studentCompetancy);
    //
    // ReportCard reportCard = studentGradeGenerator.getReportCard(studentRef, gradingPeriodRef,
    // null, null);
    // reportCard.setId("reportCard1Id");
    //
    // Diploma diploma = studentGradeGenerator.getDiploma(schoolRef);
    // diploma.setId("diploma1Id");
    // ReferenceType diplomaRef = new ReferenceType();
    // diplomaRef.setRef(diploma);
    // ReferenceType reportCardRef = new ReferenceType();
    // reportCardRef.setRef(reportCard);
    // List<ReferenceType> reportCardRefs = new ArrayList<ReferenceType>();
    // reportCardRefs.add(reportCardRef);
    // StudentAcademicRecord academicRecord =
    // studentGradeGenerator.getStudentAcademicRecord(studentRef, sessionRefs[0], reportCardRefs,
    // diplomaRef);
    // ReferenceType academicRecordReference = new ReferenceType();
    // academicRecordReference.setRef(academicRecord);
    //
    // CourseTranscript courseTranscript = studentGradeGenerator.getCourseTranscript(courseRefs[0],
    // academicRecordReference, schoolRef);
    //
    // }

}
