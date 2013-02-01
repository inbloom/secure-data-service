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

package org.slc.sli.modeling.xmi.comp;

import javax.xml.namespace.QName;

/**
 * A case-insensitive feature name expressly for matching and sorting purposes.
 */
public final class CaseInsensitiveQName implements Comparable<CaseInsensitiveQName> {
    
    private final QName name;
    
    public CaseInsensitiveQName(final String type, final String feature) {
        name = new QName(type.toString().toLowerCase(), feature.toString().toLowerCase());
    }
    
    @Override
    public int compareTo(final CaseInsensitiveQName other) {
        return QNameComparator.SINGLETON.compare(name, other.name);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveQName) {
            final CaseInsensitiveQName other = (CaseInsensitiveQName) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    public String getLocalPart() {
        return name.getLocalPart();
    }
    
    public String getNamespaceURI() {
        return name.getNamespaceURI();
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return name.toString();
    }
}
