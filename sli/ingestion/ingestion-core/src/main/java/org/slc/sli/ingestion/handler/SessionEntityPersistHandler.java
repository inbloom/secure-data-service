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
public class SessionEntityPersistHandler extends EntityPersistHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEntityPersistHandler.class);
    
    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        SimpleEntity schoolSessionAssociation = (SimpleEntity) entity.getBody().remove("schoolSessionAssociation");
        
        Entity mongoSession = super.doHandling(entity, errorReport, fileProcessStatus);
        if (mongoSession.getEntityId() != null) {
            LOG.debug("id of created session: {}", mongoSession.getEntityId());
            schoolSessionAssociation.getBody().put("sessionId", mongoSession.getEntityId());
            super.doHandling(schoolSessionAssociation, errorReport, fileProcessStatus);
        }       
        
        return mongoSession;
    }
}
