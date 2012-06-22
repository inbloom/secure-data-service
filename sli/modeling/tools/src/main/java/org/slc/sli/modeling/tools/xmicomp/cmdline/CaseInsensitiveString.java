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


package org.slc.sli.modeling.tools.xmicomp.cmdline;

public final class CaseInsensitiveString {
    
    private final String value;
    
    public CaseInsensitiveString(final String value) {
        this.value = value.toLowerCase();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CaseInsensitiveString) {
            final CaseInsensitiveString other = (CaseInsensitiveString) obj;
            return value.equals(other.value);
        } else {
            return false;
        }
    }
    
    public boolean endsWith(final CaseInsensitiveString suffix) {
        return value.endsWith(suffix.value);
    }
    
    public int length() {
        return value.length();
    }
    
    public CaseInsensitiveString substring(final int beginIndex, int endIndex) {
        return new CaseInsensitiveString(value.substring(beginIndex, endIndex));
    }
    
    public CaseInsensitiveString concat(final CaseInsensitiveString str) {
        return new CaseInsensitiveString(value.concat(str.value));
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
