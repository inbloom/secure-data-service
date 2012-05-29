package org.slc.sli.modeling.jgen;

public final class JavaGenConfig {

    private boolean useDataTypeBase;

    JavaGenConfig(final boolean useDataTypeBase) {
        this.useDataTypeBase = useDataTypeBase;
    }

    public boolean useDataTypeBase() {
        return useDataTypeBase;
    }
}
