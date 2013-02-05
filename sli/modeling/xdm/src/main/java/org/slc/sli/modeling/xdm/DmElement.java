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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * Data model element.
 */
public final class DmElement implements DmNode {

    private static final List<DmNode> EMPTY_CHILD_AXIS = Collections.emptyList();
    private final List<DmNode> children;
    private final QName name;

    public DmElement(final QName name) {
        this(name, EMPTY_CHILD_AXIS);
    }

    public DmElement(final QName name, final List<? extends DmNode> childAxis) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        if (childAxis == null) {
            throw new IllegalArgumentException("childAxis");
        }
        this.name = name;
        this.children = Collections.unmodifiableList(new ArrayList<DmNode>(childAxis));
    }

    @Override
    public List<DmNode> getChildAxis() {
        return children;
    }

    @Override
    public QName getName() {
        return name;
    }

    @Override
    public String getStringValue() {
        throw new UnsupportedOperationException("getStringValue() is not supported by DmElement");
    }
}
