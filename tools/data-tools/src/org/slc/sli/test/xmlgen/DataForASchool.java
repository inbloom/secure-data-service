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


package org.slc.sli.test.xmlgen;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slc.sli.test.edfi.entities.*;
import org.slc.sli.test.generators.BehaviorDescriptorGenerator;
import org.slc.sli.test.generators.DisciplineActionGenerator;
import org.slc.sli.test.generators.DisciplineDescriptorGenerator;
import org.slc.sli.test.generators.DisciplineGenerator;
import org.slc.sli.test.generators.ParentGenerator;
import org.slc.sli.test.generators.SchoolGenerator;
import org.slc.sli.test.generators.SectionGenerator;
import org.slc.sli.test.generators.StudentDisciplineAssociationGenerator;
import org.slc.sli.test.generators.StudentGenerator;
import org.slc.sli.test.generators.StudentParentAssociationGenerator;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.TeacherGenerator;
import org.slc.sli.test.generators.TeacherSchoolAssociationGenerator;
import org.slc.sli.test.utils.ValidateSchema;
import org.slc.sli.test.xmlgen.internals.*;

public class DataForASchool {
    private String prefix = "a";
    private Random random = new Random(31);
    private static final int numStudents = 10;
    private static final int numBehaviors = 3;
    private static final int numDisciplines = 3;
    private static final int parentsPerStudent = 2;
    private static final String delimiter = "_";

    private List<String> schools = new ArrayList<String>();

    private List<SectionInternal> sections = new ArrayList<SectionInternal>();

//    private List<StaffEducationOrgEmploymentAssociationInternal> staffEducationOrgEmploymentAssociations = new ArrayList<StaffEducationOrgEmploymentAssociationInternal>();

    private List<String> teachers = new ArrayList<String>();
    private List<TeacherSchoolAssociationInternal> teacherSchoolAssociations = new ArrayList<TeacherSchoolAssociationInternal>();
    private List<TeacherSectionAssociationInternal> teacherSectionAssociations = new ArrayList<TeacherSectionAssociationInternal>();

    private List<String> students = new ArrayList<String>();
    private List<String> parents = new ArrayList<String>();
    private List<String> studentParentAssociations = new ArrayList<String>();

    private List<String> disciplineIncidents = new ArrayList<String>();
    private List<String> studentDisciplineIncidentAssociations = new ArrayList<String>();
    private List<String> disciplineActions = new ArrayList<String>();


    private List<StudentSchoolAssociationInternal> studentSchoolAssociations = new ArrayList<StudentSchoolAssociationInternal>();

    /**
     * @param args
     * @throws Exception
     * @throws JAXBException
     */
    public static void main(String[] args) throws Exception {
        String root = "data";
        System.out.println(new Date());

        for (int i = 0; i < 2; i++) {
            DataForASchool data = new DataForASchool(Integer.toString(i));
            String path = root + "/temp" + i;
            File folder = new File(path);

            if (!folder.exists())
                folder.mkdirs();

            data.generateData(path, false, true, i);
        }

        System.out.println(new Date());

    }

    public DataForASchool(String prefix) {
        this.prefix = "a" + prefix;
    }

    public void generateData(String path, boolean display, boolean validate, int iteration) throws Exception {
        prepareData(iteration);
        saveInterchanges(path);
        if (display)
            printOnScreen();
        if (validate)
            validateInterchanges(path);
    }

