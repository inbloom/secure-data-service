package org.slc.sli.modeling.xmicomp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XmiComparison {
    
    private final XmiDefinition lhsRef;
    private final XmiDefinition rhsRef;
    private final List<XmiMapping> mappings;
    
    public XmiComparison(final XmiDefinition lhsRef, final XmiDefinition rhsRef, final List<XmiMapping> mappings) {
        if (lhsRef == null) {
            throw new NullPointerException("lhsRef");
        }
        if (rhsRef == null) {
            throw new NullPointerException("rhsRef");
        }
        this.lhsRef = lhsRef;
        this.rhsRef = rhsRef;
        this.mappings = Collections.unmodifiableList(new ArrayList<XmiMapping>(mappings));
    }
    
    public XmiDefinition getLhsDef() {
        return lhsRef;
    }
    
    public XmiDefinition getRhsDef() {
        return rhsRef;
    }
    
    public List<XmiMapping> getMappings() {
        return mappings;
    }
}
