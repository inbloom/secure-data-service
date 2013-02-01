/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.modeling.tools.xsdgen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xsd.WxsNamespace;

/**
 * Writes XSD.
 * 
 * @author kmyers
 *
 */
public final class PluginForREST implements Uml2XsdPlugin {

    // The target name-space should coincide with whatever is returned by the API.
    // Currently, we don't put the returned elements in a name-space.
    // private static final String TARGET_NAMESPACE = "";
    private static final String TARGET_NAMESPACE = "http://www.slcedu.org/api/v1";

    // The prefix for the target name-space can be empty or non-empty.
    // Using an empty prefix makes the generated schema less cluttered.
    // However, if non-empty, the prefix should be allocated dynamically to avoid collisions.
    private static final String TARGET_NAMESPACE_PREFIX = "";

    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final String getName(final AssociationEnd element, final ModelIndex lookup) {
        if (!element.getName().trim().isEmpty()) {
            return element.getName();
        } else {
            // Name using the element type. Could be more sophisticated here.
            return new Uml2XsdSyntheticHasName(element, lookup).getName();
        }
    }

    private static final QName getQName(final Type type, final ModelIndex lookup) {
        final Identifier id = type.getId();
        for (final ModelElement whereUsed : lookup.whereUsed(id)) {
            if (whereUsed instanceof UmlPackage) {
                final UmlPackage pkg = (UmlPackage) whereUsed;
                return new QName(pkg.getName(), type.getName());
            }
        }
        return new QName(TARGET_NAMESPACE, type.getName(), TARGET_NAMESPACE_PREFIX);
    }

    @Override
    public Map<String, String> declarePrefixMappings() {
        final Map<String, String> pms = new HashMap<String, String>();
        if (TARGET_NAMESPACE.trim().length() > 0) {
            pms.put(TARGET_NAMESPACE_PREFIX, TARGET_NAMESPACE);
        }
        return Collections.unmodifiableMap(pms);
    }

    @Override
    public QName getElementName(final String name, final boolean isReference) {
        // camel case is the convention for JSON.
        return new QName(TARGET_NAMESPACE, Uml2XsdTools.camelCase(name), TARGET_NAMESPACE_PREFIX);
    }

    @Override
    public QName getElementType(final String name, final boolean isAssociation) {
        return getTypeName(name);
    }

    @Override
    public QName getGraphAssociationEndName(final PsmDocument<Type> classType) {
        return new QName(TARGET_NAMESPACE, classType.getGraphAssociationEndName().getName(), TARGET_NAMESPACE_PREFIX);
    }

    @Override
    public QName getElementName(final PsmDocument<Type> classType) {
        return new QName(TARGET_NAMESPACE, classType.getSingularResourceName().getName(), TARGET_NAMESPACE_PREFIX);
    }

    @Override
    public String getTargetNamespace() {
        return TARGET_NAMESPACE;
    }

    @Override
    public QName getTypeName(final String name) {
        return new QName(TARGET_NAMESPACE, name, TARGET_NAMESPACE_PREFIX);
    }

    @Override
    public boolean isAttributeFormDefaultQualified() {
        return false;
    }

    @Override
    public boolean isElementFormDefaultQualified() {
        return true;
    }

    @Override
    public boolean isEnabled(final QName name) {
        return false;
    }

    @Override
    public void writeAppInfo(final TaggedValue taggedValue, final ModelIndex lookup, final Uml2XsdPluginWriter xsw) {
        // Ignore
    }

    @Override
    public void writeAssociation(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        // The existence of this feature depends on whether the association is logically navigable.
        if (end.isNavigable()) {
//            if (PluginHelpers.hasMongoName(end, model)) {
                writeEmbedded(complexType, end, model, xsw);
                // xsw.choice();
                // try {
                // writeReference(complexType, end, model, xsw);
                // writeEmbedded(complexType, end, model, xsw);
                // } finally {
                // xsw.end();
                // }
//            } else {
//                writeEmbedded(complexType, end, model, xsw);
//            }
        }
    }

    public void writeEmbedded(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            final String name = getName(end, model);
            final Type type = model.getType(end.getType());
            xsw.elementName(new QName(name));
            xsw.type(getTypeName(type.getName()));
            xsw.minOccurs(end.getMultiplicity().getRange().getLower());
            xsw.maxOccurs(end.getMultiplicity().getRange().getUpper());
            xsw.annotation();
            try {
                PluginHelpers.writeDocumentation(end, model, xsw);
            } finally {
                xsw.end();
            }
        } finally {
            xsw.end();
        }
    }

    public void writeReference(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            xsw.elementName(new QName(PluginHelpers.getMongoName(end, model)));
            xsw.type(WxsNamespace.STRING);
            xsw.minOccurs(end.getMultiplicity().getRange().getLower());
            xsw.maxOccurs(end.getMultiplicity().getRange().getUpper());
            xsw.annotation();
            try {
                PluginHelpers.writeDocumentation(end, model, xsw);
            } finally {
                xsw.end();
            }
        } finally {
            xsw.end();
        }
    }

    @Override
    public void writeTopLevelElement(final PsmDocument<Type> classType, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        final Type elementType = classType.getType();
        final QName elementName = getElementName(classType);
        final String ns = elementName.getNamespaceURI();
        final QName elementList = new QName(ns, elementName.getLocalPart().concat("List"));

        xsw.element();
        try {
            xsw.elementName(elementList);
            xsw.complexType();
            try {
                xsw.sequence();
                try {
                    xsw.element();
                    try {
                        xsw.ref(elementName);
                        xsw.minOccurs(Occurs.ZERO);
                        xsw.maxOccurs(Occurs.UNBOUNDED);
                        xsw.annotation();
                        try {
                            PluginHelpers.writeDocumentation(elementType, model, xsw);
                        } finally {
                            xsw.end();
                        }
                    } finally {
                        xsw.end();
                    }
                } finally {
                    xsw.end();
                }
            } finally {
                xsw.end();
            }
        } finally {
            xsw.end();
        }
        xsw.element();
        try {
            xsw.elementName(elementName);
            xsw.type(getQName(elementType, model));
            xsw.annotation();
            try {
                PluginHelpers.writeDocumentation(elementType, model, xsw);
            } finally {
                xsw.end();
            }
        } finally {
            xsw.end();
        }
    }
}
