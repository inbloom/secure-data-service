package org.slc.sli.modeling.tools.xmicomp.cmdline;

public final class XmiDefinition {
    private final String name;
    private final String version;
    
    public XmiDefinition(final String name, final String version) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == version) {
            throw new NullPointerException("version");
        }
        this.name = name;
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
}