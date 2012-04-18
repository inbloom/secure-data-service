package org.slc.sli.api.client.impl;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;

/**
 * Generic implementation of the Entity interface. This is implements the Entity interface
 * in the most generic way possible.
 *
 * @author asaarela
 */
@XmlRootElement
public class GenericEntity implements Entity {

    private final Map<String, Object> data;
    private final String type;

    /**
     * Construct a new generic entity.
     *
     * @param type
     *            Entity type for this entity.
     * @param data
     *            Map representing the entity's data.
     */
    public GenericEntity(final String type, final Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String getId() {
        if (data.containsKey(ENTITY_ID_KEY)) {
            return (String) data.get(ENTITY_ID_KEY);
        }
        return null;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public List<Link> getLinks() {

        if (data.containsKey(LINKS_KEY)) {
            return (List<Link>) data.get(LINKS_KEY);
        }
        return null;
    }

    @Override
    public String getEntityType() {
        return type;
    }
}

