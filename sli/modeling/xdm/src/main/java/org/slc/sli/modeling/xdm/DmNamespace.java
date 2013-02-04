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


package org.slc.sli.modeling.xdm;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Data model namespace.
 */
public final class DmNamespace implements DmNode {

    private final QName prefix;
    private final String namespace;

    public DmNamespace(final String prefix, final String namespace) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix");
        }
        if (namespace == null) {
            throw new IllegalArgumentException("namespace");
        }
        this.prefix = new QName(prefix);
        this.namespace = namespace;
    }

    @Override
    public QName getName() {
        return prefix;
    }

    @Override
    public String getStringValue() {
        return namespace;
    }

    @Override
    public List<DmNode> getChildAxis() {
        return Collections.emptyList();
    }
}
