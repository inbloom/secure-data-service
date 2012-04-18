package org.slc.sli.api.security.context.traversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeBuilder;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeConnection;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ResourceNames;

/**
 * Basic brute force path finding implementation.
 */
@Component
public class BrutePathFinder implements SecurityPathFinder {
    private Map<String, SecurityNode> nodeMap;
    private Map<String, List<SecurityNode>> prePath;
    private List<String> excludePath;

    @PostConstruct
    public void init() {
        nodeMap = new HashMap<String, SecurityNode>();
        prePath = new HashMap<String, List<SecurityNode>>();
        excludePath = new ArrayList<String>();
        nodeMap.put(EntityNames.TEACHER,
                SecurityNodeBuilder.buildNode("teacher")
                        .addConnection(EntityNames.SCHOOL, "schoolId", ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.TEACHER_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.COHORT, "cohortId", ResourceNames.STAFF_COHORT_ASSOCIATIONS)
                        .addConnection(EntityNames.PROGRAM, "staffId", "")
                        .construct());
        nodeMap.put(
                EntityNames.SCHOOL,
                SecurityNodeBuilder.buildNode(EntityNames.SCHOOL)
                        .addConnection(EntityNames.TEACHER, "teacherId", ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)
                        .construct());
        nodeMap.put(EntityNames.SECTION,
                SecurityNodeBuilder.buildNode(EntityNames.SECTION)
                        .addConnection(EntityNames.TEACHER, "teacherId", ResourceNames.TEACHER_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.STUDENT, "studentId", ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.COURSE, "courseId", EntityNames.SECTION)
                        .addConnection(EntityNames.SESSION, "sessionId", EntityNames.SECTION)
                        .construct());
        nodeMap.put(EntityNames.STUDENT,
                SecurityNodeBuilder.buildNode(EntityNames.STUDENT)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
                        .addConnection(EntityNames.ASSESSMENT, "assessmentId", ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS)
                        .addConnection(EntityNames.ATTENDANCE, "studentId", "")
                        .addConnection(EntityNames.DISCIPLINE_ACTION, "studentId", "")
                        .addConnection(EntityNames.DISCIPLINE_INCIDENT, "incidentId",
                                ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS)
                        .addConnection(EntityNames.PARENT, "parentId", ResourceNames.STUDENT_PARENT_ASSOCIATIONS)
                        .construct());

        // Leaf Nodes are unconnected
        nodeMap.put(EntityNames.ATTENDANCE, SecurityNodeBuilder.buildNode(EntityNames.ATTENDANCE).construct());
        nodeMap.put(EntityNames.PROGRAM, SecurityNodeBuilder.buildNode(EntityNames.PROGRAM).construct());
        nodeMap.put(EntityNames.COURSE, SecurityNodeBuilder.buildNode(EntityNames.COURSE).construct());
        nodeMap.put(EntityNames.SESSION, SecurityNodeBuilder.buildNode(EntityNames.SESSION).construct());

        nodeMap.put(EntityNames.COHORT, SecurityNodeBuilder.buildNode(EntityNames.COHORT).construct());
        nodeMap.put(EntityNames.ASSESSMENT, SecurityNodeBuilder.buildNode(EntityNames.ASSESSMENT).construct());
        nodeMap.put(EntityNames.DISCIPLINE_ACTION, SecurityNodeBuilder.buildNode(EntityNames.DISCIPLINE_ACTION)
                .construct());
        nodeMap.put(EntityNames.DISCIPLINE_INCIDENT, SecurityNodeBuilder.buildNode(EntityNames.DISCIPLINE_INCIDENT).construct());
        nodeMap.put(EntityNames.PARENT, SecurityNodeBuilder.buildNode(EntityNames.PARENT).construct());


        // excludePath.add(EntityNames.TEACHER + EntityNames.SECTION);

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

    }

    @Override
    public List<SecurityNode> find(String from, String to) {
        Stack<SecurityNode> exploring = new Stack<SecurityNode>();
        List<SecurityNode> explored = new ArrayList<SecurityNode>();
        SecurityNode temp = nodeMap.get(from);
        exploring.push(temp);
        debug("Generating a path: {} -> {}", new String[] { from, to });
        while (!exploring.empty()) {
            temp = exploring.pop();
            debug("Marking {} as explored", temp.getName());
            explored.add(temp);
            if (checkForFinalNode(to, temp)) {
                debug("Returning a path of size {}", explored.size());
                return explored;
            }
            boolean enqueued = false;
            for (SecurityNodeConnection connection : temp.getConnections()) {
                if (!explored.contains(nodeMap.get(connection.getConnectionTo()))) {
                    debug("Enqueuing: {}", connection.getConnectionTo());
                    exploring.push(nodeMap.get(connection.getConnectionTo()));
                    enqueued = true;
                }
            }
            if (!enqueued) {
                explored.remove(temp);
            }
        }
        debug("NO PATH FOUND FROM {} to {}", new String[] {from, to});
        return new ArrayList<SecurityNode>();
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

    private boolean checkForFinalNode(String to, SecurityNode temp) {
        return temp.getName().equals(to);
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
