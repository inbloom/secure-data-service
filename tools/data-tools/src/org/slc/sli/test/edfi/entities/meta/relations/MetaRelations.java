package org.slc.sli.test.edfi.entities.meta.relations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.slc.sli.test.DataFidelityType;
import org.slc.sli.test.edfi.entities.meta.CalendarMeta;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;
import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.DisciplineActionMeta;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;
import org.slc.sli.test.edfi.entities.meta.GradingPeriodMeta;
import org.slc.sli.test.edfi.entities.meta.LeaMeta;
import org.slc.sli.test.edfi.entities.meta.ParentMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;
import org.slc.sli.test.edfi.entities.meta.StaffMeta;
import org.slc.sli.test.edfi.entities.meta.StudentAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentParentAssociationMeta;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;

public final class MetaRelations {
    
    // default fidelity of data generation
    public static final DataFidelityType DEFAULT_DATA_FIDELITY_TYPE = DataFidelityType.LOW_FI;
    
    // knobs to control number of entities to create
    // public static final int TOTAL_SEAS =2 ;
    // public static final int LEAS_PER_SEA =10;
    // public static final int STAFF_PER_SEA = 3;
    // public static final int SCHOOLS_PER_LEA = 25;
    // public static final int COURSES_PER_SCHOOL = 6;
    // public static final int SESSIONS_PER_SCHOOL = 2;
    // public static final int SECTIONS_PER_COURSE_SESSION = 2;
    // public static final int TEACHERS_PER_SCHOOL = 20;
    // public static final int STUDENTS_PER_SCHOOL = 200;
    // public static final int PROGRAMS_PER_SCHOOL = 2;
    // public static final int PROGRAMS_PER_SEA = 1;
    // public static final int STAFF_PER_PROGRAM = 2;
    // public static final int FREE_STANDING_COHORT_PER_SCHOOL = 4;
    // public static final int FREE_STANDING_COHORT_SIZE = 4;
    // public static final int STAFF_PER_FREE_STANDING_COHORT = 2;
    // public static final int INV_PROB_SECTION_HAS_PROGRAM = 2;
    // public static final int ASSESSMENTS_PER_STUDENT = 2;
    // public static final int ATTENDANCE_PER_STUDENT_SECTION = 2;
    // public static final int DISCPLINE_ACTIONS_PER_SCHOOL = 2;
    // public static final int DISCPLINE_INCIDENTS_PER_SCHOOL = 5;
    // public static final int INV_PROB_STUDENT_IN_DISCPLINE_INCIDENT = 2;
    // public static final int NUM_STAFF_PER_DISCIPLINE_ACTION = 1;
    // public static final int COURSES_PER_STUDENT = 2;
    // public static final int SECTIONS_PER_STUDENT = 2;
    // public static final int CALENDER_PER_SESSIONS = 2;
    // public static final int GRADINGPERIOD_PER_CALENDAR = 2;
    
    public static final int TOTAL_SEAS = 2;
    public static final int LEAS_PER_SEA = 2;
    public static final int STAFF_PER_SEA = 3;
    public static final int SCHOOLS_PER_LEA = 5;
    public static final int COURSES_PER_SCHOOL = 6;
    public static final int SESSIONS_PER_SCHOOL = 2;
    public static final int SECTIONS_PER_COURSE_SESSION = 2;
    public static final int TEACHERS_PER_SCHOOL = 20;
    public static final int STUDENTS_PER_SCHOOL = 20;
    public static final int PROGRAMS_PER_SCHOOL = 2;
    public static final int PROGRAMS_PER_SEA = 1;
    public static final int STAFF_PER_PROGRAM = 2;
    public static final int FREE_STANDING_COHORT_PER_SCHOOL = 4;
    public static final int FREE_STANDING_COHORT_SIZE = 4;
    public static final int STAFF_PER_FREE_STANDING_COHORT = 2;
    public static final int INV_PROB_SECTION_HAS_PROGRAM = 2;
    public static final int ASSESSMENTS_PER_STUDENT = 2;
    public static final int ATTENDANCE_PER_STUDENT_SECTION = 2;
    public static final int DISCPLINE_ACTIONS_PER_SCHOOL = 2;
    public static final int DISCPLINE_INCIDENTS_PER_SCHOOL = 5;
    public static final int INV_PROB_STUDENT_IN_DISCPLINE_INCIDENT = 2;
    public static final int NUM_STAFF_PER_DISCIPLINE_ACTION = 1;
    public static final int COURSES_PER_STUDENT = 2;
    public static final int SECTIONS_PER_STUDENT = 2;
    public static final int CALENDER_PER_SESSIONS = 2;
    public static final int GRADINGPERIOD_PER_CALENDAR = 2;
    
