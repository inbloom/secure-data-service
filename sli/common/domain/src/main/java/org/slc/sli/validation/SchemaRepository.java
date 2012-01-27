package org.slc.sli.validation;


/**
 * Interface for reading schemas from XSD and converting them into neutral schema objects
 * 
 * @author nbrown
 *
 */
public interface SchemaRepository {
    
    /**
     * Gets the schema for the type
     * 
     * @param type the type for the schema to look up
     * @return the actual schema
     */
    public NeutralSchema getSchema(String type);
    
}
