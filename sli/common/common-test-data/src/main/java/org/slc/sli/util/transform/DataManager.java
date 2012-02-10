package org.slc.sli.util.transform;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author dwilliams
 *
 */
public class DataManager {
    private static HashMap<String, School> schools = null;
    private static HashMap<String, Student> students = null;
    private static HashMap<String, Assessment> assessments = null;
    private static HashMap<String, Teacher> teachers = null;
    private static HashMap<String, Section> sections = null;
    private static HashMap<String, EducationalOrganization> edOrgs = null;
    private static HashMap<String, Session> sessions = null;
    private static HashMap<String, Course> courses = null;
    private static HashMap<String, StudentAssessment> studentAssessments = null;
    
    public static HashMap<String, School> getSchools() {
        return schools;
    }
    
    public static HashMap<String, Student> getStudents() {
        return students;
    }
    
    public static HashMap<String, Assessment> getAssessments() {
        return assessments;
    }
    
    public static void setSchools(HashMap<String, School> newSchools) {
        schools = newSchools;
    }
    
    public static void addSchools(HashMap<String, School> more) {
        if (schools == null) {
            schools = more;
        } else {
            Iterator<String> iter = more.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                School s = more.get(key);
                if (schools.containsKey(key)) {
                    School existing = schools.get(key);
                    if (s.getEdOrg() == null) {
                        s.setEdOrg(existing.getEdOrg());
                    }
                    s.mergeSessionAssociationsFrom(existing);
                }
                schools.put(key, s);
            }
        }
    }
    
    public static void setStudents(HashMap<String, Student> newStudents) {
        students = newStudents;
    }
    
    public static void addStudents(HashMap<String, Student> more) {
        if (students == null) {
            students = more;
        } else {
            students.putAll(more);
        }
    }
    
    public static void setAssessments(HashMap<String, Assessment> newAssessments) {
        assessments = newAssessments;
    }
    
    public static void addAssessments(HashMap<String, Assessment> more) {
        if (assessments == null) {
            assessments = more;
        } else {
            assessments.putAll(more);
        }
    }
    
    public static Student getStudent(String id) {
        return students.get(id);
    }
    
    public static Assessment getAssessment(String id) {
        return assessments.get(id);
    }
    
    public static School getSchool(String id) {
        return schools.get(id);
    }
    
    public static void setTeachers(HashMap<String, Teacher> newTeachers) {
        teachers = newTeachers;
    }
    
    public static void addTeachers(HashMap<String, Teacher> more) {
        if (teachers == null) {
            teachers = more;
        } else {
            teachers.putAll(more);
        }
    }
    
    public static Teacher getTeacher(String id) {
        return teachers.get(id);
    }
    
    public static HashMap<String, Teacher> getTeachers() {
        return teachers;
    }
    
    public static void setSections(HashMap<String, Section> newSections) {
        sections = newSections;
    }
    
    public static void addSections(HashMap<String, Section> more) {
        if (sections == null) {
            sections = more;
        } else {
            sections.putAll(more);
        }
    }
    
    public static HashMap<String, Section> getSections() {
        return sections;
    }
    
    public static Section getSection(String id) {
        return sections.get(id);
    }
    
    public static void setEdOrgs(HashMap<String, EducationalOrganization> newEdOrgs) {
        edOrgs = newEdOrgs;
    }
    
    public static void addEdOrgs(HashMap<String, EducationalOrganization> more) {
        if (edOrgs == null) {
            edOrgs = more;
        } else {
            Iterator<String> iter = more.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                EducationalOrganization newEdOrg = more.get(key);
                if (edOrgs.containsKey(key)) {
                    EducationalOrganization existing = edOrgs.get(key);
                    existing.updateFrom(newEdOrg);
                } else {
                    edOrgs.put(key, newEdOrg);
                }
            }
        }
    }
    
    public static HashMap<String, EducationalOrganization> getEdOrgs() {
        return edOrgs;
    }
    
    public static EducationalOrganization getEdOrg(String id) {
        return edOrgs.get(id);
    }
    
    public static void setSessions(HashMap<String, Session> newSessions) {
        sessions = newSessions;
    }
    
    public static void addSessions(HashMap<String, Session> more) {
        if (sessions == null) {
            sessions = more;
        } else {
            sessions.putAll(more);
        }
    }
    
    public static HashMap<String, Session> getSessions() {
        return sessions;
    }
    
    public static Session getSession(String id) {
        return sessions.get(id);
    }
    
    public static void setCourses(HashMap<String, Course> newCourses) {
        courses = newCourses;
    }
    
    public static void addCourses(HashMap<String, Course> more) {
        if (courses == null) {
            courses = more;
        } else {
            courses.putAll(more);
        }
    }
    
    public static HashMap<String, Course> getCourses() {
        return courses;
    }
    
    public static Course getCourse(String id) {
        return courses.get(id);
    }
    
    public static void setStudentAssessments(HashMap<String, StudentAssessment> newStudentAssessments) {
        studentAssessments = newStudentAssessments;
    }
    
    public static void addStudentAssessments(HashMap<String, StudentAssessment> more) {
        if (studentAssessments == null) {
            studentAssessments = more;
        } else {
            studentAssessments.putAll(more);
        }
    }
    
    public static HashMap<String, StudentAssessment> getStudentAssessments() {
        return studentAssessments;
    }
    
    public static StudentAssessment getStudentAssessment(String id) {
        return studentAssessments.get(id);
    }
}
