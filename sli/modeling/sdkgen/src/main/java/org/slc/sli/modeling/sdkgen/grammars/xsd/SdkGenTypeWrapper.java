package org.slc.sli.modeling.sdkgen.grammars.xsd;

import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
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
        // What's going on here?
        foo(xsdType, 0);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // sb.append("name : ").append(xsdType.getClass());
        sb.append("}");
        return sb.toString();
    }

    public static SdkGenType foo(final XmlSchemaObjectCollection items, final int depth) {
        for (int i = 0; i < items.getCount(); i++) {
            final XmlSchemaObject item = items.getItem(i);
            if (item instanceof XmlSchemaElement) {
                final XmlSchemaElement element = (XmlSchemaElement) item;
                System.out.println(element.getName());
                System.out.println(element.getMinOccurs());
                System.out.println(element.getMaxOccurs() == Long.MAX_VALUE);
                foo(element.getSchemaType(), depth + 1);
                return new SdkGenElementTypeWrapper();
            } else if (item instanceof XmlSchemaChoice) {
                final XmlSchemaChoice choice = (XmlSchemaChoice) item;
                System.out.println("choice");
                return new SdkGenChoiceTypeWrapper();
            } else {
                throw new AssertionError(item);
            }
        }
        throw new AssertionError();
    }

    public static SdkGenType foo(final XmlSchemaType xmlSchemaType, final int depth) {
        if (depth > 5) {
            return null;
        }
        if (xmlSchemaType instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) xmlSchemaType;
            final XmlSchemaParticle particle = complexType.getParticle();
            if (particle != null) {
                if (particle instanceof XmlSchemaSequence) {
                    final XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
                    final XmlSchemaObjectCollection items = sequence.getItems();
                    return foo(items, depth + 1);
                } else {
                    throw new AssertionError();
                }
            } else {
                return null;
            }
        } else if (xmlSchemaType instanceof XmlSchemaSimpleType) {
            final XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) xmlSchemaType;
            System.out.println(simpleType.getName());
            return new SdkGenSimpleTypeWrapper();
        } else {
            throw new AssertionError(xmlSchemaType);
        }
    }
}
