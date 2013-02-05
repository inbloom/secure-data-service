/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
