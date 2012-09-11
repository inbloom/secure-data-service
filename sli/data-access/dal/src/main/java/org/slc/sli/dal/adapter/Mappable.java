package org.slc.sli.dal.adapter;

import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Encapsulates a schema mapping
 *
 * @author srupasinghe
 *
 */
public interface Mappable extends SchemaVisitable {

    public Iterable<Entity> readAll(List<String> ids, Iterable<Entity> entities);

    public Entity read(Entity entity);

    public Entity write(Entity entity);
}
