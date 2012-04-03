package org.slc.sli.test.mappingGenerator;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.test.edfi.entities.relations.CourseMeta;
import org.slc.sli.test.edfi.entities.relations.LeaMeta;
import org.slc.sli.test.edfi.entities.relations.SchoolMeta;
import org.slc.sli.test.edfi.entities.relations.SeaMeta;
import org.slc.sli.test.edfi.entities.relations.SectionMeta;
import org.slc.sli.test.edfi.entities.relations.SessionMeta;
import org.slc.sli.test.edfi.entities.relations.TeacherMeta;

public class MetaRelations {

    private static final int TOTAL_SEAS = 1;
    private static final int LEAS_PER_SEA = 1;
    private static final int SCHOOLS_PER_LEA = 1;
    private static final int COURSES_PER_SCHOOL = 2;
    private static final int SESSIONS_PER_SCHOOL = 1;
    private static final int SECTIONS_PER_COURSE_SESSION = 2;
    private static final int TEACHERS_PER_SCHOOL = 3;

    // InterchangeEducationOrganization
    public static Map<String, SeaMeta> seaMap = new HashMap<String, SeaMeta>();
    public static Map<String, LeaMeta> leaMap = new HashMap<String, LeaMeta>();
    public static Map<String, SchoolMeta> schoolMap = new HashMap<String, SchoolMeta>();
    public static Map<String, CourseMeta> courseMap = new HashMap<String, CourseMeta>();

    // InterchangeEducationOrganizationCalendar
    public static Map<String, SessionMeta> sessionMap = new HashMap<String, SessionMeta>();

    // InterchangeMasterSchedule
    public static Map<String, SectionMeta> sectionMap = new HashMap<String, SectionMeta>();

    // InterchangeStaffAssociation
    public static Map<String, TeacherMeta> teacherMap = new HashMap<String, TeacherMeta>();

    public static void main(String[] args) {

        buildSeas();

    }

    /**
     * The top level call to start the XML generation process is
     * to 'buildSeas'
     */
    public static void buildFromSea() {

        long startTime = System.currentTimeMillis();

        buildSeas();

        System.out.println("Time taken to build entity relations: " + (System.currentTimeMillis() - startTime));

    }

    /**
     * Looping over all SEAs, build LEAs for each SEA
     */
    private static void buildSeas() {

        for (int idNum = 0; idNum < TOTAL_SEAS; idNum++) {

            SeaMeta seaMeta = new SeaMeta("sea" + idNum);

            seaMap.put(seaMeta.id, seaMeta);

            buildLeasForSea(seaMeta);
        }
    }

    /**
     * Looping over all LEAs, build Schools for each LEA
     * @param seaMeta
     */
    private static void buildLeasForSea(SeaMeta seaMeta) {

        for (int idNum = 0; idNum < LEAS_PER_SEA; idNum++) {

            LeaMeta leaMeta = new LeaMeta("lea" + idNum, seaMeta);

            leaMap.put(leaMeta.id, leaMeta);

            buildSchoolsForLea(leaMeta);
        }
    }

    /**
     * For each School, generate:
     * 	 - teachers
     * 	 - courses
     * 	 - sessions
     * 	 - sections
     * And correlate sections with teachers.
     * @param leaMeta
     */
    private static void buildSchoolsForLea(LeaMeta leaMeta) {

        for (int idNum = 0; idNum < SCHOOLS_PER_LEA; idNum++) {

            SchoolMeta schoolMeta = new SchoolMeta("school" + idNum, leaMeta);

            schoolMap.put(schoolMeta.id, schoolMeta);

            Map<String, TeacherMeta> teachersForSchool = buildTeachersForSchool(schoolMeta);

            Map<String, CourseMeta> coursesForSchool = buildCoursesForSchool(schoolMeta);

            Map<String, SessionMeta> sessionsForSchool = buildSessionsForSchool(schoolMeta);

            Map<String, SectionMeta> sectionsForSchool = buildSectionsForSchool(schoolMeta, coursesForSchool,
                    sessionsForSchool);

            addTeachersToSections(sectionsForSchool, teachersForSchool);
        }
    }

