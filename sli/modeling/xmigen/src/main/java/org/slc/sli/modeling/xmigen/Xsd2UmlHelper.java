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
    
    public static final String titleCase(final String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
    
    /**
     * Compute a sensible name for the reverse (physical) navigation direction.
     */
    public static final String makeAssociationEndName(final String sourceTypeName, final String sourceEndName,
            final int degeneracy, final String targetTypeName) {
        if (degeneracy > 1) {
            // FIXME: It's rather crude to use a type name.
            final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
            // There is more than one pathway so make sure that the name is unique.
            return sourceEndName.concat(Xsd2UmlHelper.titleCase(targetName));
        } else {
            if (targetTypeName.equals(sourceTypeName)) {
                // Reference to self. Avoid absurdity. See AssessmentFamily.
                if ("parent".concat(sourceTypeName).equals(sourceEndName)) {
                    return "child".concat(Xsd2UmlHelper.pluralize(targetTypeName));
                } else {
                    // FIXME: It's rather crude to use a type name.
                    final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
                    return Xsd2UmlHelper.camelCase(targetName);
                }
            } else {
                // FIXME: It's rather crude to use a type name.
                final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
                if (targetName.toLowerCase().contains(sourceTypeName.toLowerCase())) {
                    return Xsd2UmlHelper.camelCase(Xsd2UmlHelper.replaceAllIgnoreCase(targetName, sourceTypeName, ""));
                } else {
                    return Xsd2UmlHelper.camelCase(targetName);
                }
            }
        }
    }
    
}
