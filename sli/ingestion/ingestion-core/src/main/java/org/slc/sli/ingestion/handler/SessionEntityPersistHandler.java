package org.slc.sli.ingestion.handler;

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class used to persist Session .
 *
 * @author ablum
 *
 */
public class SessionEntityPersistHandler extends EntityPersistHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEntityPersistHandler.class);

    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        Entity mongoSession = super.doHandling(entity, errorReport, fileProcessStatus);
        LOG.debug("created session : {}", mongoSession);
        return mongoSession;
    }
}
