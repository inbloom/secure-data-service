package org.slc.sli.dal.adapter.transform.visitor;

import org.slc.sli.dal.adapter.transform.DatabaseTransform;
import org.slc.sli.dal.adapter.transform.LocationTransform;
import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Transform visitor
 *
 * @author srupasinghe
 */
public interface TransformVisitor {

    public Entity visitRead(String type, TransformWorkItem transformWorkItem, DatabaseTransform transform);

    public List<Entity> visitReadAll(String type, List<TransformWorkItem> transformWorkItems, DatabaseTransform transform);

    public Entity visitWrite(String type, TransformWorkItem TransformWorkItem, DatabaseTransform transform);

    public Entity visitRead(String type, TransformWorkItem transformWorkItem, LocationTransform transform);

    public List<Entity> visitReadAll(String type, List<TransformWorkItem> transformWorkItems, LocationTransform transform);

    public Entity visitWrite(String type, TransformWorkItem TransformWorkItem, LocationTransform transform);
}