    // publicly accessible structures for the "meta-skeleton" entities populated by "buildFromSea()"
    // TODO: do we need maps? maybe just use Collections?
    public static final Map<String, CalendarMeta> CALENDAR_MAP = new TreeMap<String, CalendarMeta>();
    public static final Map<String, GradingPeriodMeta> GRADINGPERIOD_MAP = new TreeMap<String, GradingPeriodMeta>();
    public static final Map<String, SeaMeta> SEA_MAP = new TreeMap<String, SeaMeta>();
    public static final Map<String, LeaMeta> LEA_MAP = new TreeMap<String, LeaMeta>();
    public static final Map<String, SchoolMeta> SCHOOL_MAP = new TreeMap<String, SchoolMeta>();
    public static final Map<String, CourseMeta> COURSE_MAP = new TreeMap<String, CourseMeta>();
    public static final Map<String, SessionMeta> SESSION_MAP = new TreeMap<String, SessionMeta>();
    public static final Map<String, CourseOfferingMeta> COURSEOFFERING_MAP = new TreeMap<String, CourseOfferingMeta>();
    public static final Map<String, SectionMeta> SECTION_MAP = new TreeMap<String, SectionMeta>();
    public static final Map<String, StaffMeta> STAFF_MAP = new TreeMap<String, StaffMeta>();
    public static final Map<String, TeacherMeta> TEACHER_MAP = new TreeMap<String, TeacherMeta>();
    public static final Map<String, StudentMeta> STUDENT_MAP = new TreeMap<String, StudentMeta>();
    public static final Map<String, ParentMeta> PARENT_MAP = new TreeMap<String, ParentMeta>();
    public static final Map<String, StudentParentAssociationMeta> STUDENT_PARENT_MAP = new TreeMap<String, StudentParentAssociationMeta>();
    public static final Map<String, ProgramMeta> PROGRAM_MAP = new TreeMap<String, ProgramMeta>();
    public static final Map<String, CohortMeta> COHORT_MAP = new TreeMap<String, CohortMeta>();
    public static final Map<String, StudentAssessmentMeta> STUDENT_ASSES_MAP = new TreeMap<String, StudentAssessmentMeta>();
    public static final Map<String, DisciplineIncidentMeta> DISCIPLINE_INCIDENT_MAP = new TreeMap<String, DisciplineIncidentMeta>();
    public static final Map<String, DisciplineActionMeta> DISCIPLINE_ACTION_MAP = new TreeMap<String, DisciplineActionMeta>();
    
    public static final String SEA_PREFIX = "NY";
    public static final String FIRST_TEACHER_ID = "lroslin";
    
    /**
     * Construct the meta relationships necessary for XML interchanges
     */
    public static void construct() {
        
        long startTime = System.currentTimeMillis();
        
        buildSeas();
        
        AssessmentMetaRelations.buildStandaloneAssessments();
        
        assignAssessmentsToStudents();
        
        System.out.println("Time taken to build entity relationships: " + (System.currentTimeMillis() - startTime));
        
    }
    
    /**
     * Looping over all SEAs, build LEAs for each SEA
     */
    private static void buildSeas() {
        
        for (int idNum = 0; idNum < TOTAL_SEAS; idNum++) {
            
            SeaMeta seaMeta = new SeaMeta(SEA_PREFIX + idNum);
            // SeaMeta seaMeta = new SeaMeta(SEA_PREFIX);
            
            SEA_MAP.put(seaMeta.id, seaMeta);
            
            buildAndRelateEntitiesWithSea(seaMeta);
        }
    }
    
    private static void buildAndRelateEntitiesWithSea(SeaMeta seaMeta) {
        
        Map<String, StaffMeta> staffForSea = buildStaffForSea(seaMeta);
        
        Map<String, ProgramMeta> programmForSea = buildProgramsForSEA(seaMeta);
        
        buildLeasForSea(seaMeta, staffForSea);
    }
    
    /**
     * Create staff relations for each sea
     */
    private static Map<String, StaffMeta> buildStaffForSea(SeaMeta seaMeta) {
        
        Map<String, StaffMeta> staffInSeaMap = new HashMap<String, StaffMeta>(STAFF_PER_SEA);
        for (int idNum = 0; idNum < STAFF_PER_SEA; idNum++) {
            
            StaffMeta staffMeta = StaffMeta.createWithChainedId("staff" + idNum, seaMeta);
            
            STAFF_MAP.put(staffMeta.id, staffMeta);
            staffInSeaMap.put(staffMeta.id, staffMeta);
        }
        return staffInSeaMap;
    }
    
