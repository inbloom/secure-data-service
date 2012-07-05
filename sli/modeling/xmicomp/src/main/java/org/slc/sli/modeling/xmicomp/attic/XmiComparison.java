package org.slc.sli.modeling.xmicomp.attic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slc.sli.modeling.xmicomp.XmiMapping;

public final class XmiComparison {
    private final XmiDefinition lhsDefinition;
    private final XmiDefinition rhsDefinition;
    private final List<XmiMapping> mappings;
    
    public XmiComparison(final XmiDefinition lhsDefinition, final XmiDefinition rhsDefinition,
            final List<XmiMapping> mappings) {
        if (null == lhsDefinition) {
            throw new NullPointerException("lhsDefinition");
        }
        if (null == rhsDefinition) {
            throw new NullPointerException("rhsDefinition");
        }
        if (null == mappings) {
            throw new NullPointerException("mappings");
        }
        this.lhsDefinition = lhsDefinition;
        this.rhsDefinition = rhsDefinition;
        this.mappings = Collections.unmodifiableList(new ArrayList<XmiMapping>(mappings));
    }
    
    public XmiDefinition getLhsDefinition() {
        return lhsDefinition;
    }
    
    public XmiDefinition getRhsDefinition() {
        return rhsDefinition;
    }
    
    public List<XmiMapping> getMappings() {
        return mappings;
    }
}