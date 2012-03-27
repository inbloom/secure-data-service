package org.slc.sli.test.mappingGenerator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slc.sli.test.edfi.entities.*;
import org.slc.sli.test.generators.SchoolGenerator;

public class DataForASchool {
    private static List<String> schoolIds = new ArrayList<String>();
    private static List<SectionInternal> sections = new ArrayList<SectionInternal>();
    private static List<Teacher> teachers = new ArrayList<Teacher>();
    private static List<StaffEducationOrgEmploymentAssociationInternal> staffEducationOrgEmploymentAssociations = new ArrayList<StaffEducationOrgEmploymentAssociationInternal>();
    private static List<TeacherSectionAssociationInternal> teacherSectionAssociations = new ArrayList<TeacherSectionAssociationInternal>();

    private static List<String> studentIds = new ArrayList<String>();
    private static List<String> parentIds = new ArrayList<String>();
    private static List<String> studentParentAssociations = new ArrayList<String>();

    /**
     * @param args
     * @throws JAXBException
     */
    public static void main(String[] args) throws JAXBException {
        prepareData();


        printInterchangeEducationOrganization(System.out);
        printInterchangeMasterSchedule(System.out);
        printInterchangeAssessmentMetadata(System.out);
        printInterchangeStaffAssociation(System.out);
        printInterchangeStudentParent(System.out);
        printInterchangeStudentAssessment(System.out);
        printInterchangeEducationOrgCalendar(System.out);
        printInterchangeStudentEnrollment(System.out);
        printInterchangeStudentGrade(System.out);
        printInterchangeStudentProgram(System.out);
        printInterchangeStudentCohort(System.out);
        printInterchangeStudentDiscipline(System.out);
        printInterchangeStudentAttendance(System.out);
    }

    public static void prepareData() {
        prepareSchool(10);
    }

    public static void prepareSchool(int total) {
        for (int i = 0; i < total; i++) {
            schoolIds.add("School"+i);
        }
    }

    public static void printInterchangeEducationOrganization(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeEducationOrganization.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeEducationOrganization interchangeEducationOrganization = new InterchangeEducationOrganization();
        List<Object> list = interchangeEducationOrganization
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        // schools
        for (String schoolId : schoolIds) {
            School school = SchoolGenerator.generate(schoolId);
            list.add(school);
        }

        marshaller.marshal(interchangeEducationOrganization, ps);
    }

    public static void printInterchangeMasterSchedule(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeMasterSchedule.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeMasterSchedule interchangeMasterSchedule = new InterchangeMasterSchedule();
        List<ComplexObjectType> list = interchangeMasterSchedule.getCourseOfferingOrSectionOrBellSchedule();

        // sections
        // for (SectionInternal sectionId : sections) {
        // Section section = SectionGenerator.generate();
        // list.add(section);
        // }

        marshaller.marshal(interchangeMasterSchedule, ps);

    }

    public static void printInterchangeAssessmentMetadata(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeAssessmentMetadata.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeAssessmentMetadata interchangeAssessmentMetadata = new InterchangeAssessmentMetadata();

        List<ComplexObjectType> list = interchangeAssessmentMetadata
                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();

        // AssessmentFamily
        // Assessment
        // AssessmentPeriodDescriptor
        // PerformanceLevelDescriptor
        // ObjectiveAssessment
        // AssessmentItem
        // LearningObjective
        // LearningStandard

        marshaller.marshal(interchangeAssessmentMetadata, ps);
    }

    public static void printInterchangeStaffAssociation(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStaffAssociation.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStaffAssociation interchangeStaffAssociation = new InterchangeStaffAssociation();

        List<Object> list = interchangeStaffAssociation
                .getStaffOrStaffEducationOrgEmploymentAssociationOrStaffEducationOrgAssignmentAssociation();

        // Staff
        // StaffEducationOrgEmploymentAssociation
        // StaffEducationOrgAssignmentAssociation
        // Teacher
        // TeacherSchoolAssociation
        // TeacherSectionAssociation
        // LeaveEvent
        // OpenStaffPosition
        // CredentialFieldDescriptor

        marshaller.marshal(interchangeStaffAssociation, ps);
    }

    public static void printInterchangeStudentParent(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentParent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentParent interchangeStudentParent = new InterchangeStudentParent();

        List<Object> list = interchangeStudentParent.getStudentOrParentOrStudentParentAssociation();

        // // student
        // for (String studentId : studentIds) {
        // Student student = StudentGenerator.generate(studentId);
        // list.add(student);
        // }
        //
        // // parent
        // for (String parentId : parentIds) {
        // Parent parent = ParentGenerator.generate(parentId);
        // list.add(parent);
        // }
        //
        // // studentParentAssociation
        // for (String studentParentAssociationsId : studentParentAssociations) {
        // StudentParentAssociation studentParentAssociation =
        // StudentParentAssociationGenerator.generate(studentParentAssociationsId);
        // list.add(studentParentAssociation);
        // }

        marshaller.marshal(interchangeStudentParent, ps);
    }

