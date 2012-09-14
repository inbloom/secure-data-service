package org.slc.sli.dal.adapter.transform;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a transform work item
 *
 * @author srupasinghe
 */
public class TransformWorkItem {

    private int currentVersion;
    private int schemaVersion;
    private Entity toTransform;
    private String entityId;
    private NeutralQuery neutralQuery;

    public TransformWorkItem(String entityId, Entity toTransform) {
        this(1, 1, entityId, toTransform, null);
    }

    public TransformWorkItem(String entityId, Entity toTransform, NeutralQuery neutralQuery) {
        this(1, 1, entityId, toTransform, neutralQuery);
    }

    public TransformWorkItem(int currentVersion, int schemaVersion, String entityId, Entity toTransform, NeutralQuery neutralQuery) {
        this.currentVersion = currentVersion;
        this.schemaVersion = schemaVersion;
        this.entityId = entityId;
        this.toTransform = toTransform;
        this.neutralQuery = neutralQuery;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Entity getToTransform() {
        return toTransform;
    }

    public void setToTransform(Entity toTransform) {
        this.toTransform = toTransform;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public NeutralQuery getNeutralQuery() {
        return neutralQuery;
    }

    public void setNeutralQuery(NeutralQuery neutralQuery) {
        this.neutralQuery = neutralQuery;
    }
}
