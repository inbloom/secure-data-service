package org.slc.sli.api.security.context.traversal;

import org.slc.sli.api.security.context.traversal.graph.Node;

import java.util.List;

/**
 * Interface for whatever pathfinding algorithms you need.
 */
public interface SecurityPathFinder {
    List<Node> find(String from, String to);
}
