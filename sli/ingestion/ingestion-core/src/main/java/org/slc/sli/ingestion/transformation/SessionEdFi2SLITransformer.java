package org.slc.sli.ingestion.transformation;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

/**
 * An implementation of SmooksEdFi2SLITransformer that has special logic to match the Session and
 * SchoolSessionAssociation entities
 * 
 * @author ablum
 * @author shalka
 */
public class SessionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SessionEdFi2SLITransformer.class);
    
    /**
     * Matches a Session/SchoolSessionAssociation with a Session/SchoolSessionAssociation in the
     * database.
     * 
     * @param entity
     *            The entity that needs to be matched in the database
     * @param errorReport
     *            The errorReport used to report errors
     */
    @Override
    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {
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
        
        SimpleEntity session = (SimpleEntity) entity.getBody().get("session");        
        if (match != null && match.iterator().hasNext()) {
            Entity existingSchoolSessionAssociation = match.iterator().next();
            entity.setEntityId(existingSchoolSessionAssociation.getEntityId());
            entity.getMetaData().putAll(existingSchoolSessionAssociation.getMetaData());
            
            Entity matchedSession = getEntityRepository().findById("session",
                    (String) existingSchoolSessionAssociation.getBody().get("sessionId"));
            session.setEntityId(matchedSession.getEntityId());
            session.getMetaData().putAll((matchedSession.getMetaData()));
            session.getMetaData().put(EntityMetadataKey.EXTERNAL_ID.getKey(), session.getBody().get("sessionName"));
        } else {
            session.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(),
                    entity.getMetaData().get(EntityMetadataKey.TENANT_ID.getKey()));
            session.getMetaData().put(EntityMetadataKey.EXTERNAL_ID.getKey(), session.getBody().get("sessionName"));
        }
        entity.getBody().put("session", session);
    }
}
