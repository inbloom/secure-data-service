package org.slc.sli.modeling.tools.xmicomp.cmdline;

public final class XmiFeature {
    private final String name;
    private final String type;
    
    public XmiFeature(final String name, final String type) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == type) {
            throw new NullPointerException("type");
        }
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
}