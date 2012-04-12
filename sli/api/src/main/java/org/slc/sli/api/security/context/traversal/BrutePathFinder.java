package org.slc.sli.api.security.context.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeBuilder;
import org.springframework.stereotype.Component;

/**
 * Basic brute force path finding implementation.
 */
@Component
public class BrutePathFinder implements SecurityPathFinder {
    private Map<String, SecurityNode> nodeMap;

    @PostConstruct
    public void init() {
        nodeMap = new HashMap<String, SecurityNode>();
        nodeMap.put("teacher",
                SecurityNodeBuilder.buildNode("teacher").addConnection(EntityNames.SECTION, "sectionId", null)
                        .construct());
        nodeMap.put(EntityNames.SECTION,
                SecurityNodeBuilder.buildNode(EntityNames.SECTION).addConnection("teacher", "teacherId", null)
                        .addConnection(EntityNames.STUDENT, "studentId", "sectionStudentAssociation").construct());
        nodeMap.put(
                EntityNames.STUDENT,
                SecurityNodeBuilder.buildNode(EntityNames.STUDENT)
                        .addConnection(EntityNames.SECTION, "sectionId", null)
                        .addConnection(EntityNames.ASSESSMENT, "assessmentId", null).construct());
    }

    @Override
    public List<SecurityNode> find(String from, String to) {
        Stack<SecurityNode> exploring = new Stack<SecurityNode>();
        List<SecurityNode> explored = new ArrayList<SecurityNode>();
        SecurityNode temp = null;
        temp = nodeMap.get(from);
        exploring.push(temp);
        debug("Starting with node: {}", temp.getName());
        while (!exploring.empty()) {
            temp = exploring.pop();
            explored.add(temp);
            debug("Marking {} as explored", temp.getName());
            if (checkForFinalNode(to, temp)) {
                debug("Returning a path of size {}", explored.size());
                return explored;
            }
            for (Map<String, String> connection : temp.getConnections()) {
                if (!explored.contains(nodeMap.get(connection.get("entity")))) {
                    debug("Enqueuing: {}", connection.get("entity"));
                    exploring.push(nodeMap.get(connection.get("entity")));
                }
            }
        }
        debug("NO PATH FOUND FROM {} to {}", new String[] {from, to});
        return null;
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