    public void saveInterchanges(String path) {
        try {
            printInterchangeEducationOrganization(new PrintStream(path + "/InterchangeEducationOrganization.xml"));
//            printInterchangeMasterSchedule(new PrintStream(path + "/InterchangeMasterSchedule.xml"));
//            printInterchangeAssessmentMetadata(new PrintStream(path + "/InterchangeAssessmentMetadata.xml"));
            printInterchangeStaffAssociation(new PrintStream(path + "/InterchangeStaffAssociation.xml"));
            printInterchangeStudent(new PrintStream(path + "/InterchangeStudent.xml"));
            printInterchangeStudentParent(new PrintStream(path + "/InterchangeStudentParent.xml"));
//            printInterchangeStudentAssessment(new PrintStream(path + "/InterchangeStudentAssessment.xml"));
//            printInterchangeEducationOrgCalendar(new PrintStream(path + "/InterchangeEducationOrgCalendar.xml"));
            printInterchangeStudentEnrollment(new PrintStream(path + "/InterchangeStudentEnrollment.xml"));
//            printInterchangeStudentGrade(new PrintStream(path + "/InterchangeStudentGrade.xml"));
//            printInterchangeStudentProgram(new PrintStream(path + "/InterchangeStudentProgram.xml"));
//            printInterchangeStudentCohort(new PrintStream(path + "/InterchangeStudentCohort.xml"));
            printInterchangeStudentDiscipline(new PrintStream(path + "/InterchangeStudentDiscipline.xml"));
//            printInterchangeStudentAttendance(new PrintStream(path + "/InterchangeStudentAttendance.xml"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("DONE");
    }

    public void printOnScreen() throws Exception {
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
            ValidateSchema.check(xmlDir);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void prepareData(int iteration) {
        prepareSchool(1);
        prepareTeacher(5, iteration);
        prepareTeacherSchoolAssociation();
        prepareSection(4);
        prepareTeacherSectionAssociation();
        prepareStudent(numStudents);
        prepareParent(numStudents*parentsPerStudent);
        prepareStudentParentAssociation(numStudents*parentsPerStudent);
        prepareStudentSchoolAssociation();
        prepareDisciplineIncident(3);
        prepareStudentDisciplineIncidentAssociation();
    }

    public void prepareSchool(int total) {
        for (int i = 0; i < total; i++) {
            schools.add("school-"+ this.prefix + "-" + i);
        }
    }

	public void prepareTeacher(int total, int iteration) {

		for (int i = 0; i < total; i++) {

			if (iteration == 0) {
				switch (i) {
				case 0:
					teachers.add("cgray");
					break;
				case 1:
					teachers.add("linda.kim");
					break;
				case 2:
					teachers.add("rbraverman");
					break;
				default:
					teachers.add("teacher_" + this.prefix + "-" + i);
					break;
				}
			} else {
				teachers.add("teacher_" + this.prefix + "-" + i);
			}
		}
	}

    public void prepareTeacherSchoolAssociation() {
        Random random = new Random(31);
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
        Random r = new Random(31);
        for(String teacher : teachers) {
            TeacherSectionAssociationInternal tsai = new TeacherSectionAssociationInternal();
            tsai.teacherId = teacher;
            tsai.section = sections.get(r.nextInt(sections.size()));
            teacherSectionAssociations.add(tsai);
        }
    }

    public void prepareStudent(int total) {
        for (int i = 0 ; i < total ; i++) {
            students.add("student-" + this.prefix + "-" + i);
        }
    }

    public void prepareParent(int total) {
        for (int i = 0 ; i < total ; i++) {
            parents.add("parent-" + this.prefix + "-" + i);
        }
    }

    public void prepareStudentParentAssociation(int total) {
        int iStudent = 0;
        int iParent = 0;
        while (iStudent<students.size()) {
            String studentId = students.get(iStudent);
            while (iParent<parents.size()) {
                String parentId = parents.get(iParent);
                studentParentAssociations.add(studentId+delimiter+parentId);
                iParent++;
                if (iParent%parentsPerStudent == 0) break;
            }
            iStudent++;
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

    public void prepareDisciplineIncident(int total) {
    	String schoolId = schools.get(0);
        for (int i = 0 ; i < total ; i++) {
            disciplineIncidents.add(schoolId+delimiter+this.prefix + "-discInc-" + i);
        }
    }

    public void prepareStudentDisciplineIncidentAssociation() {
        for (String discId : disciplineIncidents) {
        	String studentId = students.get(random.nextInt(students.size()));
            studentDisciplineIncidentAssociations.add(studentId+delimiter+discId);
            disciplineActions.add(studentId+delimiter+discId);
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
            SLCSchool school = sg.getSchool(schoolId);
            list.add(school);
        }

        marshaller.marshal(interchangeEducationOrganization, ps);
    }

    public void printInterchangeMasterSchedule(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeMasterSchedule.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeMasterSchedule interchangeMasterSchedule = new InterchangeMasterSchedule();
        List<Object> list = interchangeMasterSchedule.getCourseOfferingOrSectionOrBellSchedule();

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

    public void printInterchangeStaffAssociation(PrintStream ps) throws Exception {
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
        TeacherGenerator tg = new TeacherGenerator(StateAbbreviationType.NY, true);
        for (String teacherId : teachers) {
            list.add(tg.generate(teacherId));
        }

        // TeacherSchoolAssociation
        TeacherSchoolAssociationGenerator tsag = new TeacherSchoolAssociationGenerator();
        for (TeacherSchoolAssociationInternal tsai : teacherSchoolAssociations) {
            list.add(tsag.generate(tsai.teacherId, tsai.schoolIds));
        }

        // TeacherSectionAssociation
        // TODO uncomment when course is done
//        TeacherSectionAssociationGenerator tsecag = new TeacherSectionAssociationGenerator();
//        for (TeacherSectionAssociationInternal tsai : teacherSectionAssociations) {
//            list.add(tsecag.generate(tsai.teacherId, tsai.section.schoolId, tsai.section.sectionCode));
//        }

        // LeaveEvent
        // OpenStaffPosition
        // CredentialFieldDescriptor

        marshaller.marshal(interchangeStaffAssociation, ps);
    }

    public void printInterchangeStudent(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudent interchangeStudent = new InterchangeStudent();

        List<Student> list = interchangeStudent.getStudent();

        // student
        boolean includeOptionalData = false;
        boolean randomizeData = true;
        StudentGenerator sg = new StudentGenerator(StateAbbreviationType.NY, includeOptionalData, randomizeData);
        for (String studentId : students) {
            Student student = sg.generate(studentId);
            list.add(student);
        }

        marshaller.marshal(interchangeStudent, ps);
    }

    public void printInterchangeStudentParent(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentParent.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentParent interchangeStudentParent = new InterchangeStudentParent();

        List<Object> list = interchangeStudentParent.getStudentOrParentOrStudentParentAssociation();

        // TODO uncomment once ingestion is updated to include students here
//        // student
//        StudentGenerator sg = new StudentGenerator(StateAbbreviationType.NY);
//        for (String studentId : students) {
//            Student student = sg.generate(studentId);
//            list.add(student);
//        }

        // parent
        ParentGenerator pg = new ParentGenerator(StateAbbreviationType.NY);
        for (String parentId : parents) {
            Parent parent = pg.generate(parentId, random.nextBoolean());
            list.add(parent);
        }

        // studentParentAssociation
        StudentParentAssociationGenerator spag = new StudentParentAssociationGenerator();
        for (String studentParentId : studentParentAssociations) {
            SLCStudentParentAssociation spa = spag.generate(studentParentId, delimiter);
            list.add(spa);
        }

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
        //StudentSchoolAssociationGenerator ssag = new StudentSchoolAssociationGenerator();
        for (StudentSchoolAssociationInternal ssai : studentSchoolAssociations) {
            list.add(StudentSchoolAssociationGenerator.generateLowFi(ssai.student, ssai.school));
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

        List<ComplexObjectType> list = interchangeStudentCohort.getCohortOrStudentCohortAssociationOrStaffCohortAssociation();

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

        List<ComplexObjectType> list = interchangeStudentDiscipline
                .getDisciplineIncidentOrStudentDisciplineIncidentAssociationOrDisciplineAction();

        // DisciplineIncident
        DisciplineGenerator dg = new DisciplineGenerator();
        for (String disciplineId : disciplineIncidents) {
            SLCDisciplineIncident disciplineIncident = dg.generate(disciplineId, delimiter);
            list.add(disciplineIncident);
        }

        // StudentDisciplineIncidentAssociation
        StudentDisciplineAssociationGenerator sdag = new StudentDisciplineAssociationGenerator();
        for (String stringId : studentDisciplineIncidentAssociations) {
        	SLCStudentDisciplineIncidentAssociation discAssociate = sdag.generate(stringId, delimiter);
        	list.add(discAssociate);
        }

        // DisciplineAction - Assuming action in all cases (even StudentParticipationCodeType.VICTIM.  Sorry!)
        DisciplineActionGenerator sdactg = new DisciplineActionGenerator();
        for (String stringId : disciplineActions) {
        	SLCDisciplineAction discAction = sdactg.generate(stringId, delimiter);
        	list.add(discAction);
        }

        // BehaviorDescriptor
        BehaviorDescriptorGenerator bdg = new BehaviorDescriptorGenerator();
        for (int iBehavior=0; iBehavior<numBehaviors; iBehavior++) {
        	String schoolId = schools.get(random.nextInt(schools.size()));
        	BehaviorDescriptor bd = bdg.generate(iBehavior, schoolId);
        	list.add(bd);
        }

        // DisciplineDescriptor
        DisciplineDescriptorGenerator ddg = new DisciplineDescriptorGenerator();
        for (int iDiscipline=0; iDiscipline<numDisciplines; iDiscipline++) {
        	String schoolId = schools.get(random.nextInt(schools.size()));
        	DisciplineDescriptor dd = ddg.generate(iDiscipline, schoolId);
        	list.add(dd);
        }

        marshaller.marshal(interchangeStudentDiscipline, ps);
    }

    public void printInterchangeStudentAttendance(PrintStream ps) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeStudentAttendance.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

        InterchangeStudentAttendance InterchangeStudentAttendance = new InterchangeStudentAttendance();

        List<SLCAttendanceEvent> list = InterchangeStudentAttendance.getAttendanceEvent();

        // AttendanceEvent

        marshaller.marshal(InterchangeStudentAttendance, ps);
    }

}
