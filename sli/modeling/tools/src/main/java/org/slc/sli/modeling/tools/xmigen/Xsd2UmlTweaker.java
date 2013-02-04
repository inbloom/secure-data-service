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

package org.slc.sli.modeling.tools.xmigen;

import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Converts some UML Class types into UML AssociationClass types if they are Associations.
 */
final class Xsd2UmlTweaker {
    
    public static Model tweak(final Model model, final Xsd2UmlHostedPlugin plugin) {
        
        final ModelIndex indexedModel = new DefaultModelIndex(model);
        
        final Xsd2UmlTweakerVisitor tweaker = new Xsd2UmlTweakerVisitor(indexedModel);
        
        model.accept(tweaker);
        
        return new Model(Identifier.random(), model.getName(), model.getTaggedValues(), tweaker.getOwnedElements());
    }
    
}
