package org.slc.sli.web.util;

import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.io.CharacterEscapes;

/**
 * Implementing CharaterEscapes and customizing HTML character escaping
 * used by Jackson JSON generator.
 *
 * @author ccheng
 *
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

    private int[] htmlEscapes;
    private static final String HTML_CHARS = "/<>\"\'&";

    public HTMLCharacterEscapes() {
        htmlEscapes = standardAsciiEscapesForJSON();
        for (int i = 0; i < HTML_CHARS.length(); i++) {
            char c = HTML_CHARS.charAt(i);
            htmlEscapes[c] = CharacterEscapes.ESCAPE_STANDARD;
        }
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return htmlEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int i) {
        return null;
    }
}
