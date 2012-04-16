package org.slc.sli.modeling.uml;

/**
 * Visitor for the various model types.
 */
public interface ModelVisitor {
    
    void classType(ClassType clazzType);
    
    void dataType(DataType dataType);
    
    void enumType(EnumType enumType);
    
    void generalization(Generalization generalization);
}
