package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

import java.util.List;

/**
 * Visitable interface for schema mappings
 *
 * @author srupasinghe
 *
 */
public interface SchemaVisitable {

    public List<Entity> acceptRead(String type, List<Entity> entities, NeutralQuery neutralQuery, SchemaVisitor visitor);

    public Entity acceptWrite(String type, Entity entity, SchemaVisitor visitor);
}
