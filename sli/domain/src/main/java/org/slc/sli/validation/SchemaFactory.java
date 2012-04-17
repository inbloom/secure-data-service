package org.slc.sli.validation;

import javax.xml.namespace.QName;

import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Factory for creating Neutral Schemas
 * 
 * @author nbrown
 * 
 */
public interface SchemaFactory {
    
    public NeutralSchema createSchema(QName qName);
    
    public NeutralSchema createSchema(String xsd);
    
    /**
     * Create a copy for a neutral schema
     * 
     * @param toCopy
     * @return
     */
    public NeutralSchema copySchema(NeutralSchema toCopy);
    
}
