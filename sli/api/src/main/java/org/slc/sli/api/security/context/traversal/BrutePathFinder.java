package org.slc.sli.api.security.context.traversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.resolver.EdOrgToChildEdOrgNodeFilter;
import org.slc.sli.api.security.context.resolver.StudentSectionAssociationEndDateFilter;
import org.slc.sli.api.security.context.resolver.TeacherToStaffCohortAssociationEndDateFilter;
import org.slc.sli.api.security.context.resolver.TeacherToStaffProgramAssociationEndDateFilter;
import org.slc.sli.api.security.context.resolver.StudentGracePeriodNodeFilter;
import org.slc.sli.api.security.context.resolver.StaffEdOrgStaffIDNodeFilter;
import org.slc.sli.api.security.context.resolver.StaffEdOrgEdOrgIDNodeFilter;
import org.slc.sli.api.security.context.resolver.TeacherStudentResolver;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeBuilder;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Basic brute force path finding implementation.
 */
@Component
public class BrutePathFinder implements SecurityPathFinder {
    private Map<String, SecurityNode> nodeMap;
    private Map<String, List<SecurityNode>> prePath;
    private List<String> excludePath;

    @Autowired
    private EdOrgToChildEdOrgNodeFilter edorgFilter;

    @Autowired
    private NodeFilter sectionGracePeriodNodeFilter;

    @Autowired
    private StudentGracePeriodNodeFilter studentGracePeriodNodeFilter;

    @Autowired
    private StudentSectionAssociationEndDateFilter studentSectionAssociationEndDateFilter;

    @Autowired
    private StaffEdOrgStaffIDNodeFilter staffEdOrgStaffIDNodeFilter;

    @Autowired
    private StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter;

    @Autowired
    private TeacherToStaffCohortAssociationEndDateFilter teacherToStaffCohortAssociationEndDateFilter;

    @Autowired
    private TeacherToStaffProgramAssociationEndDateFilter teacherToStaffProgramAssociationEndDateFilter;

    @Autowired
    private TeacherStudentResolver teacherStudentResolver;


    @PostConstruct
    public void init() {
        nodeMap = new HashMap<String, SecurityNode>();
        prePath = new HashMap<String, List<SecurityNode>>();
        excludePath = new ArrayList<String>();

        nodeMap.put(EntityNames.TEACHER,
                SecurityNodeBuilder.buildNode(EntityNames.TEACHER, EntityNames.STAFF)
                        .addConnection(EntityNames.STUDENT, teacherStudentResolver)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.TEACHER_SECTION_ASSOCIATIONS, sectionGracePeriodNodeFilter)
                        .addConnection(EntityNames.TEACHER_SECTION_ASSOCIATION, "teacherId")
                        .addConnection(EntityNames.SCHOOL, "schoolId", ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS,
                                Arrays.asList(staffEdOrgEdOrgIDNodeFilter, edorgFilter))
                        .addConnection(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "teacherId")
                        .construct());
        nodeMap.put(
                EntityNames.SCHOOL,
                SecurityNodeBuilder.buildNode(EntityNames.SCHOOL, EntityNames.EDUCATION_ORGANIZATION)
                        .addConnection(EntityNames.TEACHER, "teacherId", ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, staffEdOrgStaffIDNodeFilter)
                        .addConnection(EntityNames.TEACHER_SCHOOL_ASSOCIATION, "schoolId")
                        .addConnection(EntityNames.STAFF, "staffReference",
                                ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, staffEdOrgStaffIDNodeFilter)
                        .addConnection(EntityNames.STAFF_ED_ORG_ASSOCIATION, "educationOrganizationReference")
                        .construct());


