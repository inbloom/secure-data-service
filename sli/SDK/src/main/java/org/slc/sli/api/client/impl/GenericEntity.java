package org.slc.sli.api.client.impl;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.EntityType;
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
    private final Map<String, Link> links;
    private final String id;
    
    // Don't include / expect the type during marshaling.
    private final transient EntityType type;
    
    /**
     * Construct a new generic entity.
     * 
     * @param type
     *            Entity type for this entity.
     * @param id
     *            Entity identifier.
     * @param data
     *            Map representing the entity's data.
     * @param links
     *            A map of the entities links; key is the link name.
     */
    public GenericEntity(final EntityType type, final String id, final Map<String, Object> data,
            final Map<String, Link> links) {
        this.type = type;
        this.id = id;
        this.data = data;
        this.links = links;
    }
    
    /*
     * (non-Javadoc)
     * @see org.slc.sli.api.client.Entity#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    
    /*
     * (non-Javadoc)
     * @see org.slc.sli.api.client.Entity#getData()
     */
    @Override
    public Map<String, Object> getData() {
        return data;
    }
    
    /*
     * (non-Javadoc)
     * @see org.slc.sli.api.client.Entity#getLinks()
     */
    @Override
    public Map<String, Link> getLinks() {
        return links;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.api.client.Entity#getEntityType()
     */
    @Override
    public EntityType getEntityType() {
        return type;
    }
}
