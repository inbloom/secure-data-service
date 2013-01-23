/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.web.util;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;

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
        if (htmlEscapes == null) {
            return null;
        }
        
        return htmlEscapes.clone();
    }

    @Override
    public SerializableString getEscapeSequence(int i) {
        return null;
    }
}
