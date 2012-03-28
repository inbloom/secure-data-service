package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * EdFi to SLI data transformation
 *
 * @author okrook
 *
 */
public abstract class EdFi2SLITransformer implements Handler<NeutralRecord, List<SimpleEntity>> {

    private static final String METADATA_BLOCK = "metaData";

    private IdNormalizer idNormalizer;

    private EntityConfigFactory entityConfigurations;

    private Repository<Entity> entityRepository;

    @Override
    public List<SimpleEntity> handle(NeutralRecord item) {
        return handle(item, new DummyErrorReport());
    }

    @Override
    public List<SimpleEntity> handle(NeutralRecord item, ErrorReport errorReport) {
        resolveReferences(item, errorReport);

        if (errorReport.hasErrors()) {
            return Collections.emptyList();
        }

        List<SimpleEntity> transformed = transform(item, errorReport);

        if (errorReport.hasErrors()) {
            return Collections.emptyList();
        }

        for (SimpleEntity entity : transformed) {
            entity.getMetaData().put(EntityMetadataKey.ID_NAMESPACE.getKey(), item.getSourceId());

            matchEntity(entity, errorReport);

            if (errorReport.hasErrors()) {
                return Collections.emptyList();
            }
        }

        return transformed;
    }

    public void resolveReferences(NeutralRecord item, ErrorReport errorReport) {
        Entity entity = new NeutralRecordEntity(item);

        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);
    }

    /**
     * Find a matched entity in the data store. If match is found the EntityID gets updated with the
     * ID from the data store.
     *
     * @param entity
     *            Entity to match
     * @param entityConfig
     *            Configuration for the entity
     * @param errorReport
     *            Error reporting
     */
    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        Map<String, String> matchFilter = createEntityLookupFilter(entity, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Iterable<Entity> match = entityRepository.findAllByPaths(entity.getType(), matchFilter, new NeutralQuery());
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
            entity.getMetaData().putAll(matched.getMetaData());
        }
    }

    /**
     * Create entity lookup filter by fields
     *
     * @param entity : the entity to be looked up.
     * @param keyFields : the list of the fields with which to generate the filter
     * @param errorReport: error reporting
     * @return Look up filter
     *
     * @author tke
     */
    public Map<String, String> createEntityLookupFilter(SimpleEntity entity, EntityConfig entityConfig, ErrorReport errorReport) {
        Map<String, String> filter = new HashMap<String, String>();

        if (entityConfig.getKeyFields() == null || entityConfig.getKeyFields().size() == 0) {
            errorReport.fatal("Cannot find a match for an entity: No key fields specified", this);
        }

        String regionId = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();
        filter.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), regionId);

        try {
            for (String field : entityConfig.getKeyFields()) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                filter.put(field, (String) fieldValue);
            }
        } catch (Exception e) {
            errorReport.error("Invalid key fields", this);
        }

        return filter;
    }

    protected abstract List<SimpleEntity> transform(NeutralRecord item, ErrorReport errorReport);

    public IdNormalizer getIdNormalizer() {
        return idNormalizer;
    }

    public void setIdNormalizer(IdNormalizer idNormalizer) {
        this.idNormalizer = idNormalizer;
    }

    public EntityConfigFactory getEntityConfigurations() {
        return entityConfigurations;
    }

    public void setEntityConfigurations(EntityConfigFactory entityConfigurations) {
        this.entityConfigurations = entityConfigurations;
    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

}
