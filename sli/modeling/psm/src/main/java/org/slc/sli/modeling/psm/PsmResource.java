package org.slc.sli.modeling.psm;

public final class PsmResource {
    private final String name;

    public PsmResource(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
