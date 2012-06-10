package org.slc.sli.api.security.context.traversal.graph;

import java.util.List;
import org.slc.sli.domain.Entity;



/**
 *
 *
 */
public abstract class NodeFilter {

    public abstract List<Entity> filterEntities(List<Entity> toResolve, String referenceField);

}
