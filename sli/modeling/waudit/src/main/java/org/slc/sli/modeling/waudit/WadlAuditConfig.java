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

package org.slc.sli.modeling.waudit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Configuration information for fixing the WADL.
 */
public final class WadlAuditConfig {

    private final String prefix;
    private final String namespaceURI;
    private final ModelIndex model;
    private final Map<String, QName> elementNames;

    public WadlAuditConfig(final String prefix, final String namespaceURI, final ModelIndex model,
            final Map<String, QName> elementNames) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix");
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI");
        }
        if (model == null) {
            throw new IllegalArgumentException("model");
        }
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
        this.model = model;
        this.elementNames = Collections.unmodifiableMap(new HashMap<String, QName>(elementNames));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public ModelIndex getModel() {
        return model;
    }

    public Map<String, QName> getElementNameMap() {
        return elementNames;
    }
}
