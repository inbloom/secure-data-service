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

import javax.xml.namespace.QName;

/**
 * @author dholmes
 */
public class Param extends WadlElement {
    private final String name;
    private final ParamStyle style;
    private final String id;
    private final QName type;
    private final String defaultValue;
    private final boolean required;
    private final boolean repeating;
    private final String fixed;
    private final String path;
    private final List<Option> options;
    private final Link link;

    public Param(final String name, final ParamStyle style, final String id, final QName type,
            final String defaultValue, final boolean required, final boolean repeating, final String fixed,
            final String path, final List<Documentation> doc, final List<Option> options, final Link link) {
        super(doc);
        if (null == name) {
            throw new IllegalArgumentException("name");
        }
        if (null == style) {
            throw new IllegalArgumentException("style");
        }
        if (null == options) {
            throw new IllegalArgumentException("options");
        }
        this.name = name;
        this.style = style;
        this.id = id;
        this.type = type;
        this.defaultValue = defaultValue;
        this.required = required;
        this.repeating = repeating;
        this.fixed = fixed;
        this.path = path;
        this.options = Collections.unmodifiableList(new ArrayList<Option>(options));
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public ParamStyle getStyle() {
        return style;
    }

    public String getId() {
        return id;
    }

    public QName getType() {
        return type;
    }

    public String getDefault() {
        return defaultValue;
    }

    public boolean getRequired() {
        return required;
    }

    public boolean getRepeating() {
        return repeating;
    }

    public String getFixed() {
        return fixed;
    }

    public String getPath() {
        return path;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Link getLink() {
        return link;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("name").append(" : ").append(name);
        sb.append(", ");
        sb.append("style").append(" : ").append(style);
        sb.append(", ");
        sb.append("id").append(" : ").append(id);
        sb.append(", ");
        sb.append("type").append(" : ").append(type);
        sb.append(", ");
        sb.append("default").append(" : ").append(defaultValue);
        sb.append(", ");
        sb.append("required").append(" : ").append(required);
        sb.append(", ");
        sb.append("repeating").append(" : ").append(repeating);
        sb.append(", ");
        sb.append("fixed").append(" : ").append(fixed);
        sb.append(", ");
        sb.append("path").append(" : ").append(path);
        sb.append(", ");
        sb.append("options").append(" : ").append(options);
        sb.append(", ");
        sb.append("link").append(" : ").append(link);
        sb.append(", ");
        sb.append("doc").append(" : ").append(getDocumentation());
        sb.append("}");
        return sb.toString();
    }
}
