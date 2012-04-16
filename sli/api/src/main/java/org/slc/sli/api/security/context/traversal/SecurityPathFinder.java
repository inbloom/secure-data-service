package org.slc.sli.api.security.context.traversal;

import java.util.List;

import org.slc.sli.api.security.context.traversal.graph.SecurityNode;

/**
 * Interface for whatever pathfinding algorithms you need.
 */
public interface SecurityPathFinder {
    List<SecurityNode> find(String from, String to);
    
    List<SecurityNode> getPreDefinedPath(String from, String to);
}
