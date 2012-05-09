package org.slc.sli.modeling.tools.xmi2Java.cmdline;

public class TypeHelper {

    public static final String getImplementationTypeName(final String typeName) {
        // TODO: We should define some simple types to mirror XML schema data-types.
        if ("string".equals(typeName)) {
            return "String";
        } else if ("boolean".equals(typeName)) {
            return "Boolean";
        } else if ("date".equals(typeName)) {
            return "String";
        } else if ("Currency".equals(typeName)) {
            return "BigDecimal";
        } else if ("decimal".equals(typeName)) {
            return "BigDecimal";
        } else if ("int".equals(typeName)) {
            return "Integer";
        } else if ("integer".equals(typeName)) {
            return "BigInteger";
        } else if ("percent".equals(typeName)) {
            return "Integer";
        } else if ("text".equals(typeName)) {
            return "String";
        } else if ("time".equals(typeName)) {
            return "String";
        } else {
            return typeName;
        }
    }

}
