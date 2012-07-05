package org.slc.sli.modeling.sdkgen.grammars.xsd;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.slc.sli.modeling.sdkgen.grammars.SdkGenElement;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;

public final class SdkGenElementWrapper implements SdkGenElement {

    private final XmlSchemaElement element;

    public SdkGenElementWrapper(final XmlSchemaElement element) {
        if (element == null) {
            throw new NullPointerException("element");
        }
        this.element = element;
    }

    @Override
    public QName getName() {
        return element.getQName();
    }

    public SdkGenType getType() {
        final XmlSchemaType schemaType = element.getSchemaType();
        return new SdkGenTypeWrapper(schemaType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("name : ").append(element.getQName());
        sb.append("}");
        return sb.toString();
    }
}
