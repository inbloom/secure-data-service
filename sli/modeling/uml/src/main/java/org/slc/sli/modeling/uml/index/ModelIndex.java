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

package org.slc.sli.modeling.uml.index;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.Visitor;

/**
 * Provides the ability to lookup references.
 * 
 * The purpose is to enable the parse model to be used directly.
 */
public interface ModelIndex {
    
    /**
     * Returns a list of association ends for the type reference.
     * 
     * @param type
     *            The type reference.
     * @return The type associations.
     */
    List<AssociationEnd> getAssociationEnds(Identifier type);
    
    Map<String, ClassType> getClassTypes();
    
    Map<QName, DataType> getDataTypes();
    
    Iterable<EnumType> getEnumTypes();
    
    /**
     * Returns a list of generalizations for the derived reference.
     * 
     * @param derived
     *            The derived reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationBase(Identifier derived);
    
    /**
     * Returns a list of generalizations for the derived reference.
     * 
     * @param base
     *            The base type reference.
     * @return The base generalizations.
     */
    List<Generalization> getGeneralizationDerived(Identifier base);
    
    /**
     * Returns the scope in which the type exists.
     * 
     * @param type
     *            The type for which the scope should be found.
     */
    String getNamespaceURI(Type type);
    
    /**
     * Returns the tag definition for the specified reference.
     * 
     * @param reference
     *            The reference to the tag definition.
     * @return the tag definition required.
     */
    TagDefinition getTagDefinition(Identifier reference);
    
    /**
     * Returns the tag definition for the specified name.
     * 
     * @param name
     *            The name of the tag definition.
     * @return the tag definition required.
     */
    TagDefinition getTagDefinition(QName name);
    
    /**
     * Returns the type specified by the reference.
     * 
     * @param reference
     *            The reference to the type.
     * @return the type required.
     */
    Type getType(Identifier reference);
    
    void lookup(Identifier id, Visitor visitor);
    
    @Deprecated
    Set<ModelElement> lookupByName(QName name);
    
    Set<ModelElement> whereUsed(Identifier id);
}
