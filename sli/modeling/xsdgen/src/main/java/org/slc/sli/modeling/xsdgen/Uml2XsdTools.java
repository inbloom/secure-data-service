package org.slc.sli.modeling.xsdgen;

final class Uml2XsdTools {

    public static final String camelCase(final String name) {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }
}
