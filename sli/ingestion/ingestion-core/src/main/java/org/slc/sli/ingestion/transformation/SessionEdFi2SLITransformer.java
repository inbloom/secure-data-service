package org.slc.sli.ingestion.transformation;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The purpose behind overriding matchEntity is to set a flag that will be used to determine if a new schoolSessionAssociation record
 * is to be created.
 *
 * @author ablum
 * @author shalka
 */
public class SessionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEdFi2SLITransformer.class);

    /**
     * Matches a Session with a Session in the database.
     *
     * @param entity
     *            The entity that needs to be matched in the database
     * @param errorReport
     *            The errorReport used to report errors
     */

    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {

    /* This code is identical to EdFiSLITransformer. We will remove this as the schoolSessionAssociat is removed */

        EntityConfig entityConfig = getEntityConfigurations().getEntityConfiguration(entity.getType());
        if (entityConfig == null || entityConfig.getReferences().isEmpty()) {
            LOG.warn("Cannot find reference configuration for entity of type: {}", entity.getType());
            return;
        }
        LOG.debug("Found reference configuration for entity of type: {}", entity.getType());

        Query query = createEntityLookupQuery(entity, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        @SuppressWarnings("deprecation")
        Iterable<Entity> match = getEntityRepository().findByQuery(entity.getType(), query, 0, 0);
        SimpleEntity ssAssociation = (SimpleEntity) entity.getBody().get("schoolSessionAssociation");
        ssAssociation.getBody().put("matched","false");
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
            entity.getMetaData().putAll(matched.getMetaData());
        }

    }
}
