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

/**
 * A feature in an XMI document.
 */
public final class XmiFeature {
    private final String name;
    private final boolean exists;
    private final String className;
    private final boolean classExists;
    
    public XmiFeature(final String name, final boolean exists, final String className, final boolean classExists) {
        if (null == name) {
            throw new IllegalArgumentException("name");
        }
        if (null == className) {
            throw new IllegalArgumentException("className");
        }
        this.name = name;
        this.exists = exists;
        this.className = className;
        this.classExists = classExists;
    }
    
    /**
     * The name of the feature.
     */
    public String getName() {
        return name;
    }
    
    /**
     * The type that is the owner of the feature.
     */
    public String getOwnerName() {
        return className;
    }
    
    public boolean ownerExists() {
        return classExists;
    }
    
    public boolean exists() {
        return exists;
    }
    
    @Override
    public String toString() {
        return String.format("{name : %s, exists : %s, className : %s, classExists : %s}", name, exists, className,
                classExists);
    }
}