    /**
     * Looping over all LEAs, build Schools for each LEA
     * 
     * @param seaMeta
     */
    private static void buildLeasForSea(SeaMeta seaMeta, Map<String, StaffMeta> staffForSea) {
        
        for (int idNum = 0; idNum < LEAS_PER_SEA; idNum++) {
            
            LeaMeta leaMeta = new LeaMeta("D" + idNum, seaMeta);
            
            LEA_MAP.put(leaMeta.id, leaMeta);
            
            buildSchoolsForLea(leaMeta, staffForSea);
        }
    }
    
    /**
     * For each School, generate:
     * - teachers
     * - courses
     * - sessions
     * - sections
     * And correlate sections with teachers.
     * 
     * @param leaMeta
     */
    private static void buildSchoolsForLea(LeaMeta leaMeta, Map<String, StaffMeta> staffForSea) {
        
        for (int idNum = 0; idNum < SCHOOLS_PER_LEA; idNum++) {
            
            SchoolMeta schoolMeta = new SchoolMeta("HSch" + idNum, leaMeta);
            
            SCHOOL_MAP.put(schoolMeta.id, schoolMeta);
            
            buildAndRelateEntitiesWithSchool(schoolMeta, staffForSea);
        }
    }
    
    private static void buildAndRelateEntitiesWithSchool(SchoolMeta schoolMeta, Map<String, StaffMeta> staffForSea) {
        
        Map<String, TeacherMeta> teachersForSchool = buildTeachersForSchool(schoolMeta);
        
        Map<String, StudentMeta> studentsForSchool = buildStudentsForSchool(schoolMeta);
        
        Map<String, CourseMeta> coursesForSchool = buildCoursesForSchool(schoolMeta);
        
        Map<String, SessionMeta> sessionsForSchool = buildSessionsForSchool(schoolMeta);
        
        Map<String, CalendarMeta> calendarForSession = buildCalendarForSessions(sessionsForSchool);
        
        Map<String, GradingPeriodMeta> gradingPeriodForCalendar = buildGradingPeriodForCalendar(calendarForSession);
        
        Map<String, ProgramMeta> programForSchool = buildProgramsForSchool(schoolMeta);
        
        Map<String, CourseOfferingMeta> courseOfferingForSchool = buildCourseOfferingsForSchool(schoolMeta,
                sessionsForSchool, coursesForSchool);
        
        Map<String, SectionMeta> sectionsForSchool = buildSectionsForSchool(schoolMeta, coursesForSchool,
                sessionsForSchool, programForSchool);
        
        Map<String, CohortMeta> freeStandingCohortsForSchool = buildFreeStandingCohortsForSchool(schoolMeta);
        
        Map<String, DisciplineIncidentMeta> disciplineIncidentsForSchool = buildDisciplineIncidentsForSchool(schoolMeta);
        
        Map<String, DisciplineActionMeta> disciplineActionsForSchool = buildDisciplineActionsForSchool(schoolMeta);
        
        addSectionsToTeachers(sectionsForSchool, teachersForSchool);
        
        addStudentsToSections(sectionsForSchool, studentsForSchool);
        
        addStudentsToPrograms(sectionsForSchool, studentsForSchool, programForSchool);
        
        // programs are added for schools and sea, here we need add staffs for all programs
        addStaffToPrograms(MetaRelations.PROGRAM_MAP, staffForSea);
        
        addStaffStudentToFreeStandingCohorts(freeStandingCohortsForSchool, studentsForSchool, staffForSea);
        
        addDisciplineIncidentsToDisciplineActions(disciplineIncidentsForSchool, disciplineActionsForSchool);
        
        addStaffStudentToDisciplines(disciplineIncidentsForSchool, disciplineActionsForSchool, studentsForSchool,
                staffForSea);
    }
    
    // hacky way to generate 1 special teacher
    private static boolean createdSpecialTeacher = false;
    
