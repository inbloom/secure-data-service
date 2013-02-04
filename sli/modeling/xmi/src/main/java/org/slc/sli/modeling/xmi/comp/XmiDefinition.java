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
 * Xmi definition.
 */
public final class XmiDefinition {
    private final String name;
    private final String version;
    private final String file;
    
    public XmiDefinition(final String name, final String version, final String file) {
        if (null == name) {
            throw new IllegalArgumentException("name");
        }
        if (null == version) {
            throw new IllegalArgumentException("version");
        }
        if (null == file) {
            throw new IllegalArgumentException("file");
        }
        this.name = name;
        this.version = version;
        this.file = file;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getFile() {
        return file;
    }
}
