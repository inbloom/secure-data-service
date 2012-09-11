package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Visitable interface for schema mappings
 *
 * @author srupasinghe
 *
 */
public interface SchemaVisitable {

    public Iterable<Entity> acceptRead(String type, NeutralQuery neutralQuery, SchemaVisitor visitor);

    public Entity acceptWrite(String type, Entity entity, SchemaVisitor visitor);
}
