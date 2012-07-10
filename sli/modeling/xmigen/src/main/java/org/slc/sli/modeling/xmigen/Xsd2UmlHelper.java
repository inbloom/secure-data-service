package org.slc.sli.modeling.xmigen;

public final class Xsd2UmlHelper {
    
    public static String replaceAllIgnoreCase(final String strval, final String target, final String replacement) {
        return strval.replaceAll("(?i)".concat(target), replacement);
    }
}
