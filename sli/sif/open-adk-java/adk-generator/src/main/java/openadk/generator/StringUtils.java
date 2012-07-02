//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

public class StringUtils {

    /**
     * Escapes an XML string by replacing the characters shown below with their equivalent entity references as defined
     * by the XML specification.
     * <p>
     * 
     * <table>
     * <tr>
     * <td>Character</td>
     * <td>Entity Reference</td>
     * </tr>
     * <tr>
     * <td>&lt;</td>
     * <td>&amp;lt;</td>
     * </tr>
     * <tr>
     * <td>&gt;</td>
     * <td>&amp;gt;</td>
     * </tr>
     * <tr>
     * <td>&amp;</td>
     * <td>&amp;amp;</td>
     * </tr>
     * <tr>
     * <td>&apos;</td>
     * <td>&amp;apos;</td>
     * </tr>
     * <tr>
     * <td>&quot;</td>
     * <td>&amp;quot;</td>
     * </tr>
     * </table>
     * 
     * @param str
     *            The source string
     * @return The escaped string
     */
    public static String encodeXML(String str) {
        if (str == null)
            return null;

        StringBuilder b = new StringBuilder();

        int cnt = str.length();
        for (int i = 0; i < cnt; i++) {
            char c = str.charAt(i);

            switch (c) {
            case '<':
                b.append("&lt;");
                break;
            case '>':
                b.append("&gt;");
                break;
            case '\'':
                b.append("&apos;");
                break;
            case '"':
                b.append("&quot;");
                break;
            case '&':
                b.append("&amp;");
                break;
            default:
                b.append((char) c);
            }
        }

        return b.toString();
    }

}
