package org.slc.sli.test.mappingGenerator;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slc.sli.test.edfi.entities.*;
import org.slc.sli.test.generators.ParentGenerator;
import org.slc.sli.test.generators.SchoolGenerator;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.TeacherGenerator;
import org.slc.sli.test.generators.TeacherSchoolAssociationGenerator;
import org.slc.sli.test.generators.TeacherSectionAssociationGenerator;
import org.slc.sli.test.mappingGenerator.internals.*;
import org.slc.sli.test.validator.ValidateSchema;

public class DataForASchool {
    private String prefix = "a";
    private Random random = new Random();

    private List<String> schools = new ArrayList<String>();

    private List<SectionInternal> sections = new ArrayList<SectionInternal>();

//    private List<StaffEducationOrgEmploymentAssociationInternal> staffEducationOrgEmploymentAssociations = new ArrayList<StaffEducationOrgEmploymentAssociationInternal>();

    private List<String> teachers = new ArrayList<String>();
    private List<TeacherSchoolAssociationInternal> teacherSchoolAssociations = new ArrayList<TeacherSchoolAssociationInternal>();
    private List<TeacherSectionAssociationInternal> teacherSectionAssociations = new ArrayList<TeacherSectionAssociationInternal>();

    private List<String> students = new ArrayList<String>();
    private List<String> parents = new ArrayList<String>();
    private List<String> studentParentAssociations = new ArrayList<String>();


    private List<StudentSchoolAssociationInternal> studentSchoolAssociations = new ArrayList<StudentSchoolAssociationInternal>();

    /**
     * @param args
     * @throws JAXBException
     */
    public static void main(String[] args) {
        DataForASchool data = new DataForASchool();
        data.prepareData();
        data.printOnScreen();

        String path = "data";

        data.saveInterchanges(path);
        data.validateInterchanges(path);
    }

