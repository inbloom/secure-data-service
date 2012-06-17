package org.slc.sli.modeling.jgen;

public final class JavaParam {

    private final String name;
    private final JavaType type;
    private final boolean isFinal;

    public JavaParam(final String name, final JavaType type, final boolean isFinal) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public JavaType getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
