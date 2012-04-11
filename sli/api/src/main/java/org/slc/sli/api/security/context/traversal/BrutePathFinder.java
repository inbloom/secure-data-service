package org.slc.sli.api.security.context.traversal;

import org.slc.sli.api.security.context.traversal.graph.Node;
import org.slc.sli.api.security.context.traversal.graph.NodeBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic brute force path finding implementation.
 */
@Component
public class BrutePathFinder implements SecurityPathFinder {

    private Map<String, Node> nodeMap;

    @PostConstruct
    public void init() {
        nodeMap = new HashMap<String, Node>();
        nodeMap.put("teacher", NodeBuilder.buildNode("teacher").addConnection("section", "sectionId", null).construct());
        nodeMap.put("section", NodeBuilder.buildNode("section").addConnection("teacher", "teacherId", null).construct());
        nodeMap.put("student", NodeBuilder.buildNode("student").addConnection("section", "sectionId", null).construct());
    }

    @Override
    public List<Node> find(String from, String to) {
        return null;
    }
}
