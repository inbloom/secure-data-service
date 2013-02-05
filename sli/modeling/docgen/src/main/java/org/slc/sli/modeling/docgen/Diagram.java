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


package org.slc.sli.modeling.docgen;

/**
 * Models a UML diagram.
 */
public final class Diagram {
    private final String title;
    private final String source;
    private final String prolog;
    private final String epilog;

    public Diagram(final String title, final String source, final String prolog, final String epilog) {
        if (title == null) {
            throw new IllegalArgumentException("title");
        }
        if (source == null) {
            throw new IllegalArgumentException("source");
        }
        if (prolog == null) {
            throw new IllegalArgumentException("prolog");
        }
        if (epilog == null) {
            throw new IllegalArgumentException("epilog");
        }
        this.title = title;
        this.source = source;
        this.prolog = prolog;
        this.epilog = epilog;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("title : \"").append(title).append("\"");
        sb.append(", ");
        sb.append("source : \"").append(source).append("\"");
        sb.append(", ");
        sb.append("prolog : \"").append(prolog).append("\"");
        sb.append(", ");
        sb.append("epilog : \"").append(epilog).append("\"");
        sb.append("}");
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getProlog() {
        return prolog;
    }

    public String getEpilog() {
        return epilog;
    }
}
