package org.slc.sli.api.security.context.traversal.graph;

import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 6/8/12
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NodeAggregator {
    public abstract List<String> addAssociatedIds(List<String> toResolve);
}
