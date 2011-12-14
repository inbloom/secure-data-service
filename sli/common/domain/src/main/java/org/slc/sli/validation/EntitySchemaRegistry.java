package org.slc.sli.validation;

import org.apache.avro.Schema;

import org.slc.sli.domain.Entity;

/**
 * Provides a registry for fetching avro schema.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

public interface EntitySchemaRegistry {
    
    /**
     * Returns the schema for the given type, or null if this schema has not been registered.
     * 
     * @param entity
     * @return
     */
    public Schema findSchemaForType(Entity entity);
    
}
