package org.slc.sli.modeling.xmi.reader;

import org.slc.sli.modeling.uml.AssociationEnd;

/**
 * This class only exists temporarily during parsing.
 */
final class XmiAssociationConnection {
    
    private final AssociationEnd lhs;
    private final AssociationEnd rhs;
    
    public XmiAssociationConnection(final AssociationEnd lhs, final AssociationEnd rhs) {
        if (lhs == null) {
            throw new NullPointerException("lhs");
        }
        if (rhs == null) {
            throw new NullPointerException("rhs");
        }
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public AssociationEnd getLHS() {
        return lhs;
    }
    
    public AssociationEnd getRHS() {
        return rhs;
    }
}
