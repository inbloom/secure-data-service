package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

import java.util.List;

/**
 * Handles schema mappings and adaptations
 *
 * @author srupasinghe
 *
 */

public interface SchemaVisitor {

    public List<Entity> visitRead(String type, List<Entity> entities, NeutralQuery neutralQuery, LocationMapper mapper);

    public Entity visitWrite(String type, Entity entity, LocationMapper mapper);

    public List<Entity> visitRead(String type, List<Entity> entities, NeutralQuery neutralQuery, GenericMapper mapper);

}
