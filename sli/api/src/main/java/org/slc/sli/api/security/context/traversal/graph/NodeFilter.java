package org.slc.sli.api.security.context.traversal.graph;

import java.util.List;



/**
 *
 *
 */
public abstract class NodeFilter {

    public abstract List<String> filterIds(List<String> toResolve);

}
