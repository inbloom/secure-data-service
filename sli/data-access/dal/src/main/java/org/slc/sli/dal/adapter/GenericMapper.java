package org.slc.sli.dal.adapter;

import org.slc.sli.dal.adapter.transform.Transform;
import org.slc.sli.dal.adapter.transform.DatabaseTransformStore;
import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/12/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericMapper implements Mappable {

    private DatabaseTransformStore databaseTransformStore;
    private String type;

    public GenericMapper(DatabaseTransformStore databaseTransformStore, String type) {
        this.databaseTransformStore = databaseTransformStore;
        this.type = type;
    }

    @Override
    public List<Entity> readAll(List<TransformWorkItem> toTransform) {
        List<Entity> results = new ArrayList<Entity>();

        for (TransformWorkItem workItem : toTransform) {
            List<Transform> transforms = databaseTransformStore.getTransform(workItem.getToTransform().getType(),
                    workItem.getCurrentVersion(), workItem.getSchemaVersion());

            //TODO - need to sort these transforms by versions before applying them.

            Entity entity = null;
            for (Transform transform : transforms) {
                entity = transform.transformRead(workItem.getToTransform());
                workItem.setToTransform(entity);
                workItem.setEntityId(entity.getEntityId());
            }

            results.add(entity);
        }

        return results;
    }

    @Override
    public Entity read(Entity entity) {
        return null;
    }

    @Override
    public Entity write(TransformWorkItem toTransform) {
        Entity entity = null;
        List<Transform> transforms = databaseTransformStore.getTransform(toTransform.getToTransform().getType(),
                toTransform.getCurrentVersion(), toTransform.getSchemaVersion());

        //TODO - need to sort these transforms by versions before applying them.

        for (Transform transform : transforms) {
            entity = transform.transformWrite(toTransform.getToTransform());
            toTransform.setToTransform(entity);
            toTransform.setEntityId(entity.getEntityId());
        }

        if (entity != null) {
            return entity;
        }

        return toTransform.getToTransform();
    }

    @Override
    public List<Entity> acceptRead(String type, List<Entity> entities, NeutralQuery neutralQuery, SchemaVisitor visitor) {
        return visitor.visitRead(type, entities, neutralQuery, this);
    }

    @Override
    public Entity acceptWrite(String type, Entity entity, SchemaVisitor visitor) {
        return visitor.visitWrite(type, entity, this);
    }
}
