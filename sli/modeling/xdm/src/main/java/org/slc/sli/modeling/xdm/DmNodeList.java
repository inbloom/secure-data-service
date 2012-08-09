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


package org.slc.sli.modeling.xdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DmNodeList implements DmNodeSequence {

    @SuppressWarnings("unused")
    private final List<DmNode> nodes;

    public DmNodeList(final List<? extends DmNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes");
        }
        this.nodes = Collections.unmodifiableList(new ArrayList<DmNode>(nodes));
    }
}
