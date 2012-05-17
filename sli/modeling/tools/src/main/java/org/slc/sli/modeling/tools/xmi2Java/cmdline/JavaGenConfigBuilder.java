package org.slc.sli.modeling.tools.xmi2Java.cmdline;

/**
 * Builder pattern for a {@link JavaGenConfig}
 *
 * <p/>
 * The defaults are:
 * <table border = '1'>
 * <tr>
 * <th>property</th>
 * <th>value</th>
 * </tr>
 * <tr>
 * <td>useDataTypeBase</td>
 * <td>false</td>
 * </tr>
 * </table>
 *
 */
public final class JavaGenConfigBuilder {

    private boolean useDataTypeBase = false;

    public JavaGenConfigBuilder() {

    }

    public JavaGenConfigBuilder useDataTypeBase(final boolean useDataTypeBase) {
        this.useDataTypeBase = useDataTypeBase;
        return this;
    }

    public JavaGenConfig build() {
        return new JavaGenConfig(useDataTypeBase);
    }
}
