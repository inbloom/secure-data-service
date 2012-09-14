package org.slc.sli.dal.adapter.transform;

import org.slc.sli.dal.adapter.transform.visitor.TransformVisitable;
import org.slc.sli.domain.Entity;

import java.util.List;


/**
 * Identifies a transform.
 *
 * @author srupasinghe
 */
public interface Transform extends TransformVisitable {

    public Entity transformWrite(TransformWorkItem toTransform);

    public Entity transformRead(TransformWorkItem toTransform);

    public List<Entity> transformReadAll(List<TransformWorkItem> toTransform);

    public boolean isTransformable(String type, int fromVersion, int toVersion);

    public boolean isTransformable(String type);
}
