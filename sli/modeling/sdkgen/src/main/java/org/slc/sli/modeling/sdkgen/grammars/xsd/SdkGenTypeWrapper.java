package org.slc.sli.modeling.sdkgen.grammars.xsd;

import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;

public final class SdkGenTypeWrapper implements SdkGenType {

    private final XmlSchemaType xsdType;

    public SdkGenTypeWrapper(final XmlSchemaType xsdType) {
        if (xsdType == null) {
            throw new NullPointerException("xsdType");
        }
        this.xsdType = xsdType;
    }

    @Override
    public String toString() {
        foo();
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("name : ").append(xsdType.getClass());
        sb.append("}");
        return sb.toString();
    }

    public void foo() {
        if (xsdType instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) xsdType;
            final XmlSchemaParticle particle = complexType.getParticle();
            if (particle instanceof XmlSchemaSequence) {
                final XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
                final XmlSchemaObjectCollection items = sequence.getItems();
                for (int i = 0; i < items.getCount(); i++) {
                    @SuppressWarnings("unused")
                    final XmlSchemaObject item = items.getItem(i);
                }
            } else {
                throw new AssertionError();
            }
        } else if (xsdType instanceof XmlSchemaSimpleType) {
            @SuppressWarnings("unused")
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) xsdType;
            throw new AssertionError();
        } else {
            throw new AssertionError(xsdType);
        }
    }
}
