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
 * Data model processing instruction
 */
public final class DmProcessingInstruction implements DmNode {

    private final String data;
    private final QName target;

    public DmProcessingInstruction(final String target, final String data) {
        if (target == null) {
            throw new IllegalArgumentException("target");
        }
        if (data == null) {
            throw new IllegalArgumentException("data");
        }
        this.target = new QName(target);
        this.data = data;
    }

    @Override
    public List<DmNode> getChildAxis() {
        return Collections.emptyList();
    }

    @Override
    public QName getName() {
        return target;
    }

    @Override
    public String getStringValue() {
        return data;
    }
}
