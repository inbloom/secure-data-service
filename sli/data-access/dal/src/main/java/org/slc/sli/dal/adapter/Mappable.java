package org.slc.sli.dal.adapter;

import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Encapsulates a schema mapping
 *
 * @author srupasinghe
 *
 */
public interface Mappable extends SchemaVisitable {

    public List<Entity> readAll(List<TransformWorkItem> toTransform);

    public Entity read(Entity entity);

    public Entity write(TransformWorkItem toTransform);
}
