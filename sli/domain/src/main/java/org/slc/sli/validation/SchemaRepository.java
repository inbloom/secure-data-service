package org.slc.sli.validation;

import org.slc.sli.validation.schema.NeutralSchema;


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
    
    /**
     * Gets the schema for an underlying field on the given type
     *
     * @param type the type for the schema to look up
     * @param field the potentially nested field whose schema is to be returned
     */
    public NeutralSchema getSchema(String type, String field);

}
