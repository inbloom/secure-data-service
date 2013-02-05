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

package org.slc.sli.api.model;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Provides information from the model about Associations and Attributes
 *
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
        List<Generalization> baseGeneralizations = modelIndex.getGeneralizationBase(type);
        List<AssociationEnd> baseEnds = new ArrayList<AssociationEnd>();
        List<AssociationEnd> fullAssociationEnds = new ArrayList<AssociationEnd>();

        if (baseGeneralizations != null) {
            for (Generalization generalization : baseGeneralizations) {
                baseEnds.addAll(getAssociationEnds(generalization.getParent()));
            }
        }

        List<AssociationEnd> associationEnds = modelIndex.getAssociationEnds(type);

        fullAssociationEnds.addAll(baseEnds);
        fullAssociationEnds.addAll(associationEnds);

        return fullAssociationEnds;
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
        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        if (attributeName == null) {
            throw new IllegalArgumentException("attributeName");
        }

        final List<Attribute> attributes = getAttributes(type);
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAssociation(final ClassType type, final String attribute) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }

        if (attribute == null) {
            throw new IllegalArgumentException("attribute");
        }

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
        final List<Attribute> attributes = getAttributes(type);
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                return attribute;
            }
        }
        return null;
    }

    public List<Attribute> getAttributes(final ClassType type) {
        List<Generalization> baseGeneralizations = modelIndex.getGeneralizationBase(type.getId());
        List<Attribute> baseAttributes = new ArrayList<Attribute>();
        List<Attribute> fullAttributes = new ArrayList<Attribute>();

        if (baseGeneralizations != null) {
            for (Generalization generalization : baseGeneralizations) {
                Type baseType = modelIndex.getType(generalization.getParent());

                if (baseType != null) {
                    ClassType baseClassType = modelIndex.getClassTypes().get(baseType.getName());
                    baseAttributes.addAll(baseClassType.getAttributes());
                }
            }
        }

        fullAttributes.addAll(baseAttributes);
        fullAttributes.addAll(type.getAttributes());

        return fullAttributes;
    }

    private ClassType getEmbeddedClassType(final ClassType type, final String attr) {
        final List<Attribute> attributes = type.getAttributes();
        for (final Attribute attribute : attributes) {
            if (attribute.getName().equals(attr)) {
                final Type embeddedType = getType(attribute.getType());
                if (embeddedType.isClassType()) {
                    return (ClassType) embeddedType;
                } else {
                    break;
                }
            }
        }
        return null;
    }

    private ClassType getAssociationType(final ClassType type, final String attr) {
        final List<AssociationEnd> associationEnds = getAssociationEnds(type.getId());
        for (final AssociationEnd end : associationEnds) {
            if (end.getName().equals(attr)) {
                final Type matchType = getType(end.getType());
                if (matchType.isClassType()) {
                    return (ClassType) matchType;
                } else {
                    break;
                }
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

    public String getConnectionPath(final ClassType fromEntityType, final ClassType toEntityType) {
        List<AssociationEnd> associationEnds = getAssociationEnds(fromEntityType.getId());
        List<Generalization> generalizations = modelIndex.getGeneralizationBase(toEntityType.getId());

        List<Generalization> derivedGeneralizations = modelIndex.getGeneralizationDerived(fromEntityType.getId());

        for (Generalization derivedGeneralization : derivedGeneralizations) {
            List<AssociationEnd> ends = getAssociationEnds(derivedGeneralization.getChild());
            associationEnds.addAll(ends);
        }

        for (AssociationEnd end : associationEnds) {
            if (checkType(toEntityType.getId(), generalizations, end.getType())) {
                return end.getAssociatedAttributeName();
            }
        }
        return null;
    }

    private boolean checkType(final Identifier toType, List<Generalization> baseToTypeGeneralizations, final Identifier endType) {

        if (baseToTypeGeneralizations != null) {
            for (Generalization generalization : baseToTypeGeneralizations) {
                if (generalization.getParent().equals(endType)) {
                    return true;
                }
            }
        }

        return toType.equals(endType);
    }

    public List<String> getAssociatedDatedEntities(final ClassType entityType) {
        List<String> associatedDatedEntities = entityType.getAssociatedDatedCollectionStore();
        if (associatedDatedEntities == null) {
            associatedDatedEntities = new ArrayList<String>();
            List<TaggedValue> taggedValues = entityType.getTaggedValues();
            for (TaggedValue taggedValue : taggedValues) {
                if (modelIndex.getTagDefinition(taggedValue.getTagDefinition()).getName().equals("dataStore.associatedDatedCollection")) {
                    associatedDatedEntities.add(taggedValue.getValue());
                }
            }
        }
        entityType.setAssociatedDatedCollectionStore(associatedDatedEntities);
        return entityType.getAssociatedDatedCollectionStore();
    }
    public String getFilterBeginDateOn(final  ClassType entityType) {
        String date = "";
        List<TaggedValue> taggedValues = entityType.getTaggedValues();
        for (TaggedValue taggedValue : taggedValues) {
            if (modelIndex.getTagDefinition(taggedValue.getTagDefinition()).getName().equals("dataStore.filterBeginDateOn")) {
                date = taggedValue.getValue();
            }
        }
        return date;
    }

    public String getFilterEndDateOn(final  ClassType entityType) {
        String date = "";

        List<TaggedValue> taggedValues = entityType.getTaggedValues();
        for (TaggedValue taggedValue : taggedValues) {
            if (modelIndex.getTagDefinition(taggedValue.getTagDefinition()).getName().equals("dataStore.filterEndDateOn")) {
                date = taggedValue.getValue();
            }
        }
        return date;
    }
}

