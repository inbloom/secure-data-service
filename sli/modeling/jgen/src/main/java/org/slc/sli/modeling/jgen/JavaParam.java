package org.slc.sli.modeling.jgen;

public final class JavaParam {

    private final String name;
    private final String type;
    private final boolean isFinal;

    public JavaParam(final String name, final String type, final boolean isFinal) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