    /**
     * Generate the teachers for this school.
     * teachersInSchoolMap is used later in this class.
     * TEACHER_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, TeacherMeta> buildTeachersForSchool(SchoolMeta schoolMeta) {
        
        Map<String, TeacherMeta> teachersInSchoolMap = new HashMap<String, TeacherMeta>(TEACHERS_PER_SCHOOL);
        for (int idNum = 0; idNum < TEACHERS_PER_SCHOOL; idNum++) {
            
            TeacherMeta teacherMeta;
            if (!createdSpecialTeacher) {
                // hardcode first teacher so we can log in as established user
                teacherMeta = TeacherMeta.create(FIRST_TEACHER_ID, schoolMeta);
                createdSpecialTeacher = true;
            } else {
                teacherMeta = TeacherMeta.createWithChainedId("teacher" + idNum, schoolMeta);
            }
            
            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            teachersInSchoolMap.put(teacherMeta.id, teacherMeta);
            TEACHER_MAP.put(teacherMeta.id, teacherMeta);
        }
        
        return teachersInSchoolMap;
    }
    
    /**
     * Generate the students for this school.
     * studentsInSchoolMap is used later in this class.
     * STUDENT_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, StudentMeta> buildStudentsForSchool(SchoolMeta schoolMeta) {
        int counter = 0;
        int parentId = 0;
        Random random = new Random();
        Map<String, StudentMeta> studentsInSchoolMap = new HashMap<String, StudentMeta>(STUDENTS_PER_SCHOOL);
        Map<String, ParentMeta> parentStudentsMap = new HashMap<String, ParentMeta>();
        Map<String, StudentParentAssociationMeta> stuParAssoMap = new HashMap<String, StudentParentAssociationMeta>();
        
        for (int idNum = 0; idNum < STUDENTS_PER_SCHOOL; idNum++) {
            
            StudentMeta studentMeta = new StudentMeta("stu" + idNum, schoolMeta);
            
            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            studentsInSchoolMap.put(studentMeta.id, studentMeta);
            STUDENT_MAP.put(studentMeta.id, studentMeta);
            
            boolean hasBoth = random.nextBoolean();
            
            if (hasBoth == false) {
                ParentMeta fatherMeta = new ParentMeta(studentMeta.id + "-dad" + counter, true);
                PARENT_MAP.put(fatherMeta.id, fatherMeta);
                StudentParentAssociationMeta studentFatherMeta = new StudentParentAssociationMeta("sPA" + counter,
                        studentMeta, fatherMeta);
                STUDENT_PARENT_MAP.put(studentMeta.id + schoolMeta.id + counter + "1", studentFatherMeta);
            }
            
            else {
                ParentMeta fatherMeta = new ParentMeta(studentMeta.id + "-dad" + counter, true);
                PARENT_MAP.put(fatherMeta.id, fatherMeta);
                ParentMeta motherMeta = new ParentMeta(studentMeta.id + "-mom" + counter, false);
                PARENT_MAP.put(motherMeta.id, motherMeta);
                StudentParentAssociationMeta studentMotherMeta = new StudentParentAssociationMeta("sPA" + counter,
                        studentMeta, motherMeta);
                STUDENT_PARENT_MAP.put(studentMeta.id + schoolMeta.id + counter + "1", studentMotherMeta);
                StudentParentAssociationMeta studentFatherMeta = new StudentParentAssociationMeta("sPA" + counter,
                        studentMeta, fatherMeta);
                STUDENT_PARENT_MAP.put(studentMeta.id + schoolMeta.id + counter + "2", studentFatherMeta);
            }
            
            counter++;
        }
        
        return studentsInSchoolMap;
    }
    
    /**
     * Generate the courses for the school.
     * coursesForSchool is used later in this class.
     * COURSE_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, CourseMeta> buildCoursesForSchool(SchoolMeta schoolMeta) {
        
        Map<String, CourseMeta> coursesForSchool = new HashMap<String, CourseMeta>(COURSES_PER_SCHOOL);
        
        for (int idNum = 0; idNum < COURSES_PER_SCHOOL; idNum++) {
            
            CourseMeta courseMeta = new CourseMeta("cse" + idNum, schoolMeta);
            
            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            coursesForSchool.put(courseMeta.id, courseMeta);
            COURSE_MAP.put(courseMeta.id, courseMeta);
        }
        
        return coursesForSchool;
    }
    
    /**
     * Generate the Calendar For GradingPeriod.
     * CalendarForGradingPeriod is used later in this class.
     * GRADINGPERIOD_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, GradingPeriodMeta> buildGradingPeriodForCalendar(
            Map<String, CalendarMeta> calendarForGradingPeriod) {
        
        Map<String, GradingPeriodMeta> gradingPeriodMetas = new HashMap<String, GradingPeriodMeta>(
                GRADINGPERIOD_PER_CALENDAR);
        
        for (CalendarMeta calendarMeta : calendarForGradingPeriod.values()) {
            
            for (int idNum = 0; idNum < GRADINGPERIOD_PER_CALENDAR; idNum++) {
                
                String gradingPeriodId = calendarMeta.id + "-" + idNum;
                
                GradingPeriodMeta gradingPeriodMeta = new GradingPeriodMeta(gradingPeriodId);
                gradingPeriodMeta.calendars.add(calendarMeta.id);
                
                // it's useful to return the objects created JUST for this school
                gradingPeriodMetas.put(gradingPeriodMeta.id, gradingPeriodMeta);
                GRADINGPERIOD_MAP.put(gradingPeriodMeta.id, gradingPeriodMeta);
            }
        }
        
        return gradingPeriodMetas;
    }
    
    /**
     * Generate the Calendar For Sessions.
     * CalendarForSessions is used later in this class.
     * CALENDAR_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, CalendarMeta> buildCalendarForSessions(Map<String, SessionMeta> sessionForSchool) {
        Map<String, CalendarMeta> calendarForSessions = new HashMap<String, CalendarMeta>(CALENDER_PER_SESSIONS);
        
        for (SessionMeta sessionMeta : sessionForSchool.values()) {
            for (int idNum = 0; idNum < SESSIONS_PER_SCHOOL; idNum++) {
                
                String calendarId = sessionMeta.id + "-" + idNum;
                CalendarMeta calendarMeta = new CalendarMeta(calendarId, sessionMeta);
                
                // it's useful to return the objects created JUST for this school
                calendarForSessions.put(calendarMeta.id, calendarMeta);
                CALENDAR_MAP.put(calendarMeta.id, calendarMeta);
                sessionMeta.calendarList.add(calendarId);
            }
        }
        return calendarForSessions;
        
    }
    
    private static Map<String, SessionMeta> buildSessionsForSchool(SchoolMeta schoolMeta) {
        
        Map<String, SessionMeta> sessionsForSchool = new HashMap<String, SessionMeta>(SESSIONS_PER_SCHOOL);
        
        for (int idNum = 0; idNum < SESSIONS_PER_SCHOOL; idNum++) {
            
            SessionMeta sessionMeta = new SessionMeta("ses" + idNum, schoolMeta);
            
            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            sessionsForSchool.put(sessionMeta.id, sessionMeta);
            SESSION_MAP.put(sessionMeta.id, sessionMeta);
        }
        
        return sessionsForSchool;
    }
    
    private static Map<String, CourseOfferingMeta> buildCourseOfferingsForSchool(SchoolMeta schoolMeta,
            Map<String, SessionMeta> sessionsForSchool, Map<String, CourseMeta> courseForSchool) {
        Map<String, CourseOfferingMeta> courseOfferingForSchool = new HashMap<String, CourseOfferingMeta>();
        
        for (SessionMeta sessionMeta : sessionsForSchool.values()) {
            for (CourseMeta courseMeta : courseForSchool.values()) {
                String id = "l" + sessionMeta.id.substring(sessionMeta.id.lastIndexOf('-'))
                        + courseMeta.id.substring(courseMeta.id.lastIndexOf('-'));
                CourseOfferingMeta courseOfferingMeta = new CourseOfferingMeta(id, schoolMeta, sessionMeta, courseMeta);
                courseOfferingForSchool.put(courseOfferingMeta.id, courseOfferingMeta);
                COURSEOFFERING_MAP.put(courseOfferingMeta.id, courseOfferingMeta);
            }
        }
        
        return courseOfferingForSchool;
    }
    
    /**
     * Generate the sections for this school.
     * sectionMapForSchool is used later in this class.
     * SECTION_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @param coursesForSchool
     * @param sessionsForSchool
     * @param programsForSchool
     * @return
     */
    private static Map<String, SectionMeta> buildSectionsForSchool(SchoolMeta schoolMeta,
            Map<String, CourseMeta> coursesForSchool, Map<String, SessionMeta> sessionsForSchool,
            Map<String, ProgramMeta> programsForSchool) {
        
        Map<String, SectionMeta> sectionMapForSchool = new HashMap<String, SectionMeta>();
        
        Object[] programMetas = programsForSchool.values().toArray();
        int programCounter = 0;
        
        for (SessionMeta sessionMeta : sessionsForSchool.values()) {
            
            for (CourseMeta courseMeta : coursesForSchool.values()) {
                
                for (int idNum = 0; idNum < SECTIONS_PER_COURSE_SESSION; idNum++) {
                    
                    // program reference in section is optional; will create one program reference
                    // for every inverse-probability-section-has-program section
                    ProgramMeta programMeta = null;
                    if (programMetas.length > 0 && sectionMapForSchool.size() % INV_PROB_SECTION_HAS_PROGRAM == 0) {
                        programMeta = (ProgramMeta) programMetas[programCounter];
                        programCounter = (programCounter + 1) % programMetas.length;
                    }
                    
                    SectionMeta sectionMeta = new SectionMeta("sec" + idNum, schoolMeta, courseMeta, sessionMeta,
                            programMeta);
                    
                    // it's useful to return the objects created JUST for this school
                    // add to both maps here to avoid loop in map.putAll if we merged maps later
                    sectionMapForSchool.put(sectionMeta.id, sectionMeta);
                    SECTION_MAP.put(sectionMeta.id, sectionMeta);
                }
            }
        }
        
        return sectionMapForSchool;
    }
    
