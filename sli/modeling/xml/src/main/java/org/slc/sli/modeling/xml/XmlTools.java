package org.slc.sli.modeling.xml;

/**
 * eXtensible Markup Language Utilities and Tools.
 */
public final class XmlTools {
    
    private static final String collapseSpace(final String value) {
        final String temp = value.replace("  ", " ");
        if (temp.equals(value)) {
            return value;
        } else {
            return collapseSpace(temp);
        }
    }
    
    /**
     * Whitespace collapsing consistent with xs:token.
     * 
     * Collapsing whitespace involves changing CR, LF and NL to a single space followed by the
     * replacement of double spaces by a single space. Finally, the string is trimmed of leading and
     * trailing whitespace.
     * 
     * @param value
     *            The string to be collapsed.
     * @return The collapsed string.
     */
    public static final String collapseWhitespace(final String value) {
        return collapseSpace(value.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim());
    }
}
