package org.slc.sli.ingestion.transformation;

import java.util.List;

import org.slc.sli.domain.Entity;

/**
 * Container that holds multiple Entity records
 *
 * @author okrook
 *
 */
public class EntityContainer {
    private List<Entity> entities;

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
