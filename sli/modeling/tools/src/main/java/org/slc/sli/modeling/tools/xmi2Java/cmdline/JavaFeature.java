package org.slc.sli.modeling.tools.xmi2Java.cmdline;

/**
 * A Java feature is a feature of a class such as an attribute or an association end.
 */
public final class JavaFeature {

    private final String name;
    private final String type;

    public JavaFeature(final String name, final String type) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (type == null) {
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
