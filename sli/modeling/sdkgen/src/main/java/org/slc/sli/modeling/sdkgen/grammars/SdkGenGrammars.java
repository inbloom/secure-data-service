package org.slc.sli.modeling.sdkgen.grammars;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;

public interface SdkGenGrammars {

    XmlSchemaElement getElement(final QName name);

}
