package org.slc.sli.modeling.xmicomp;

public final class XmiFeature {
    private final String name;
    private final boolean exists;
    private final String className;
    private final boolean classExists;
    
    public XmiFeature(final String name, final boolean exists, final String className, final boolean classExists) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == className) {
            throw new NullPointerException("className");
        }
        this.name = name;
        this.exists = exists;
        this.className = className;
        this.classExists = classExists;
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
    public String getOwnerName() {
        return className;
    }
    
    public boolean ownerExists() {
        return classExists;
    }
    
    public boolean exists() {
        return exists;
    }
    
    @Override
    public String toString() {
        return String.format("{name : %s, exists : %s, className : %s, classExists : %s}", name, exists, className,
                classExists);
    }
}