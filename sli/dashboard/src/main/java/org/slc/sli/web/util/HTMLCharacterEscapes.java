package org.slc.sli.web.util;

import org.codehaus.jackson.SerializableString;
import org.codehaus.jackson.io.CharacterEscapes;

/**
 *
 * @author ccheng
 *
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

    private final int[] htmlEscapes;
    private final String htmlChars = "/<>\"\'&";

    public HTMLCharacterEscapes() {
        htmlEscapes = standardAsciiEscapesForJSON();
        for (int i = 0; i < htmlChars.length(); i++) {
            char c = htmlChars.charAt(i);
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