    /**
     * Generate the courses for the school.
     * coursesForSchool is used later in this class.
     * courseMap is used to actually generate the XML.
     * @param schoolMeta
     * @return
     */
    private static Map<String, CourseMeta> buildCoursesForSchool(SchoolMeta schoolMeta) {

        Map<String, CourseMeta> coursesForSchool = new HashMap<String, CourseMeta>(COURSES_PER_SCHOOL);

        for (int idNum = 0; idNum < COURSES_PER_SCHOOL; idNum++) {

            CourseMeta courseMeta = new CourseMeta("course" + idNum, schoolMeta);

            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            coursesForSchool.put(courseMeta.id, courseMeta);
            courseMap.put(courseMeta.id, courseMeta);
        }

        return coursesForSchool;
    }

    /**
     * Generate the sessions for the school.
     * sessionsForSchool is used later in this class.
     * sessionMap is used to actually generate the XML.
     * @param schoolMeta
     * @return
     */
    private static Map<String, SessionMeta> buildSessionsForSchool(SchoolMeta schoolMeta) {

        Map<String, SessionMeta> sessionsForSchool = new HashMap<String, SessionMeta>(SESSIONS_PER_SCHOOL);

        for (int idNum = 0; idNum < SESSIONS_PER_SCHOOL; idNum++) {

            SessionMeta sessionMeta = new SessionMeta("session" + idNum, schoolMeta);

            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            sessionsForSchool.put(sessionMeta.id, sessionMeta);
            sessionMap.put(sessionMeta.id, sessionMeta);
        }

        return sessionsForSchool;
    }

    /**
     * Generate the sections for this school.
     * sectionMapForSchool is used later in this class.
     * sectionMap is used to actually generate the XML.
     * @param schoolMeta
     * @param coursesForSchool
     * @param sessionsForSchool
     * @return
     */
    private static Map<String, SectionMeta> buildSectionsForSchool(SchoolMeta schoolMeta,
            Map<String, CourseMeta> coursesForSchool, Map<String, SessionMeta> sessionsForSchool) {

        Map<String, SectionMeta> sectionMapForSchool = new HashMap<String, SectionMeta>();

        for (SessionMeta sessionMeta : sessionsForSchool.values()) {

            for (CourseMeta courseMeta : coursesForSchool.values()) {

                for (int idNum = 0; idNum < SECTIONS_PER_COURSE_SESSION; idNum++) {

                    SectionMeta sectionMeta = new SectionMeta("section" + idNum, schoolMeta, courseMeta, sessionMeta);

                    // it's useful to return the objects created JUST for this school
                    // add to both maps here to avoid loop in map.putAll if we merged maps later
                    sectionMapForSchool.put(sectionMeta.id, sectionMeta);
                    sectionMap.put(sectionMeta.id, sectionMeta);
                }
            }
        }

        return sectionMapForSchool;
    }

    /**
     * Generate the teachers for this school.
     * teachersInSchoolMap is used later in this class.
     * teacherMap is used to actually generate the XML.
     * @param schoolMeta
     * @return
     */
    private static Map<String, TeacherMeta> buildTeachersForSchool(SchoolMeta schoolMeta) {

        Map<String, TeacherMeta> teachersInSchoolMap = new HashMap<String, TeacherMeta>(TEACHERS_PER_SCHOOL);
        for (int idNum = 0; idNum < TEACHERS_PER_SCHOOL; idNum++) {

            TeacherMeta teacherMeta = new TeacherMeta("teacher" + idNum, schoolMeta);

            // it's useful to return the objects created JUST for this school
            // add to both maps here to avoid loop in map.putAll if we merged maps later
            teachersInSchoolMap.put(teacherMeta.id, teacherMeta);
            teacherMap.put(teacherMeta.id, teacherMeta);
        }

        return teachersInSchoolMap;
    }

    /**
     * Correlates teachers and sections on a 'per school' basis.
     * @param sectionsForSchool
     * @param teachersForSchool
     */
    private static void addTeachersToSections(Map<String, SectionMeta> sectionsForSchool,
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

}
