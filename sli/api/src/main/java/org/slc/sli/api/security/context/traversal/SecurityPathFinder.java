package org.slc.sli.api.security.context.traversal;

import org.slc.sli.api.security.context.traversal.graph.SecurityNode;

import java.util.List;

/**
 * Interface for whatever pathfinding algorithms you need.
 */
public interface SecurityPathFinder {
    List<SecurityNode> find(String from, String to);
}
