package org.slc.sli.validation;

import javax.xml.namespace.QName;

import org.slc.sli.validation.schema.NeutralSchema;

/**
 * @author nbrown
 *
 */
public interface SchemaFactory {

    public NeutralSchema createSchema(QName qName);

    public NeutralSchema createSchema(String xsd);

}
