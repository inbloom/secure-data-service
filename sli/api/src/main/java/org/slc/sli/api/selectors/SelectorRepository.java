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

package org.slc.sli.api.selectors;

import org.slc.sli.api.selectors.model.SemanticSelector;

/**
 * Returns a selector appropriate to the logical model for the given type.
 *
 *
 * @author kmyers
 *
 */
public interface SelectorRepository {

    /**
     * Returns the associated default selector, or null if not available.
     */
    public SemanticSelector getSelector(String type);
}
