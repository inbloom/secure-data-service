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
package org.slc.sli.modeling.uml;

import java.util.UUID;

/**
 * Encapsulation of the identifier implementation and for type safety.
 */
public final class Identifier {
    
    public static final Identifier fromString(final String id) {
        return new Identifier(id);
    }
    
    public static final Identifier random() {
        return new Identifier(UUID.randomUUID().toString());
    }
    
    private final String id;
    
    private Identifier(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        this.id = id;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Identifier) {
            final Identifier other = (Identifier) obj;
            return id.equals(other.id);
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return id.toString();
    }
}
