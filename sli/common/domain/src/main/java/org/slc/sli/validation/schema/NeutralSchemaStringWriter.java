package org.slc.sli.validation.schema;

/**
 * Interface for defining a neutral schema string write operation. A transform operation
 * takes the provided neutral schema, applies transform logic, and returns a string
 * representing the resulting schema representation.
 * 
 * @author asaarela
 * 
 */
public interface NeutralSchemaStringWriter {
    
    /**
     * Transform the provided NeutralSchema object and return the resulting representation.
     * 
     * @param schema
     *            NeutralSchema to transform
     * @return String containing the new representation.
     */
    String transform(NeutralSchema schema);
    
}
