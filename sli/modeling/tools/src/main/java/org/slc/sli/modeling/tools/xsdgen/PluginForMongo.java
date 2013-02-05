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
import org.slc.sli.modeling.psm.helpers.SliMongoConstants;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Writes XSD.
 * 
 * 
 * @author kmyers
 *
 */
public final class PluginForMongo implements Uml2XsdPlugin {
    
    /**
     * This is the type that is used whenever we have just a reference.
     */
    private static final String MONGO_REFERENCE_TYPE = "Reference";
    
    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final String getName(final AssociationEnd end, final ModelIndex lookup) {
        if (!end.getName().trim().isEmpty()) {
            // If a name has been specified explicitly, use it without change.
            return end.getName();
        } else {
            // Synthesize the name using the and accounting for naive plurality.
            final Occurs maxOccurs = end.getMultiplicity().getRange().getUpper();
            final boolean unbounded = isUnbounded(maxOccurs);
            final String name = Uml2XsdTools.camelCase(lookup.getType(end.getType()).getName());
            return name.concat(unbounded ? "Ids" : "Id");
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
        return new QName(type.getName());
    }
    
    public static final boolean isUnbounded(final Occurs occurs) {
        return occurs.equals(Occurs.UNBOUNDED);
    }
    
    @Override
    public Map<String, String> declarePrefixMappings() {
        final Map<String, String> pms = new HashMap<String, String>();
        pms.put("sli", SliMongoConstants.NAMESPACE_SLI);
        return Collections.unmodifiableMap(pms);
    }
    
    @Override
    public QName getElementName(final String name, final boolean isReference) {
        // camel case is the convention for JSON.
        return new QName(Uml2XsdTools.camelCase(name));
    }
    
    @Override
    public QName getElementType(final String name, final boolean isAssociation) {
        return getTypeName(name);
    }
    
    @Override
    public QName getGraphAssociationEndName(final PsmDocument<Type> classType) {
        return new QName(classType.getGraphAssociationEndName().getName());
    }
    
    @Override
    public QName getElementName(final PsmDocument<Type> classType) {
        return new QName(classType.getSingularResourceName().getName());
    }
    
    @Override
    public String getTargetNamespace() {
        return SliMongoConstants.NAMESPACE_SLI;
    }
    
    @Override
    public QName getTypeName(final String name) {
        return new QName(name);
    }
    
    @Override
    public boolean isAttributeFormDefaultQualified() {
        return true;
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
        final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
        xsw.appinfo();
        if (SliUmlConstants.TAGDEF_NATURAL_KEY.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_NATURAL_KEY.getLocalPart(),
                    SliMongoConstants.SLI_NATURAL_KEY.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_APPLY_NATURAL_KEYS.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_APPLY_NATURAL_KEYS.getLocalPart(),
                    SliMongoConstants.SLI_APPLY_NATURAL_KEYS.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_PII.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_PII.getLocalPart(), SliMongoConstants.SLI_PII.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_ENFORCE_READ.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_READ_ENFORCEMENT.getLocalPart(),
                    SliMongoConstants.SLI_READ_ENFORCEMENT.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_ENFORCE_WRITE.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_WRITE_ENFORCEMENT.getLocalPart(),
                    SliMongoConstants.SLI_WRITE_ENFORCEMENT.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_RELAXED_BLACKLIST.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_RELAXEDBLACKLIST.getLocalPart(),
                    SliMongoConstants.SLI_RELAXEDBLACKLIST.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_SECURITY_SPHERE.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_SECURITY_SPHERE.getLocalPart(),
                    SliMongoConstants.SLI_SECURITY_SPHERE.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_ASSOCIATION_KEY.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_ASSOCIATION_KEY.getLocalPart(),
                    SliMongoConstants.SLI_ASSOCIATION_KEY.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_BEGIN_DATE.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_BEGIN_DATE.getLocalPart(), SliMongoConstants.SLI_BEGIN_DATE.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_END_DATE.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_END_DATE.getLocalPart(), SliMongoConstants.SLI_END_DATE.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_FILTER_BEGIN_DATE_ON.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_FILTER_BEGIN_DATE_ON.getLocalPart(), SliMongoConstants.SLI_FILTER_BEGIN_DATE_ON.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        } else if (SliUmlConstants.TAGDEF_FILTER_END_DATE_ON.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_FILTER_END_DATE_ON.getLocalPart(), SliMongoConstants.SLI_FILTER_END_DATE_ON.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        }  else if (SliUmlConstants.TAGDEF_ASSOCIATED_DATED_COLLECTION.equals(tagDefinition.getName())) {
            xsw.begin("sli", SliMongoConstants.SLI_ASSOCIATED_DATED_COLLECTION.getLocalPart(), SliMongoConstants.SLI_ASSOCIATED_DATED_COLLECTION.getNamespaceURI());
            xsw.characters(taggedValue.getValue());
            xsw.end();
        }  else {
            throw new AssertionError(tagDefinition.getName());
        }
        xsw.end();
    }
    
    @Override
    public void writeAssociation(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        // The existence of this feature depends on whether the association is physically navigable.
        if (PluginHelpers.isMongoNavigable(end, model)) {
            final Type type = model.getType(end.getType());
            xsw.element();
            try {
                if (PluginHelpers.hasMongoName(end, model)) {
                    xsw.elementName(new QName(PluginHelpers.getMongoName(end, model)));
                } else {
                    xsw.elementName(new QName(getName(end, model)));
                }
                xsw.type(getTypeName(MONGO_REFERENCE_TYPE));
                xsw.minOccurs(end.getMultiplicity().getRange().getLower());
                xsw.maxOccurs(end.getMultiplicity().getRange().getUpper());
                xsw.annotation();
                PluginHelpers.writeDocumentation(end, model, xsw);
                xsw.appinfo();
                xsw.begin("sli", SliMongoConstants.SLI_REFERENCE_TYPE.getLocalPart(),
                        SliMongoConstants.SLI_REFERENCE_TYPE.getNamespaceURI());
                xsw.characters(getTypeName(type.getName()).getLocalPart());
                xsw.end();
                xsw.end();
                xsw.end();
            } finally {
                xsw.end();
            }
        }
    }
    
    @Override
    public void writeTopLevelElement(PsmDocument<Type> classType, ModelIndex model, Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            final QName name = getElementName(classType);
            xsw.elementName(name);
            final Type elementType = classType.getType();
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
