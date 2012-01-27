package org.slc.sli.validation;

import javax.xml.namespace.QName;

/**
 * @author nbrown
 *
 */
public interface SchemaFactory {
    
    public NeutralSchema createSchema(QName qName);
    
    public NeutralSchema createSchema(String xsd);
    
}