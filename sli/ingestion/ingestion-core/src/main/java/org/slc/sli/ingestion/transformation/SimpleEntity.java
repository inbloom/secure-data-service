package org.slc.sli.ingestion.transformation;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * @author okrook
 *
 */
public class SimpleEntity implements Entity {
    private String type;
    private String entityId;
    private Map<String, Object> body;
    private Map<String, Object> metaData;
    private long recordNumber;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public long getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(long recordNumber) {
        this.recordNumber = recordNumber;
    }
    
    @Override
    public String toString() {
        StringBuilder entity = new StringBuilder();
        entity.append("[ Simple Entity: ");
        entity.append("{type: ").append(getType()).append(" }, ");
        entity.append("{entity id: ").append(getEntityId()).append(" }, ");
        entity.append("{body: ").append(getBody()).append(" }, ");
        entity.append("{metadata: ").append(getMetaData()).append(" }, ");
        entity.append("{record number: ").append(getRecordNumber()).append(" }");
        entity.append(" ]");
        return entity.toString();
    }
}
