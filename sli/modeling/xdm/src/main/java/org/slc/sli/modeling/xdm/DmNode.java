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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Data model node.
 */
public interface DmNode extends DmItem, DmNodeSequence {

    /**
     * The dm:name property.
     */
    QName getName();

    /**
     * The dm:child-axis property.
     */
    List<DmNode> getChildAxis();

    /**
     * Returns the dm:string-value property.
     */
    String getStringValue();
}