        nodeMap.put(EntityNames.SECTION,
                SecurityNodeBuilder.buildNode(EntityNames.SECTION)
                        .addConnection(EntityNames.TEACHER, "teacherId", ResourceNames.TEACHER_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.TEACHER_SECTION_ASSOCIATION, "sectionId")
                        .addConnection(EntityNames.STUDENT, "studentId", ResourceNames.STUDENT_SECTION_ASSOCIATIONS, studentSectionAssociationEndDateFilter)
                        .addConnection(EntityNames.STUDENT_SECTION_ASSOCIATION, "sectionId")
                        .addConnection(EntityNames.COURSE, "courseId", EntityNames.SECTION)
                        .addConnection(EntityNames.SESSION, "sessionId", EntityNames.SECTION)
                        .addConnection(EntityNames.PROGRAM, "programReference", "")
                        .addConnection(EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY, "sectionId", "")
                        .addConnection(EntityNames.PROGRAM, "programReference")
                        .addConnection(EntityNames.GRADEBOOK_ENTRY, "sectionId", "")
                        .addConnection(EntityNames.SECTION_ASSESSMENT_ASSOCIATION, "sectionId")
                        .construct());

        nodeMap.put(EntityNames.STUDENT,
                SecurityNodeBuilder.buildNode(EntityNames.STUDENT)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.STUDENT_SECTION_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.ASSESSMENT, "assessmentId", ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS)
                        .addConnection(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.ATTENDANCE, "studentId")
                        .addConnection(EntityNames.DISCIPLINE_ACTION, "studentId")
                        .addConnection(EntityNames.DISCIPLINE_INCIDENT, "disciplineIncidentId",
                                ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS)
                        .addConnection(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.PARENT, "parentId", ResourceNames.STUDENT_PARENT_ASSOCIATIONS)
                        .addConnection(EntityNames.REPORT_CARD, "studentId", "")
                        .addConnection(EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY, "studentId", "")
                        .addConnection(EntityNames.STUDENT_PARENT_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.STUDENT_ACADEMIC_RECORD, "studentId")
                        .addConnection(EntityNames.STUDENT_PROGRAM_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, "studentId")
                        .addConnection(EntityNames.STUDENT_COHORT_ASSOCIATION, "studentId")
                        .construct());

        nodeMap.put(EntityNames.STAFF,
                SecurityNodeBuilder.buildNode(EntityNames.STAFF)
                        .addConnection(EntityNames.EDUCATION_ORGANIZATION, "educationOrganizationReference",
                                ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS,
                                Arrays.asList(staffEdOrgEdOrgIDNodeFilter, edorgFilter))
                        .addConnection(EntityNames.STAFF_ED_ORG_ASSOCIATION, "staffReference")
                        .addConnection(EntityNames.STAFF_PROGRAM_ASSOCIATION, "staffId")
                        .addConnection(EntityNames.STAFF_COHORT_ASSOCIATION, "staffId")
                        .construct());


        nodeMap.put(EntityNames.EDUCATION_ORGANIZATION,
                SecurityNodeBuilder.buildNode(EntityNames.EDUCATION_ORGANIZATION)
                        .addConnection(EntityNames.STAFF, "staffReference", ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, staffEdOrgStaffIDNodeFilter)
                        .addConnection(EntityNames.STAFF_ED_ORG_ASSOCIATION, "educationOrganizationReference")
                        .addConnection(EntityNames.STUDENT, "studentId", ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, studentGracePeriodNodeFilter)
                        .addConnection(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "schoolId")
                        .addConnection(EntityNames.SCHOOL, "", "")
                        .addConnection(EntityNames.PROGRAM, "programReference") //TODO: fix XSD
                        .addConnection(EntityNames.SECTION, "schoolId")
                        .construct());

        nodeMap.put(EntityNames.SESSION,
                SecurityNodeBuilder.buildNode(EntityNames.SESSION)
                        .addConnection(EntityNames.SCHOOL_SESSION_ASSOCIATION, "sessionId")
                        .addConnection(EntityNames.COURSE_OFFERING, "sessionId")
                        .addLocalReference(EntityNames.GRADING_PERIOD, "gradingPeriodReference")
                        .construct());

        // Leaf Nodes are unconnected
        nodeMap.put(EntityNames.SCHOOL_SESSION_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.SCHOOL_SESSION_ASSOCIATION).construct());
        nodeMap.put(EntityNames.TEACHER_SECTION_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.TEACHER_SECTION_ASSOCIATION).construct());
        nodeMap.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.TEACHER_SCHOOL_ASSOCIATION).construct());

        nodeMap.put(EntityNames.STUDENT_SECTION_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_SECTION_ASSOCIATION)
                .addConnection(EntityNames.STUDENT_COMPETENCY, "studentSectionAssociationId", "")
                .addConnection(EntityNames.GRADE, "studentSectionAssociationId", "")
                .construct());

        nodeMap.put(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_ASSESSMENT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_PARENT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_PARENT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_SCHOOL_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STAFF_ED_ORG_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STAFF_ED_ORG_ASSOCIATION).construct());
        nodeMap.put(EntityNames.COURSE_OFFERING, SecurityNodeBuilder.buildNode(EntityNames.COURSE_OFFERING).construct());

        nodeMap.put(EntityNames.REPORT_CARD,
                SecurityNodeBuilder.buildNode(EntityNames.REPORT_CARD)
                        .construct());

        nodeMap.put(EntityNames.GRADING_PERIOD,
                SecurityNodeBuilder.buildNode(EntityNames.GRADING_PERIOD)
                        .construct());

        nodeMap.put(EntityNames.ASSESSMENT, SecurityNodeBuilder.buildNode(EntityNames.ASSESSMENT).construct());
        nodeMap.put(EntityNames.ATTENDANCE, SecurityNodeBuilder.buildNode(EntityNames.ATTENDANCE).construct());
        nodeMap.put(EntityNames.COHORT, SecurityNodeBuilder.buildNode(EntityNames.COHORT).construct());
        nodeMap.put(EntityNames.COURSE, SecurityNodeBuilder.buildNode(EntityNames.COURSE).construct());
        nodeMap.put(EntityNames.DISCIPLINE_ACTION, SecurityNodeBuilder.buildNode(EntityNames.DISCIPLINE_ACTION).construct());
        nodeMap.put(EntityNames.DISCIPLINE_INCIDENT, SecurityNodeBuilder.buildNode(EntityNames.DISCIPLINE_INCIDENT).construct());
        nodeMap.put(EntityNames.PROGRAM, SecurityNodeBuilder.buildNode(EntityNames.PROGRAM).construct());
        nodeMap.put(EntityNames.PARENT, SecurityNodeBuilder.buildNode(EntityNames.PARENT).construct());

        nodeMap.put(EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY).construct());
        nodeMap.put(EntityNames.GRADEBOOK_ENTRY, SecurityNodeBuilder.buildNode(EntityNames.GRADEBOOK_ENTRY).construct());
        nodeMap.put(EntityNames.STUDENT_ACADEMIC_RECORD, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_ACADEMIC_RECORD)
                .addConnection(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, "studentAcademicRecordId").construct());
        nodeMap.put(EntityNames.STUDENT_COMPETENCY, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_COMPETENCY).construct());
        nodeMap.put(EntityNames.GRADE, SecurityNodeBuilder.buildNode(EntityNames.GRADE).construct());

        nodeMap.put(EntityNames.STAFF_COHORT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STAFF_COHORT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STAFF_PROGRAM_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STAFF_PROGRAM_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_COHORT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_COHORT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_PROGRAM_ASSOCIATION).construct());
        nodeMap.put(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.STUDENT_TRANSCRIPT_ASSOCIATION).construct());
        nodeMap.put(EntityNames.SECTION_ASSESSMENT_ASSOCIATION, SecurityNodeBuilder.buildNode(EntityNames.SECTION_ASSESSMENT_ASSOCIATION).construct());

        excludePath.add(EntityNames.TEACHER + EntityNames.EDUCATION_ORGANIZATION);
        excludePath.add(EntityNames.TEACHER + EntityNames.STUDENT);
        excludePath.add(EntityNames.TEACHER + EntityNames.COHORT);
        excludePath.add(EntityNames.TEACHER + EntityNames.PROGRAM);

        excludePath.add(EntityNames.STAFF + EntityNames.DISCIPLINE_INCIDENT);
        excludePath.add(EntityNames.STAFF + EntityNames.DISCIPLINE_ACTION);
        excludePath.add(EntityNames.STAFF + EntityNames.COHORT);
        excludePath.add(EntityNames.STAFF + EntityNames.PROGRAM);

        excludePath.add(EntityNames.TEACHER + EntityNames.STUDENT_SCHOOL_ASSOCIATION);


        prePath.put(
                EntityNames.TEACHER + EntityNames.TEACHER_SECTION_ASSOCIATION,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.TEACHER_SECTION_ASSOCIATION)));        
        
        prePath.put(
                EntityNames.STAFF + EntityNames.STAFF,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.STAFF)));

        prePath.put(
                EntityNames.STAFF + EntityNames.SECTION,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION)));

        prePath.put(
                EntityNames.STAFF + EntityNames.COURSE,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.COURSE)));

        prePath.put(
                EntityNames.STAFF + EntityNames.SESSION,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.SESSION)));

        prePath.put(
                EntityNames.STAFF + EntityNames.TEACHER,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SCHOOL), nodeMap.get(EntityNames.TEACHER)));

        prePath.put(
                EntityNames.STAFF + EntityNames.SCHOOL,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SCHOOL)));

        prePath.put(
                EntityNames.STAFF + EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.STUDENT_SECTION_GRADEBOOK_ENTRY)));

        prePath.put(
                EntityNames.STAFF + EntityNames.GRADEBOOK_ENTRY,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.GRADEBOOK_ENTRY)));

        prePath.put(
                EntityNames.STAFF + EntityNames.STUDENT_COMPETENCY,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.STUDENT_SECTION_ASSOCIATION),
                        nodeMap.get(EntityNames.STUDENT_COMPETENCY)));

        prePath.put(
                EntityNames.STAFF + EntityNames.GRADE,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.STUDENT_SECTION_ASSOCIATION),
                        nodeMap.get(EntityNames.GRADE)));

        prePath.put(
                EntityNames.TEACHER + EntityNames.TEACHER,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SCHOOL),
                        nodeMap.get(EntityNames.TEACHER)));

        prePath.put(
                EntityNames.TEACHER + EntityNames.SECTION,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION)));
        prePath.put(
                EntityNames.TEACHER + EntityNames.COURSE,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.COURSE)));
        prePath.put(
                EntityNames.TEACHER + EntityNames.SESSION,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.SESSION)));
        prePath.put(
                EntityNames.TEACHER + EntityNames.SCHOOL_SESSION_ASSOCIATION,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.SESSION), nodeMap.get(EntityNames.SCHOOL_SESSION_ASSOCIATION)));

        prePath.put(
                EntityNames.STAFF + EntityNames.SCHOOL_SESSION_ASSOCIATION,
                Arrays.asList(nodeMap.get(EntityNames.STAFF), nodeMap.get(EntityNames.EDUCATION_ORGANIZATION),
                        nodeMap.get(EntityNames.SECTION), nodeMap.get(EntityNames.SESSION),
                        nodeMap.get(EntityNames.SCHOOL_SESSION_ASSOCIATION)));

        prePath.put(
                EntityNames.TEACHER + EntityNames.SCHOOL,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SCHOOL)));

    }

    @Override
    public List<SecurityNode> find(String from, String to) {
        return find(from, to, new ArrayList<SecurityNode>());
    }

    public List<SecurityNode> find(String from, String to, List<SecurityNode> path) {
        SecurityNode current = nodeMap.get(from);
        path.add(current);

        if (from.equals(to)) {
            return path;
        }

        for (SecurityNodeConnection connection : current.getConnections()) {
            SecurityNode next = nodeMap.get(connection.getConnectionTo());
            if (path.contains(next)) {  //cycle
                continue;
            }
            List<SecurityNode> newPath = find(next.getName(), to, path);
            if (newPath != null) {
                return newPath;
            }
        }
        debug("NO PATH FOUND FROM {} to {}", new Object[]{from, to });
        path.remove(current);
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.api.security.context.traversal.SecurityPathFinder#getPreDefinedPath(java.lang
     * .String, java.lang.String)
     */
    @Override
    public List<SecurityNode> getPreDefinedPath(String from, String to) {
        if (prePath.containsKey(from + to)) {
            return prePath.get(from + to);
        }
        return new ArrayList<SecurityNode>();
    }

    public void setNodeMap(Map<String, SecurityNode> nodeMap) {
        this.nodeMap = nodeMap;
    }

    /**
     * @return the nodeMap
     */
    public Map<String, SecurityNode> getNodeMap() {
        return nodeMap;
    }

    public boolean isPathExcluded(String from, String to) {
        return excludePath.contains(from + to);
    }

}
