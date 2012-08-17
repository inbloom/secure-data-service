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
package org.slc.sli.modeling.uml.utils;

import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;

import java.util.Collections;
import java.util.List;

/**
 * Utilities for unit tests
 * @author chung
 */
public class TestUtils {
    public static final Multiplicity zeroToOne = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
    public static final Multiplicity oneToMany = new Multiplicity(new Range(Occurs.ONE, Occurs.UNBOUNDED));
    public static final List<TaggedValue> EMPTY_TAGGED_VALUES = Collections.emptyList();
}
