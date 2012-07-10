package org.slc.sli.modeling.xmicomp;

public final class XmiFeature {
    /**
     * The class that the feature belongs to.
     */
    private final String type;
    /**
     * The name of the feature.
     */
    private final String name;
    /**
     * Whether the feature exists.
     */
    private final boolean defined;
    
    public XmiFeature(final String featureName, final String type, final boolean defined) {
        if (null == featureName) {
            throw new NullPointerException("name");
        }
        if (null == type) {
            throw new NullPointerException("type");
        }
        this.name = featureName;
        this.type = type;
        this.defined = defined;
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
    
    /**
     * Whether the feature exists.
     */
    public boolean isDefined() {
        return defined;
    }
    
    @Override
    public String toString() {
        return String.format("{name : %s, type : %s, defined : %s}", name, type, defined);
    }
}