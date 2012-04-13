package org.slc.sli.ingestion.dal;

import java.util.Map;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;

/**
 * Provides encryption for staging database by extending the EntityEncryption. It creates a Pii Map by reading the configuration from the entity's json file.
 *
 * @author npandey
 *
 */
public class StagingEntityEncryption extends EntityEncryption {
    private EntityConfigFactory entityConfigFactory;

    public EntityConfigFactory getEntityConfigFactory() {
        return entityConfigFactory;
    }

    public void setEntityConfigFactory(EntityConfigFactory entityConfigFactory) {
        this.entityConfigFactory = entityConfigFactory;
    }

    @Override
    protected Map<String, Object> buildPiiMap(String entityType) {
        EntityConfig ec = entityConfigFactory.getEntityConfiguration(entityType);
        return ec != null ? ec.getPiiFields() : null;
    }
}
