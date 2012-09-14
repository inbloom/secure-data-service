package org.slc.sli.dal.adapter.transform.visitor;

import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Identifies if an object is visitable by the transform visitor
 *
 * @author srupasinghe
 */
public interface TransformVisitable {

    public List<Entity> acceptReadAll(String type, List<TransformWorkItem> toTransform, TransformVisitor visitor);

    public Entity acceptRead(String type, TransformWorkItem toTransform, TransformVisitor visitor);

    public Entity acceptWrite(String type, TransformWorkItem toTransform, TransformVisitor visitor);

}
