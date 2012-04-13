package org.slc.sli.modeling.xsd2xmi.linker;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Identifier;

public final class Navigation {
    private final Identifier source;
    private final AssociationEnd target;
    
    public Navigation(final Identifier source, final AssociationEnd target) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (target == null) {
            throw new NullPointerException("target");
        }
        this.source = source;
        this.target = target;
    }
    
    public Identifier getSource() {
        return source;
    }
    
    public AssociationEnd getTarget() {
        return target;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("source: " + source);
        sb.append(", ");
        sb.append("target: " + target);
        sb.append("}");
        return sb.toString();
    }
    
}
