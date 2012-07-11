package org.slc.sli.modeling.xmigen;

public final class Xsd2UmlHelper {
    
    /**
     * TODO: This should be driven from some sort of external configuration.
     */
    public static final String pluralize(final String typeName) {
        if (typeName == null) {
            throw new NullPointerException("typeName");
        }
        if (typeName.endsWith("y")) {
            return typeName.substring(0, typeName.length() - 1).concat("ies");
        } else if (typeName.endsWith("s")) {
            return typeName;
        } else if (typeName.equalsIgnoreCase("staff")) {
            return typeName;
        } else {
            return typeName.concat("s");
        }
    }
    
    public static String replaceAllIgnoreCase(final String strval, final String target, final String replacement) {
        return strval.replaceAll("(?i)".concat(target), replacement);
    }
    
    private static final String[] ACRONYMS = { "CIP", "CTE", "GPA", "ID", "IEP", "LEA", "URI", "URL", "URN" };
    
    public static final String camelCase(final String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        for (final String acronym : ACRONYMS) {
            if (acronym.equals(text)) {
                return text;
            }
        }
        for (final String acronym : ACRONYMS) {
            if (text.startsWith(acronym)) {
                return acronym.toLowerCase().concat(text.substring(acronym.length()));
            }
        }
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
}
