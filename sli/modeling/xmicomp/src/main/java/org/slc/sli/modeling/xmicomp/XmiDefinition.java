package org.slc.sli.modeling.xmicomp;

public final class XmiDefinition {
    private final String name;
    private final String version;
    private final String file;
    
    public XmiDefinition(final String name, final String version, final String file) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == version) {
            throw new NullPointerException("version");
        }
        if (null == file) {
            throw new NullPointerException("file");
        }
        this.name = name;
        this.version = version;
        this.file = file;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getFile() {
        return file;
    }
}