    /**
     * Generate the programs for this school.
     * programMapForSchool is used later in this class.
     * PROGRAM_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, ProgramMeta> buildProgramsForSchool(SchoolMeta schoolMeta) {
        
        Map<String, ProgramMeta> programMapForSchool = new HashMap<String, ProgramMeta>();
        
        for (int idNum = 0; idNum < PROGRAMS_PER_SCHOOL; idNum++) {
            
            ProgramMeta programMeta = new ProgramMeta("prg" + idNum, schoolMeta);
            
            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            programMapForSchool.put(programMeta.id, programMeta);
            PROGRAM_MAP.put(programMeta.id, programMeta);
            
            schoolMeta.programId = programMeta.id;
            
            buildCohortsForProgram(programMeta, schoolMeta);
        }
        
        return programMapForSchool;
    }
    
    /**
     * Generate the programs for SEA.
     * programMapForSEA is used later in this class.
     * PROGRAM_MAP is used to actually generate the XML.
     * 
     * @param
     * @return
     */
    private static Map<String, ProgramMeta> buildProgramsForSEA(SeaMeta seaMeta) {
        
        Map<String, ProgramMeta> programMapForSEA = new HashMap<String, ProgramMeta>();
        
        for (int idNum = 0; idNum < PROGRAMS_PER_SEA; idNum++) {
            ProgramMeta programMeta = new ProgramMeta("prg" + seaMeta.id + "_" + idNum, seaMeta);
            programMapForSEA.put(programMeta.id, programMeta);
            PROGRAM_MAP.put(programMeta.id, programMeta);
            seaMeta.programId = programMeta.id;
            seaMeta.programs.put(seaMeta.programId, programMeta);
        }
        return programMapForSEA;
    }
    
