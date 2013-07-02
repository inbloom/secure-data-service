package org.slc.sli.ingestion.dal;

import org.slc.sli.domain.CascadeResult;
import org.slc.sli.domain.CascadeResultError;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bsuzuki
 * Date: 4/5/13
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SafeDeleteException extends RuntimeException {

    final CascadeResult.Status status;
    final List<CascadeResultError> errors;
    final String entityId;
    final String entityType;

    public SafeDeleteException(CascadeResult.Status status, String entityId, String entityType, List<CascadeResultError> errors) {
        super();
        this.status = status;
        this.entityId = entityId;
        this.entityType = entityType;
        this.errors = Collections.unmodifiableList(errors);
    }

    public CascadeResult.Status getStatus() {
        return status;
    }

    public List<CascadeResultError> getErrors() {
        return errors;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }
}


