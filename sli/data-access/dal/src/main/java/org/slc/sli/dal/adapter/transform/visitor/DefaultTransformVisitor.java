package org.slc.sli.dal.adapter.transform.visitor;

import org.slc.sli.dal.adapter.transform.DatabaseTransform;
import org.slc.sli.dal.adapter.transform.LocationTransform;
import org.slc.sli.dal.adapter.transform.TransformStore;
import org.slc.sli.dal.adapter.transform.TransformWorkItem;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Main visitor for handling transformations
 *
 * @author srupasinghe
 */
@Component
public class DefaultTransformVisitor implements TransformVisitor {

    @Autowired
    @Qualifier("databaseTransformStore")
    private TransformStore<DatabaseTransform> databaseTransformStore;

    @Autowired
    private SchemaRepository schemaRepository;

    @Override
    public Entity visitRead(String type, TransformWorkItem transformWorkItem, DatabaseTransform transform) {
        NeutralSchema schema = schemaRepository.getSchema(type);
        //int schemaVersion = schema.getAppInfo().getSchemaVersion();
        //TODO
        int schemaVersion = 2;

        String version = (String) transformWorkItem.getToTransform().getMetaData().get("version");
        int docVersion = Integer.parseInt((version == null) ? "1" : version);

        if ((docVersion < schemaVersion) && transform.isTransformable(type, docVersion, schemaVersion)) {
            transformWorkItem.setCurrentVersion(docVersion);
            transformWorkItem.setSchemaVersion(schemaVersion);

            Entity transformedEntity = null;
            transformedEntity = transform.transformRead(transformWorkItem);
            transformWorkItem.setToTransform(transformedEntity);
        }

        return transformWorkItem.getToTransform();
    }

    @Override
    public List<Entity> visitReadAll(String type, List<TransformWorkItem> transformWorkItems, DatabaseTransform transform) {

        List<Entity> results = new ArrayList<Entity>();

        for (TransformWorkItem workItem : transformWorkItems) {
            results.add(visitRead(type, workItem, transform));
        }

        return results;
    }

    @Override
    public Entity visitWrite(String type, TransformWorkItem transformWorkItem, DatabaseTransform transform) {
        throw new UnsupportedOperationException("No need to transform on writes");
    }

    @Override
    public Entity visitRead(String type, TransformWorkItem transformWorkItem, LocationTransform transform) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entity> visitReadAll(String type, List<TransformWorkItem> transformWorkItems, LocationTransform transform) {
        List<String> ids = new ArrayList<String>();
        List<TransformWorkItem> toTransform = new ArrayList<TransformWorkItem>();

        for (TransformWorkItem workItem : transformWorkItems) {
            for (NeutralCriteria criteria : workItem.getNeutralQuery().getCriteria()) {
                if (criteria.getKey().equals("_id")) {
                    ids = (List<String>) criteria.getValue();
                    break;
                }
            }
        }

        for (String id : ids) {
            toTransform.add(new TransformWorkItem(1, 1, id, null, null));
        }

        return transform.transformReadAll(toTransform);
    }

    @Override
    public Entity visitWrite(String type, TransformWorkItem transformWorkItem, LocationTransform transform) {
        return transform.transformWrite(transformWorkItem);
    }
}