    public void saveInterchanges(String path) {
        try {
            printInterchangeEducationOrganization(new PrintStream(path + "/InterchangeEducationOrganization.xml"));
            printInterchangeMasterSchedule(new PrintStream(path + "/InterchangeMasterSchedule.xml"));
            printInterchangeAssessmentMetadata(new PrintStream(path + "/InterchangeAssessmentMetadata.xml"));
            printInterchangeStaffAssociation(new PrintStream(path + "/InterchangeStaffAssociation.xml"));
            printInterchangeStudentParent(new PrintStream(path + "/InterchangeStudentParent.xml"));
            printInterchangeStudentAssessment(new PrintStream(path + "/InterchangeStudentAssessment.xml"));
            printInterchangeEducationOrgCalendar(new PrintStream(path + "/InterchangeEducationOrgCalendar.xml"));
            printInterchangeStudentEnrollment(new PrintStream(path + "/InterchangeStudentEnrollment.xml"));
            printInterchangeStudentGrade(new PrintStream(path + "/InterchangeStudentGrade.xml"));
            printInterchangeStudentProgram(new PrintStream(path + "/InterchangeStudentProgram.xml"));
            printInterchangeStudentCohort(new PrintStream(path + "/InterchangeStudentCohort.xml"));
            printInterchangeStudentDiscipline(new PrintStream(path + "/InterchangeStudentDiscipline.xml"));
            printInterchangeStudentAttendance(new PrintStream(path + "/InterchangeStudentAttendance.xml"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("DONE");
    }

    public void printOnScreen() {
        try {
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
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void validateInterchanges(String path) {
        try {
            ValidateSchema vs = new ValidateSchema();
            String xmlDir = "./" + path + "/";
            vs.check(xmlDir);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void prepareData() {
        prepareSchool(2);
        prepareTeacher(2);
        prepareTeacherSchoolAssociation();
        prepareSection(4);
        prepareTeacherSectionAssociation();
        prepareStudent(2);
        prepareParent(4);
        prepareStudentSchoolAssociation();
    }

    public void prepareSchool(int total) {
        for (int i = 0; i < total; i++) {
            schools.add(this.prefix + "-School-"+i);
        }
    }

    public void prepareTeacher(int total) {
        for (int i = 0; i < total; i++) {
            teachers.add(this.prefix + "-teacher-" + i);
        }
    }

    public void prepareTeacherSchoolAssociation() {
        Random random = new Random();
        for (String teacherId : teachers) {
            TeacherSchoolAssociationInternal tsa = new TeacherSchoolAssociationInternal();
            tsa.teacherId = teacherId;
            tsa.schoolIds.add(schools.get(random.nextInt(schools.size())));

            teacherSchoolAssociations.add(tsa);
        }
    }

    public void prepareSection(int sectionPerSchool) {
        int sectionNumber = sectionPerSchool/4;
        for (String school : schools) {
            for (int i = 0; i < sectionNumber; i++) {
                String sectionCode = (this.prefix + "-" + UUID.randomUUID().toString()).substring(0, 30);
                for (int j = 0; j < 4; j++) {
                    SectionInternal si = new SectionInternal();
                    si.schoolId = school;
                    si.sectionCode = sectionCode;
                    si.sequenceOfCourse = j;
                    sections.add(si);
                }
            }
        }
    }

    public void prepareTeacherSectionAssociation() {
        Random r = new Random();
        for(String teacher : teachers) {
            TeacherSectionAssociationInternal tsai = new TeacherSectionAssociationInternal();
            tsai.teacherId = teacher;
            tsai.section = sections.get(r.nextInt(sections.size()));
            teacherSectionAssociations.add(tsai);
        }
    }

    public void prepareStudent(int total) {
        for (int i = 0 ; i < total ; i++) {
            students.add(this.prefix + "-student-" + i);
        }
    }

    public void prepareParent(int total) {
        for (int i = 0 ; i < total ; i++) {
            parents.add(this.prefix + "-parent-" + i);
        }
    }

    public void prepareStudentSchoolAssociation() {
        for (String student : students) {
            StudentSchoolAssociationInternal ssai = new StudentSchoolAssociationInternal();
            ssai.student = student;
            ssai.school = schools.get(random.nextInt(schools.size()));
            studentSchoolAssociations.add(ssai);
        }
    }

    public void printInterchangeEducationOrganization(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeEducationOrganization.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeEducationOrganization interchangeEducationOrganization = new InterchangeEducationOrganization();
        List<Object> list = interchangeEducationOrganization
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        // schools
        SchoolGenerator sg = new SchoolGenerator(StateAbbreviationType.NY);

        for (String schoolId : schools) {
            School school = sg.getSchool(schoolId);
            list.add(school);
        }

        marshaller.marshal(interchangeEducationOrganization, ps);
    }

    public void printInterchangeMasterSchedule(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeMasterSchedule.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeMasterSchedule interchangeMasterSchedule = new InterchangeMasterSchedule();
        List<ComplexObjectType> list = interchangeMasterSchedule.getCourseOfferingOrSectionOrBellSchedule();

        // sections
        for (SectionInternal si : sections) {
            list.add(SectionGenerator.generate(si.sectionCode, si.sequenceOfCourse, si.schoolId));
        }

        marshaller.marshal(interchangeMasterSchedule, ps);

    }

    public void printInterchangeAssessmentMetadata(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStaffAssociation(PrintStream ps) throws JAXBException {
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
        TeacherGenerator tg = new TeacherGenerator(StateAbbreviationType.NY);
        for (String teacherId : teachers) {
            list.add(tg.generate(teacherId));
        }

        // TeacherSchoolAssociation
        for (TeacherSchoolAssociationInternal tsai : teacherSchoolAssociations) {
            list.add(TeacherSchoolAssociationGenerator.generate(tsai.teacherId, tsai.schoolIds));
        }

        // TeacherSectionAssociation
        for (TeacherSectionAssociationInternal tsai : teacherSectionAssociations) {
            list.add(TeacherSectionAssociationGenerator.generate(tsai.teacherId, tsai.section.schoolId, tsai.section.sectionCode));
        }

        // LeaveEvent
        // OpenStaffPosition
        // CredentialFieldDescriptor

        marshaller.marshal(interchangeStaffAssociation, ps);
    }

    public void printInterchangeStudentParent(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentParent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentParent interchangeStudentParent = new InterchangeStudentParent();

        List<Object> list = interchangeStudentParent.getStudentOrParentOrStudentParentAssociation();

        // student
        StudentGenerator sg = new StudentGenerator(StateAbbreviationType.NY);
        for (String studentId : students) {
            Student student = sg.generate(studentId);
            list.add(student);
        }

                // // parent
        ParentGenerator pg = new ParentGenerator(StateAbbreviationType.NY);
        for (String parentId : parents) {
        	Parent parent = pg.generate(parentId);
        	list.add(parent);
        }
        //
        // // studentParentAssociation
        // for (String studentParentAssociationsId : studentParentAssociations) {
        // StudentParentAssociation studentParentAssociation =
        // StudentParentAssociationGenerator.generate(studentParentAssociationsId);
        // list.add(studentParentAssociation);
        // }

        marshaller.marshal(interchangeStudentParent, ps);
    }

    public void printInterchangeStudentAssessment(PrintStream ps) throws JAXBException {
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

    public void printInterchangeEducationOrgCalendar(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStudentEnrollment(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentEnrollment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentEnrollment interchangeStudentEnrollment = new InterchangeStudentEnrollment();

        List<Object> list = interchangeStudentEnrollment
                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        // StudentSchoolAssociation
        StudentSchoolAssociationGenerator ssag = new StudentSchoolAssociationGenerator();
        for (StudentSchoolAssociationInternal ssai : studentSchoolAssociations) {
            list.add(ssag.generate(ssai.student, ssai.school));
        }

        // StudentSectionAssociation
        // GraduationPlan

        marshaller.marshal(interchangeStudentEnrollment, ps);
    }

    public void printInterchangeStudentGrade(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStudentProgram(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStudentCohort(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStudentDiscipline(PrintStream ps) throws JAXBException {
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

    public void printInterchangeStudentAttendance(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentAttendance.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentAttendance InterchangeStudentAttendance = new InterchangeStudentAttendance();

        List<AttendanceEvent> list = InterchangeStudentAttendance.getAttendanceEvent();

        // AttendanceEvent

        marshaller.marshal(InterchangeStudentAttendance, ps);
    }

}
