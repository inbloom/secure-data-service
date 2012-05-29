package org.slc.sli.ingestion.transformation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * An implementation of SmooksEdFi2SLITransformer that has special logic to match the Session and SchoolSessionAssociation entities
 * @author ablum
 *
 */
public class SessionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEdFi2SLITransformer.class);

    /**
     * Matches a Session/SchoolSessionAssociation with a Session/SchoolSessionAssociation in the database
     * This is a special case, so it used the IdNormalizers resolveInternalIds method to resolve the match
     * @param entity The entity that needs to be matched in the database
     * @param errorReport The errorReport used to report errors
     */
    @Override
    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {
        EntityConfig entityConfig = getEntityConfigurations().getEntityConfiguration(entity.getType());

        if (entityConfig.getReferences().isEmpty()) {
            LOG.warn("Cannot find complex reference configuration file");
            return;
        }
        List<String> ids = getIdNormalizer().resolveReferenceInternalIds((Entity) entity, (String) entity.getMetaData().get("tenantId"), entityConfig.getReferences().get(0).getRef(), "", errorReport);

        SimpleEntity session = (SimpleEntity) entity.getBody().get("session");
        if (ids != null  && ids.size() > 0) {
            Entity matchSSA = getEntityRepository().findById(entity.getType(), ids.get(0));
            entity.setEntityId(matchSSA.getEntityId());
            entity.getMetaData().putAll(matchSSA.getMetaData());

            Entity matchedSession = getEntityRepository().findById("session", (String) matchSSA.getBody().get("sessionId"));
            session.setEntityId(matchedSession.getEntityId());
            session.getMetaData().putAll((matchedSession.getMetaData()));
            session.getMetaData().put(EntityMetadataKey.EXTERNAL_ID.getKey(), session.getBody().get("sessionName"));
        } else {
            session.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), entity.getMetaData().get(EntityMetadataKey.TENANT_ID.getKey()));
            session.getMetaData().put(EntityMetadataKey.EXTERNAL_ID.getKey(), session.getBody().get("sessionName"));
        }
       entity.getBody().put("session", session);
    }
}
