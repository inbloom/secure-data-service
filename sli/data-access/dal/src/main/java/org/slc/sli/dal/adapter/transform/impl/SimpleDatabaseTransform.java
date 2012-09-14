package org.slc.sli.dal.adapter.transform.impl;

import org.slc.sli.dal.adapter.transform.DatabaseTransform;
import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.dal.adapter.transform.visitor.TransformVisitor;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample transform
 *
 * @author srupasinghe
 */
@Component
public class SimpleDatabaseTransform implements DatabaseTransform {

    private static final String KEY = "populationServed";
    private static final String NEW_KEY = "newAttribute";

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate template;

    @Override
    public Entity transformWrite(TransformWorkItem toTransform) {
        Entity entity = toTransform.getToTransform();
        String value = (String) entity.getBody().get(NEW_KEY);
        entity.getBody().remove(NEW_KEY);
        entity.getBody().put(KEY, value);

        return entity;
    }

    @Override
    public Entity transformRead(TransformWorkItem toTransform) {
        Entity entity = toTransform.getToTransform();
        String value = (String) entity.getBody().get(KEY);
        entity.getBody().remove(KEY);
        entity.getBody().put(NEW_KEY, value);

        return entity;
    }

    @Override
    public List<Entity> transformReadAll(List<TransformWorkItem> toTransform) {
        List<Entity> results = new ArrayList<Entity>();
        for (TransformWorkItem workItem : toTransform) {
            results.add(transformRead(workItem));
        }

        return results;
    }

    @Override
    public boolean isTransformable(String type, int fromVersion, int toVersion) {
        if (type.equals("section")) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isTransformable(String type) {
        if (type.equals("section")) {
            return true;
        }

        return false;
    }

    @Override
    public List<Entity> acceptReadAll(String type, List<TransformWorkItem> toTransform, TransformVisitor visitor) {
        return visitor.visitReadAll(type, toTransform, this);
    }

    @Override
    public Entity acceptRead(String type, TransformWorkItem toTransform, TransformVisitor visitor) {
        return visitor.visitRead(type, toTransform, this);
    }

    @Override
    public Entity acceptWrite(String type, TransformWorkItem toTransform, TransformVisitor visitor) {
        return visitor.visitWrite(type, toTransform, this);
    }
}
