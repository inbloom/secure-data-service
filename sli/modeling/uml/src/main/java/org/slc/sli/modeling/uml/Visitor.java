package org.slc.sli.modeling.uml;

/**
 * Visitor for the various model elements.
 */
public interface Visitor {
    
    void visit(Association association);
    
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
    
    void visit(TaggedValue taggedValue);
    
    void visit(UmlPackage pkg);
    
    void visit(TagDefinition tagDefinition);
}