    public static void printInterchangeStudentAssessment(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentAssessment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentAssessment interchangeStudentAssessment = new InterchangeStudentAssessment();

        List<Object> list = interchangeStudentAssessment.getStudentReferenceOrAssessmentReferenceOrStudentAssessment();

        // StudentAssessment
        // StudentObjectiveAssessment
        // StudentAssessmentItem

        marshaller.marshal(interchangeStudentAssessment, ps);
    }

    public static void printInterchangeEducationOrgCalendar(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeEducationOrgCalendar.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeEducationOrgCalendar interchangeEducationOrgCalendar = new InterchangeEducationOrgCalendar();

        List<ComplexObjectType> list = interchangeEducationOrgCalendar.getSessionOrGradingPeriodOrCalendarDate();

        // Session
        // GradingPeriod
        // CalendarDate
        // AcademicWeek

        marshaller.marshal(interchangeEducationOrgCalendar, ps);
    }

    public static void printInterchangeStudentEnrollment(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentEnrollment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentEnrollment interchangeStudentEnrollment = new InterchangeStudentEnrollment();

        List<Object> list = interchangeStudentEnrollment
                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        // StudentSchoolAssociation
        // StudentSectionAssociation
        // GraduationPlan

        marshaller.marshal(interchangeStudentEnrollment, ps);
    }

    public static void printInterchangeStudentGrade(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentGrade.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentGrade interchangeStudentGrade = new InterchangeStudentGrade();

        List<ComplexObjectType> list = interchangeStudentGrade.getStudentAcademicRecordOrCourseTranscriptOrReportCard();

        // StudentAcademicRecord
        // CourseTranscript
        // ReportCard
        // Grade
        // StudentCompetency
        // Diploma
        // GradebookEntry
        // StudentGradebookEntry
        // CompetencyLevelDescriptor
        // LearningObjective
        // StudentCompetencyObjective

        marshaller.marshal(interchangeStudentGrade, ps);
    }

    public static void printInterchangeStudentProgram(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentProgram.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentProgram interchangeStudentProgram = new InterchangeStudentProgram();

        List<Object> list = interchangeStudentProgram
                .getStudentProgramAssociationOrStudentSpecialEdProgramAssociationOrRestraintEvent();

        // StudentProgramAssociation
        // StudentSpecialEdProgramAssociation
        // RestraintEvent
        // StudentCTEProgramAssociation
        // StudentTitleIPartAProgramAssociation
        // ServiceDescriptor

        marshaller.marshal(interchangeStudentProgram, ps);
    }

    public static void printInterchangeStudentCohort(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentCohort.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentCohort interchangeStudentCohort = new InterchangeStudentCohort();

        List<Object> list = interchangeStudentCohort.getCohortOrStudentCohortAssociationOrStaffCohortAssociation();

        // Cohort
        // StudentCohortAssociation
        // StaffCohortAssociation

        marshaller.marshal(interchangeStudentCohort, ps);
    }

    public static void printInterchangeStudentDiscipline(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentDiscipline.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentDiscipline interchangeStudentDiscipline = new InterchangeStudentDiscipline();

        List<Object> list = interchangeStudentDiscipline
                .getDisciplineIncidentOrStudentDisciplineIncidentAssociationOrDisciplineAction();

        // DisciplineIncident
        // StudentDisciplineIncidentAssociation
        // DisciplineAction
        // BehaviorDescriptor
        // DisciplineDescriptor

        marshaller.marshal(interchangeStudentDiscipline, ps);
    }

    public static void printInterchangeStudentAttendance(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentAttendance.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentAttendance InterchangeStudentAttendance = new InterchangeStudentAttendance();

        List<AttendanceEvent> list = InterchangeStudentAttendance.getAttendanceEvent();

        // AttendanceEvent

        marshaller.marshal(InterchangeStudentAttendance, ps);
    }

    class SectionInternal {
        String uniqueSectionCode;
        int sequenceOfCourse;
    }

    class StaffEducationOrgEmploymentAssociationInternal {
        StaffReferenceType staffReference;
        EducationalOrgReferenceType educationOrganizationReference;
        StaffClassificationType staffClassification;
    }

    class TeacherSectionAssociationInternal {
        StaffReferenceType teacherReference;
        SectionReferenceType sectionReference;
        ClassroomPositionType classroomPosition;
    }

    class TeacherSchoolAssociationInternal {
        StaffReferenceType teacherReference;
        List<EducationalOrgReferenceType> schoolReference;
    }

}
