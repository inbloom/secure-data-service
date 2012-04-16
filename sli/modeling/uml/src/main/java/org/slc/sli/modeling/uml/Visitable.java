package org.slc.sli.modeling.uml;

public interface Visitable {
    
    void accept(Visitor visitor);
    
}