    /**
     * Generate a cohort for this program.
     * COHORT_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     * @param programMeta
     * @return
     */
    private static void buildCohortsForProgram(ProgramMeta programMeta, SchoolMeta schoolMeta) {
        CohortMeta cohortMeta = new CohortMeta("ch", programMeta);
        COHORT_MAP.put(cohortMeta.id, cohortMeta);
        programMeta.cohortIds.add(cohortMeta.id);
        return;
    }
    
    /**
     * Generate free-standing (non-program-affiliated cohorts for school.
     * 
     * freeStandingCohortsForSchool is used later in this class
     * COHORT_MAP is used to actually generate the XML.
     * 
     * @param schoolMeta
     */
    private static Map<String, CohortMeta> buildFreeStandingCohortsForSchool(SchoolMeta schoolMeta) {
        
        Map<String, CohortMeta> freeStandingCohortsForSchool = new HashMap<String, CohortMeta>(
                FREE_STANDING_COHORT_PER_SCHOOL);
        for (int idNum = 0; idNum < FREE_STANDING_COHORT_PER_SCHOOL; idNum++) {
            CohortMeta cohortMeta = new CohortMeta("coh" + idNum, schoolMeta);
            freeStandingCohortsForSchool.put(cohortMeta.id, cohortMeta);
            COHORT_MAP.put(cohortMeta.id, cohortMeta);
        }
        return freeStandingCohortsForSchool;
        
    }
    
    /**
     * Generates discipline incidents for a school.
     * 
     * @param schoolMeta
     */
    private static Map<String, DisciplineIncidentMeta> buildDisciplineIncidentsForSchool(SchoolMeta schoolMeta) {
        Map<String, DisciplineIncidentMeta> disciplineIncidentsForSchool = new HashMap<String, DisciplineIncidentMeta>(
                DISCPLINE_INCIDENTS_PER_SCHOOL);
        for (int idNum = 0; idNum < DISCPLINE_INCIDENTS_PER_SCHOOL; idNum++) {
            DisciplineIncidentMeta disciplineIncidentMeta = new DisciplineIncidentMeta("i" + idNum, schoolMeta);
            disciplineIncidentsForSchool.put(disciplineIncidentMeta.id, disciplineIncidentMeta);
            DISCIPLINE_INCIDENT_MAP.put(disciplineIncidentMeta.id, disciplineIncidentMeta);
        }
        return disciplineIncidentsForSchool;
    }
    
    /**
     * Generates discipline actions for a school
     * 
     * @param schoolMeta
     * @return
     */
    private static Map<String, DisciplineActionMeta> buildDisciplineActionsForSchool(SchoolMeta schoolMeta) {
        Map<String, DisciplineActionMeta> disciplineActionForSchool = new HashMap<String, DisciplineActionMeta>();
        for (int idNum = 0; idNum < DISCPLINE_INCIDENTS_PER_SCHOOL; idNum++) {
            DisciplineActionMeta disciplineActionMeta = new DisciplineActionMeta("a" + idNum, schoolMeta);
            disciplineActionForSchool.put(disciplineActionMeta.id, disciplineActionMeta);
            DISCIPLINE_ACTION_MAP.put(disciplineActionMeta.id, disciplineActionMeta);
        }
        return disciplineActionForSchool;
    }
    
