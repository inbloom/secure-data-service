/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.sdkgen;

import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaDatatype;
import org.apache.ws.commons.schema.XmlSchemaDerivationMethod;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;

public final class Level3ClientJavaHelper {

    public static JavaType toJavaTypeFromSchemaElement(final XmlSchemaElement element, final Stack<QName> elementNames,
            final SdkGenGrammars grammars) {
        final QName elementName = element.getQName();
        @SuppressWarnings("unused")
        final long minOccurs = element.getMinOccurs();
        final long maxOccurs = element.getMaxOccurs();
        if (elementNames.contains(elementName)) {
            // Avoid infinite recursion.
            return JavaType.JT_OBJECT;
        }
        elementNames.push(elementName);
        try {
            final XmlSchemaType schemaType = element.getSchemaType();
            if (schemaType != null) {
                final JavaType primeType = toJavaTypeFromSchemaType(schemaType, elementNames, grammars);
                if (maxOccurs > 1) {
                    return JavaType.collectionType(JavaCollectionKind.LIST, primeType);
                } else {
                    return primeType;
                }
            } else {
                final QName refName = element.getRefName();
                final XmlSchemaElement referencedElement = grammars.getElement(refName);
                final JavaType primeType = toJavaTypeFromSchemaElement(referencedElement, elementNames, grammars);
                if (maxOccurs > 1) {
                    return JavaType.collectionType(JavaCollectionKind.LIST, primeType);
                } else {
                    return primeType;
                }
            }
        } finally {
            elementNames.pop();
        }
    }

    private static JavaType toJavaTypeFromSchemaType(final XmlSchemaType schemaType, final Stack<QName> elementNames,
            final SdkGenGrammars grammars) {
        if (schemaType == null) {
            throw new IllegalArgumentException("schemaType");
        }
        if (elementNames == null) {
            throw new IllegalArgumentException("elementNames");
        }
        if (schemaType instanceof XmlSchemaComplexType) {
            final XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaType;
            return toJavaTypeFromComplexType(complexType, elementNames, grammars);
        } else {
            throw new AssertionError(schemaType);
        }
    }

    private static JavaType toJavaTypeFromComplexType(final XmlSchemaComplexType complexType,
            final Stack<QName> elementNames, final SdkGenGrammars grammars) {
        final QName name = complexType.getQName();
        if (name != null) {
            return JavaType.complexType(name.getLocalPart(), JavaType.JT_OBJECT);
        } else {
            @SuppressWarnings("unused")
            final XmlSchemaDatatype dataType = complexType.getDataType();
            // System.out.println("dataType : " + dataType);
            @SuppressWarnings("unused")
            final XmlSchemaDerivationMethod deriveBy = complexType.getDeriveBy();
            // System.out.println("deriveBy : " + deriveBy);
            final XmlSchemaParticle particle = complexType.getParticle();
            if (particle != null) {
                return toJavaTypeFromParticle(particle, elementNames, grammars);
            } else {
                return JavaType.JT_OBJECT;
            }
        }
    }

    private static JavaType toJavaTypeFromParticle(final XmlSchemaParticle particle, final Stack<QName> elementNames,
            final SdkGenGrammars grammars) {
        if (particle == null) {
            throw new IllegalArgumentException("particle");
        }
        if (particle instanceof XmlSchemaSequence) {
            final XmlSchemaSequence sequence = (XmlSchemaSequence) particle;
            return toJavaTypeFromSequence(sequence, elementNames, grammars);
        } else {
            throw new AssertionError(particle);
        }
    }

    private static JavaType toJavaTypeFromSequence(final XmlSchemaSequence sequence, final Stack<QName> elementNames,
            final SdkGenGrammars grammars) {
        final XmlSchemaObjectCollection items = sequence.getItems();
        final int count = items.getCount();
        if (count == 1) {
            final XmlSchemaObject schemaObject = items.getItem(0);
            return toJavaTypeFromSchemaObject(schemaObject, elementNames, grammars);
        } else {
            for (int i = 0; i < count; i++) {
                final XmlSchemaObject schemaObject = items.getItem(i);
                @SuppressWarnings("unused")
                final JavaType child = toJavaTypeFromSchemaObject(schemaObject, elementNames, grammars);
            }
            return JavaType.JT_VOID;
        }
    }

    private static JavaType toJavaTypeFromSchemaObject(final XmlSchemaObject schemaObject,
            final Stack<QName> elementNames, final SdkGenGrammars grammars) {
        if (schemaObject instanceof XmlSchemaElement) {
            final XmlSchemaElement element = (XmlSchemaElement) schemaObject;
            // Just gone recursive on element depth.
            return toJavaTypeFromSchemaElement(element, elementNames, grammars);
        } else if (schemaObject instanceof XmlSchemaChoice) {
            return JavaType.JT_OBJECT;
        } else {
            throw new AssertionError(schemaObject);
        }
    }

}
