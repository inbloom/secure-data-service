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
import java.util.Comparator;

/**
 * Compare QName objects
 */
enum QNameComparator implements Comparator<QName> {
    SINGLETON;

    @Override
    public int compare(final QName arg0, final QName arg1) {
        final int nsComp = arg0.getNamespaceURI().compareTo(
                arg1.getNamespaceURI());
        if (nsComp == 0) {
            return arg0.getLocalPart().compareTo(arg1.getLocalPart());
        } else {
            return nsComp;
        }
    }

}
