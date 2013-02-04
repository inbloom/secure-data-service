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


package org.slc.sli.modeling.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slc.sli.modeling.xdm.DmNode;

/**
 * Each WADL-defined element can have one or more child <code>doc</code> elements that can be used
 * to document the element.
 *
 * The <code>doc</code> element has mixed content and may contain text and zero or more child
 * elements that form the body of the documentation. It is <em>RECOMMENDED</em> that the child
 * elements be members of the text, list or table modules of XHTML.
 */
public class Documentation {
    private final String title;
    private final String language;
    private final List<DmNode> contents;

    public Documentation(final String title, final String language, final List<DmNode> contents) {
        if (null == contents) {
            throw new IllegalArgumentException("contents");
        }
        this.title = title;
        this.language = language;
        this.contents = Collections.unmodifiableList(new ArrayList<DmNode>(contents));
    }

    /**
     * A short plain text description of the element being documented, the value <em>SHOULD</em> be
     * suitable for use as a title for the contained documentation.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Defines the language for the <code>title</code> attribute value and the contents of the
     * <code>doc</code> element. If an element contains more than one <code>doc</code> element then
     * they <em>MUST</em> have distinct values for their <code>xml:lang</code> attribute.
     */
    public String getLanguage() {
        return language;
    }

    public List<DmNode> getContents() {
        return contents;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("language").append(" : ").append(language);
        sb.append(", ");
        sb.append("title").append(" : ").append(title);
        sb.append(", ");
        sb.append("contents").append(" : ").append(contents);
        sb.append("}");
        return sb.toString();
    }
}
