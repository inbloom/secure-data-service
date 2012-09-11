package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Handles schema mappings and adaptations
 *
 * @author srupasinghe
 *
 */

public interface SchemaVisitor {

    public Iterable<Entity> visitRead(String type, NeutralQuery neutralQuery, AttributeMapper mapper);

    public Entity visitWrite(String type, Entity entity, AttributeMapper mapper);

    public Iterable<Entity> visitRead(String type, NeutralQuery neutralQuery, LocationMapper mapper);

    public Entity visitWrite(String type, Entity entity, LocationMapper mapper);

}
