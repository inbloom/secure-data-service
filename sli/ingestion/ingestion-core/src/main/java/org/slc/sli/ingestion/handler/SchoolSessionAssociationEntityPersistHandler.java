package org.slc.sli.ingestion.handler;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class used to persist Session and SchoolSessionAssociation in a certain order.
 *
 * @author ablum
 *
 */
public class SchoolSessionAssociationEntityPersistHandler extends EntityPersistHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SchoolSessionAssociationEntityPersistHandler.class);
    
    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        SimpleEntity session = (SimpleEntity) entity.getBody().remove("session");
        LOG.debug("session peeled off from school session association: {}", session);
        
        Entity mongoSession = super.doHandling(session, errorReport, fileProcessStatus);
        LOG.debug("id of created session: {}", mongoSession.getEntityId());
        entity.getBody().put("sessionId", mongoSession.getEntityId());

        Entity createdSchoolSessionAssociation = super.doHandling(entity, errorReport, fileProcessStatus);
        LOG.debug("created school session association: {}", createdSchoolSessionAssociation);
        return createdSchoolSessionAssociation;
    }
}
