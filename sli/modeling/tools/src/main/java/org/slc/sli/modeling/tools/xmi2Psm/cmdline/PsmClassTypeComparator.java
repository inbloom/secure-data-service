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


package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import java.util.Comparator;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.uml.Type;

/**
 * Compare PSM documents.
 */
enum PsmDocumentComparator implements Comparator<PsmDocument<Type>> {

    SINGLETON;

    @Override
    public int compare(final PsmDocument<Type> arg0, final PsmDocument<Type> arg1) {
        return arg0.getType().getName().compareTo(arg1.getType().getName());
    }
}
