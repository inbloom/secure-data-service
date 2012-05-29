package org.slc.sli.ingestion.handler;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * A class used to persist Session and SchoolSessionAssociation in a certain order.
 *
 * @author ablum
 *
 */
public class SchoolSessionAssociationEntityPersistHandler extends EntityPersistHandler {

    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        SimpleEntity session = (SimpleEntity) entity.getBody().remove("session");

        Entity mongoSession = super.doHandling(session, errorReport, fileProcessStatus);

        entity.getBody().put("sessionId", mongoSession.getEntityId());

        return super.doHandling(entity, errorReport, fileProcessStatus);

    }
}
