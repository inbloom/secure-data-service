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

package org.slc.sli.modeling.uml;

/**
 * Visitor for the various model elements.
 */
public interface Visitor {
    
    void beginPackage(UmlPackage pkg);
    
    void endPackage(UmlPackage pkg);
    
    void visit(AssociationEnd associationEnd);
    
    void visit(Attribute attribute);
    
    void visit(ClassType classType);
    
    void visit(DataType dataType);
    
    void visit(EnumLiteral enumLiteral);
    
    void visit(EnumType enumType);
    
    void visit(Generalization generalization);
    
    void visit(Model model);
    
    void visit(Multiplicity multiplicity);
    
    void visit(Range range);
    
    void visit(TagDefinition tagDefinition);
    
    void visit(TaggedValue taggedValue);
}
