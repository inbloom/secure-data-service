package org.slc.sli.dal.adapter.transform;

import org.slc.sli.domain.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 9/12/12
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransformWorkItem {

    private int currentVersion;
    private int schemaVersion;
    private Entity toTransform;
    private String entityId;

    public TransformWorkItem(int currentVersion, int schemaVersion, String entityId, Entity toTransform) {
        this.currentVersion = currentVersion;
        this.schemaVersion = schemaVersion;
        this.entityId = entityId;
        this.toTransform = toTransform;
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
}
