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

package org.slc.sli.api.selectors.doc;


import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;

/**
 * Visitor for building queries
 *
 * @author srupasinghe
 *
 */
public interface SelectorQueryVisitor {

    public SelectorQuery visit(SemanticSelector semanticSelector);

    public SelectorQuery visit(BooleanSelectorElement booleanSelectorElement);

    public SelectorQuery visit(ComplexSelectorElement complexSelectorElement);

    public SelectorQuery visit(IncludeAllSelectorElement includeAllSelectorElement);

    public SelectorQuery visit(IncludeXSDSelectorElement includeXSDSelectorElement);

}
