package org.slc.sli.modeling.xmicomp;

public class XmiMappingModel {
    private final String name;
    private final String version;
    private final String xmi;

    public XmiMappingModel(final String name, final String version, final String xmi) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == version) {
            throw new NullPointerException("version");
        }
        if (null == xmi) {
            throw new NullPointerException("xmi");
        }
        this.name = name;
        this.version = version;
        this.xmi = xmi;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return version;
    }

    public String getXmi() {
        return xmi;
    }
}