    /**
     * Correlates teachers and sections on a 'per school' basis.
     * 
     * @param sectionsForSchool
     * @param teachersForSchool
     */
    private static void addSectionsToTeachers(Map<String, SectionMeta> sectionsForSchool,
            Map<String, TeacherMeta> teachersForSchool) {
        
        Object[] teacherMetas = teachersForSchool.values().toArray();
        int teacherCounter = 0;
        
        // each section needs to be referenced by a TeacherMeta
        for (SectionMeta sectionMeta : sectionsForSchool.values()) {
            
            // loop through the teachers we have in this school and assign them to sections
            if (teacherCounter >= teacherMetas.length) {
                teacherCounter = 0;
            }
            ((TeacherMeta) teacherMetas[teacherCounter]).sectionIds.add(sectionMeta.id);
            teacherCounter++;
        }
    }
    
    /**
     * Correlates students and sections on a 'per school' basis.
     * 
     * @param sectionsForSchool
     * @param studentsForSchool
     */
    private static void addStudentsToSections(Map<String, SectionMeta> sectionsForSchool,
            Map<String, StudentMeta> studentsForSchool) {
        
        Object[] sectionMetas = sectionsForSchool.values().toArray();
        int sectionCounter = 0;
        
        for (StudentMeta studentMeta : studentsForSchool.values()) {
            
            // TODO students should not belong to simultaneous sections
            for (int i = 0; i < SECTIONS_PER_STUDENT; i++) {
                if (sectionCounter >= sectionMetas.length) {
                    sectionCounter = 0;
                }
                studentMeta.sectionIds.add(((SectionMeta) sectionMetas[sectionCounter]).id);
                sectionCounter++;
            }
        }
    }
    
    /**
     * Correlates students and program on a 'per school' basis.
     * Student S is correlated with a program P iff there exists a section X s.t. S is
     * correlated with X and X is correlated with P.
     * 
     * @param sectionsForSchool
     * @param studentsForSchool
     */
    private static void addStudentsToPrograms(Map<String, SectionMeta> sectionsForSchool,
            Map<String, StudentMeta> studentsForSchool, Map<String, ProgramMeta> programsForSchool) {
        
        for (StudentMeta studentMeta : studentsForSchool.values()) {
            for (String sectionId : studentMeta.sectionIds) {
                SectionMeta sectionMeta = sectionsForSchool.get(sectionId);
                if (sectionMeta != null && sectionMeta.programId != null
                        && programsForSchool.containsKey(sectionMeta.programId)) {
                    ProgramMeta programMeta = programsForSchool.get(sectionMeta.programId);
                    programMeta.studentIds.add(studentMeta.id);
                }
            }
        }
        
        // for each cohort in the program, add all the students in it to the cohort too.
        for (ProgramMeta programMeta : programsForSchool.values()) {
            for (String cohortId : programMeta.cohortIds) {
                CohortMeta cohortMeta = COHORT_MAP.get(cohortId);
                if (cohortMeta != null) {
                    cohortMeta.studentIds.addAll(programMeta.studentIds);
                }
            }
        }
    }
    
    /**
     * Correlates staff and program on a 'per school' basis.
     * 
     * @param programsForSchool
     */
    private static void addStaffToPrograms(Map<String, ProgramMeta> programsForSchool,
            Map<String, StaffMeta> staffForSea) {
        Object[] staffMetas = staffForSea.values().toArray();
        int staffCounter = 0;
        
        // each program needs to be referenced by a StaffMeta
        for (ProgramMeta programMeta : programsForSchool.values()) {
            for (int staffToAssign = 0; staffToAssign < STAFF_PER_PROGRAM; staffToAssign++) {
                // loop through the sections we have in this school and assign students to them
                if (staffCounter >= staffMetas.length) {
                    staffCounter = 0;
                }
                String staffId = ((StaffMeta) staffMetas[staffCounter]).id;
                programMeta.staffIds.add(staffId);
                staffCounter++;
            }
        }
        
        // for each cohort in the program, add all the staff in it to the cohort too.
        for (ProgramMeta programMeta : programsForSchool.values()) {
            for (String cohortId : programMeta.cohortIds) {
                CohortMeta cohortMeta = COHORT_MAP.get(cohortId);
                if (cohortMeta != null) {
                    cohortMeta.staffIds.addAll(programMeta.staffIds);
                }
            }
        }
    }
    
