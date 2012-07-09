package org.slc.sli.modeling.xmicomp;

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
    
    /**
     * The name of the feature.
     */
    public String getName() {
        return name;
    }
    
    /**
     * The type that is the owner of the feature.
     */
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return String.format("{name : %s, type : %s}", name, type);
    }
}