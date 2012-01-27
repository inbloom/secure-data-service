package org.slc.sli.validation.utils;

import java.util.Map;

import org.slc.sli.validation.NeutralSchema;

/**
 * Interface for reading schemas from XSD and converting them into neutral schema objects
 * 
 * @author nbrown
 *
 */
public interface SchemaLoader {
    
    /**
     * Returns (loads if necessary) all the schema files
     * 
     * @return a map of types to schemas
     */
    public Map<String, NeutralSchema> getSchemas();
    
}
