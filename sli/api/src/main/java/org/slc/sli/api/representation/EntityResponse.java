package org.slc.sli.api.representation;

import java.util.HashMap;

/**
 * Contents of entities, for use in XML return
 */
public class EntityResponse extends HashMap<String, Object> {
    private String entityCollectionName;
    private static final String ENTITY = "Entity";
    private static final long serialVersionUID = -8766900333518618999L;

    public EntityResponse(String entityCollectionName, Object object) {
        super();

        setEntityCollectionName(entityCollectionName);
        put(this.entityCollectionName, object);
    }

    public Object getEntity() {
        return this.get(entityCollectionName);
    }

    protected void setEntityCollectionName(String entityCollectionName) {
        if (entityCollectionName != null && !entityCollectionName.isEmpty()) {
            this.entityCollectionName = entityCollectionName;
        } else {
            this.entityCollectionName = ENTITY;
        }
    }

    public String getEntityCollectionName() {
        return this.entityCollectionName;
    }
}