    /**
     * Assign staff and student to the school's free-standing cohorts
     * 
     * @param freeStandingCohortsForSchool
     * @param studentsForSchool
     * @param staffForSea
     */
    private static void addStaffStudentToFreeStandingCohorts(Map<String, CohortMeta> freeStandingCohortsForSchool,
            Map<String, StudentMeta> studentsForSchool, Map<String, StaffMeta> staffForSea) {
        Object[] studentIds = studentsForSchool.keySet().toArray();
        Object[] staffIds = staffForSea.keySet().toArray();
        int studentIdsIndx = 0;
        int staffIdsIndx = 0;
        
        for (CohortMeta cohortMeta : freeStandingCohortsForSchool.values()) {
            for (int idNum = 0; idNum < FREE_STANDING_COHORT_SIZE; idNum++) {
                cohortMeta.studentIds.add((String) studentIds[studentIdsIndx]);
                studentIdsIndx = (studentIdsIndx + 1) % studentIds.length;
            }
            for (int staffToAssign = 0; staffToAssign < STAFF_PER_FREE_STANDING_COHORT; staffToAssign++) {
                cohortMeta.staffIds.add((String) staffIds[staffIdsIndx]);
                staffIdsIndx = (staffIdsIndx + 1) % staffIds.length;
            }
        }
    }
    
    /**
     * Assign discipline incidents to discipline actions
     * 
     * @param disciplineIncidentsForSchool
     * @param disciplineActionsForSchool
     */
    private static void addDisciplineIncidentsToDisciplineActions(
            Map<String, DisciplineIncidentMeta> disciplineIncidentsForSchool,
            Map<String, DisciplineActionMeta> disciplineActionsForSchool) {
        Object[] disciplineActionMetas = disciplineActionsForSchool.values().toArray();
        int disciplineActionsIndx = 0;
        
        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentsForSchool.values()) {
            DisciplineActionMeta disciplineActionMeta = (DisciplineActionMeta) disciplineActionMetas[disciplineActionsIndx];
            disciplineActionMeta.incidentIds.add(disciplineIncidentMeta.id);
            disciplineActionsIndx = (disciplineActionsIndx + 1) % disciplineActionMetas.length;
        }
    }
    
    /**
     * Assign staff and student to discipline entities
     * 
     * @param disciplineIncidentsForSchool
     * @param disciplineActionsForSchool
     * @param studentsForSchool
     * @param staffForSea
     */
    private static void addStaffStudentToDisciplines(Map<String, DisciplineIncidentMeta> disciplineIncidentsForSchool,
            Map<String, DisciplineActionMeta> disciplineActionsForSchool, Map<String, StudentMeta> studentsForSchool,
            Map<String, StaffMeta> staffForSea) {
        Object[] disciplineIncidentMetas = disciplineIncidentsForSchool.values().toArray();
        int disciplineIncidentIndx = 0;
        Object[] staffMetas = staffForSea.values().toArray();
        int staffIndx = 0;
        Object[] studentMetas = studentsForSchool.values().toArray();
        int studentIndx = 0;
        
        // assign a student to each discipline incident.
        for (DisciplineIncidentMeta disciplineIncidentMeta : disciplineIncidentsForSchool.values()) {
            StudentMeta studentMeta = (StudentMeta) studentMetas[studentIndx];
            disciplineIncidentMeta.studentIds.add(studentMeta.id);
            studentIndx = (studentIndx + 1) % staffMetas.length;
        }
        
        // assign all students in all discipline incidents involved in a discipline action in that
        // action.
        for (DisciplineActionMeta disciplineActionMeta : disciplineActionsForSchool.values()) {
            for (String disciplineIncidentId : disciplineActionMeta.incidentIds) {
                DisciplineIncidentMeta disciplineIncidentMeta = disciplineIncidentsForSchool.get(disciplineIncidentId);
                // should be non-null.
                disciplineActionMeta.studentIds.addAll(disciplineIncidentMeta.studentIds);
            }
            // assign NUM_STAFF_PER_DISCIPLINE_ACTION staff to the action.
            for (int i = 0; i < NUM_STAFF_PER_DISCIPLINE_ACTION; i++) {
                StaffMeta staffMeta = (StaffMeta) staffMetas[staffIndx];
                disciplineActionMeta.staffIds.add(staffMeta.id);
                staffIndx = (staffIndx + 1) % staffMetas.length;
            }
        }
        
    }
    
    private static void assignAssessmentsToStudents() {
        for (StudentMeta studentMeta : STUDENT_MAP.values()) {
            for (int count = 0; count < ASSESSMENTS_PER_STUDENT; count++) {
                
                StudentAssessmentMeta studentAssessmentMeta = StudentAssessmentMeta.create(studentMeta,
                        AssessmentMetaRelations.getRandomAssessmentMeta());
                STUDENT_ASSES_MAP.put(studentAssessmentMeta.xmlId, studentAssessmentMeta);
            }
        }
    }
    
}
