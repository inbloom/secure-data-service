package org.slc.sli.api.security.context.traversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeBuilder;
import org.springframework.stereotype.Component;

/**
 * Basic brute force path finding implementation.
 */
@Component
public class BrutePathFinder implements SecurityPathFinder {
    private Map<String, SecurityNode> nodeMap;
    private Map<String, List<SecurityNode>> prePath;

    @PostConstruct
    public void init() {
        nodeMap = new HashMap<String, SecurityNode>();
        prePath = new HashMap<String, List<SecurityNode>>();
        nodeMap.put(EntityNames.TEACHER,
                SecurityNodeBuilder.buildNode("teacher")
                        .addConnection(EntityNames.SCHOOL, "schoolId", ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.TEACHER_SECTION_ASSOCIATIONS)
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
                        .construct());
        nodeMap.put(EntityNames.STUDENT,
                SecurityNodeBuilder.buildNode(EntityNames.STUDENT)
                        .addConnection(EntityNames.SECTION, "sectionId", ResourceNames.STUDENT_SECTION_ASSOCIATIONS)
                        .construct());

        prePath.put(
                EntityNames.TEACHER + EntityNames.TEACHER,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SCHOOL),
                        nodeMap.get(EntityNames.TEACHER)));
        
        prePath.put(
                EntityNames.TEACHER + EntityNames.SECTION,
                Arrays.asList(nodeMap.get(EntityNames.TEACHER), nodeMap.get(EntityNames.SECTION),
                        nodeMap.get(EntityNames.STUDENT), nodeMap.get(EntityNames.SECTION)));

    }

    @Override
    public List<SecurityNode> find(String from, String to) {
        Stack<SecurityNode> exploring = new Stack<SecurityNode>();
        List<SecurityNode> explored = new ArrayList<SecurityNode>();
        SecurityNode temp = nodeMap.get(from);
        exploring.push(temp);
        debug("Starting with node: {}", temp.getName());
        while (!exploring.empty()) {
            temp = exploring.pop();
            debug("Marking {} as explored", temp.getName());
            explored.add(temp);
            if (checkForFinalNode(to, temp)) {
                debug("Returning a path of size {}", explored.size());
                return explored;
            }
            boolean enqueued = false;
            for (Map<String, String> connection : temp.getConnections()) {
                if (!explored.contains(nodeMap.get(connection.get("entity")))) {
                    debug("Enqueuing: {}", connection.get("entity"));
                    exploring.push(nodeMap.get(connection.get("entity")));
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
        if(prePath.containsKey(from+to)) {
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

}
