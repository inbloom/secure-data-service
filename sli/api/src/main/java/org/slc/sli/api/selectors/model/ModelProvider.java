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

package org.slc.sli.api.selectors.model;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Set;

/**
 * Provides information from the model about Associations and Attributes
 * @author jstokes
 */

@Component
public class ModelProvider {
    private final ModelIndex modelIndex;
    private static final String DEFAULT_XMI_LOC = "/sliModel/SLI.xmi";

    public ModelProvider(final String xmiLoc) {
        final Model model = XmiReader.readModel(getClass().getResourceAsStream(xmiLoc));
        modelIndex = new DefaultModelIndex(model);
    }

    public ModelProvider() {
        this(DEFAULT_XMI_LOC);
    }

    public ModelProvider(final ModelIndex modelIndex) {
        this.modelIndex = modelIndex;
    }

    public List<AssociationEnd> getAssociationEnds(final Identifier type) {
        return modelIndex.getAssociationEnds(type);
    }

    public Set<ModelElement> lookupByName(final QName qName) {
        return modelIndex.lookupByName(qName);
    }

    public TagDefinition getTagDefinition(final Identifier id) {
        return modelIndex.getTagDefinition(id);
    }

    public Type getType(final Identifier id) {
        return modelIndex.getType(id);
    }

    public ClassType getClassType(final String typeName) {
        return modelIndex.getClassTypes().get(typeName);
    }

    public ClassType getClassType(final ClassType type, final String attr) {
        if (isAssociation(type, attr)) {
            return getAssociationType(type, attr);
        } else if (isAttribute(type, attr)) {
            return getEmbeddedClassType(type, attr);
        }
        return null;
    }

    public boolean isAttribute(final ClassType type, final String attributeName) {
        if (type == null) throw new NullPointerException("type");
        if (attributeName == null) throw new NullPointerException("attributeName");

        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAssociation(final ClassType type, final String attribute) {
        if (type == null) throw new NullPointerException("type");
        if (attribute == null) throw new NullPointerException("attribute");

        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    public Set<ModelElement> whereUsed(Identifier identifier) {
        return modelIndex.whereUsed(identifier);
    }

    public ModelIndex getModelIndex() {
        return modelIndex;
    }

    public Attribute getAttributeType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                return attribute;
            }
        }
        return null;
    }

    private ClassType getEmbeddedClassType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                final Type embeddedType = getType(attribute.getType());
                if (embeddedType.isClassType()) return (ClassType) embeddedType;
                else break;
            }
        }
        return null;
    }

    private ClassType getAssociationType(final ClassType type, final String attr) {
        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attr)) {
                final Type matchType = getType(end.getType());
                if (matchType.isClassType()) return (ClassType) matchType;
                else break;
            }
        }
        return null;
    }

    public ModelElement getModelElement(final ClassType type, final String key) {
        if (isAssociation(type, key)) {
            return getAssociationType(type, key);
        } else if (isAttribute(type, key)) {
            return getAttributeType(type, key);
        }
        return null;
    }

    public Type getConnectingEntityType(Type currentType, Type previousType) {
        List<AssociationEnd> ends = getAssociationEnds(previousType.getId());

        for (AssociationEnd first : ends) {
            Type firstType = getType(first.getType());
            ClassType firstClassType = getClassType(firstType.getName());

            if (firstClassType.isAssociation()) {
                List<AssociationEnd> secondLevel = getAssociationEnds(firstType.getId());

                for (AssociationEnd second : secondLevel) {
                    Type secondType = getType(second.getType());
                    ClassType secondClassType = getClassType(secondType.getName());

                    if (secondClassType.isClassType() && secondType.equals(currentType)) {
                        return firstType;
                    }
                }
            }
        }

        return null;
    }
}

