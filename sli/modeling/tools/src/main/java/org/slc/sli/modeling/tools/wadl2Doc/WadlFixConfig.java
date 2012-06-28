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


package org.slc.sli.modeling.tools.wadl2Doc;

/**
 * Configuration information for fixing the WADL.
 */
public final class WadlFixConfig {

    private final String prefix;
    private final String namespaceURI;

    public WadlFixConfig(final String prefix, final String namespaceURI) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }
}
