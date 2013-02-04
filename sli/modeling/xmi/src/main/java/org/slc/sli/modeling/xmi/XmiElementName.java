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

package org.slc.sli.modeling.xmi;

/**
 * Symbolic constants used for element names in XMI.
 */
public enum XmiElementName {
    /**
     * 
     */
    ASSOCIATION("Association"),
    /**
     * 
     */
    ASSOCIATION_CLASS("AssociationClass"),
    /**
     * 
     */
    ASSOCIATION_DOT_CONNECTION("Association.connection"),
    /**
     * 
     */
    ASSOCIATION_END("AssociationEnd"),
    /**
     * 
     */
    ASSOCIATION_END_DOT_MULTIPLICITY("AssociationEnd.multiplicity"),
    /**
     * 
     */
    ASSOCIATION_END_DOT_PARTICIPANT("AssociationEnd.participant"),
    /**
     * 
     */
    ATTRIBUTE("Attribute"),
    /**
     * 
     */
    OPERATION("Operation"),
    /**
     * 
     */
    CLASS("Class"),
    /**
     * 
     */
    CLASSIFIER_DOT_FEATURE("Classifier.feature"),
    /**
     * 
     */
    COMMENT("Comment"),
    /**
     * 
     */
    DATA_TYPE("DataType"),
    /**
     * 
     */
    ENUMERATION("Enumeration"),
    /**
     * 
     */
    ENUMERATION_LITERAL("EnumerationLiteral"),
    /**
     * The enumeration literal group has the localName "Enumeration.literal".
     */
    ENUMERATION_LITERAL_GROUP("Enumeration.literal"),
    /**
     * 
     */
    GENERALIZATION("Generalization"),
    /**
     * 
     */
    GENERALIZATION_DOT_CHILD("Generalization.child"),
    /**
     * 
     */
    GENERALIZATION_DOT_PARENT("Generalization.parent"),
    /**
     * 
     */
    MODEL("Model"),
    /**
     * 
     */
    MODEL_ELEMENT_DOT_TAGGED_VALUE("ModelElement.taggedValue"),
    /**
     * 
     */
    MULTIPLICITY("Multiplicity"),
    /**
     * 
     */
    MULTIPLICITY_DOT_RANGE("Multiplicity.range"),
    /**
     * 
     */
    MULTIPLICITY_RANGE("MultiplicityRange"),
    /**
     * 
     */
    NAMESPACE_DOT_OWNED_ELEMENT("Namespace.ownedElement"),
    /**
     * 
     */
    PACKAGE("Package"),
    /**
     * 
     */
    STRUCTURAL_FEATURE_DOT_MULTIPLICITY("StructuralFeature.multiplicity"),
    /**
     * 
     */
    STRUCTURAL_FEATURE_DOT_TYPE("StructuralFeature.type"),
    /**
     * 
     */
    TAG_DEFINITION("TagDefinition"),
    /**
     * 
     */
    TAG_DEFINITION_DOT_MULTIPLICITY("TagDefinition.multiplicity"),
    /**
     * 
     */
    TAGGED_VALUE("TaggedValue"),
    /**
     * 
     */
    TAGGED_VALUE_DOT_DATA_VALUE("TaggedValue.dataValue"),
    /**
     * 
     */
    TAGGED_VALUE_DOT_TYPE("TaggedValue.type");
    
    private final String localName;
    
    XmiElementName(final String localName) {
        if (localName == null) {
            throw new IllegalArgumentException("localName");
        }
        this.localName = localName;
    }
    
    public String getLocalName() {
        return localName;
    }
    
    public static final XmiElementName getElementName(final String localName) {
        for (final XmiElementName name : values()) {
            if (name.localName.equals(localName)) {
                return name;
            }
        }
        return null;
    }
}
