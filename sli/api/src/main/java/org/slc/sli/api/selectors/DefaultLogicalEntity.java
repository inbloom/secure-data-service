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

package org.slc.sli.api.selectors;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.selectors.doc.Constraint;
import org.slc.sli.api.selectors.doc.SelectorDocument;
import org.slc.sli.api.selectors.doc.SelectorQueryEngine;
import org.slc.sli.api.selectors.doc.SelectorQueryPlan;
import org.slc.sli.api.selectors.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 */
@Component
public class DefaultLogicalEntity implements LogicalEntity {

    @Autowired
    private ModelProvider provider;

    @Autowired
    private SelectorSemanticModel selectorSemanticModel;

    @Autowired
    private SelectorQueryEngine selectorQueryEngine;

    @Autowired
    private SelectorDocument selectorDocument;

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    public List<EntityBody> createEntities(final Map<String, Object> selector, final Constraint constraint,
                                                  final String resourceName) {

        if (selector == null) throw new NullPointerException("selector");
        if (constraint == null) throw new NullPointerException("constraint");

        final EntityDefinition typeDef = entityDefinitionStore.lookupByResourceName(resourceName);
        // TODO FIXME TODO FIXME TODO FIXME TODO FIXME TODO FIXME TODO FIXME
        // This is ugly - we have to capitalize here because our model
        // and API are not in sync
        final ClassType entityType = provider.getClassType(StringUtils.capitalize(typeDef.getType()));

        final SemanticSelector semanticSelector = selectorSemanticModel.parse(selector, entityType);
        final Map<Type, SelectorQueryPlan> selectorQuery = selectorQueryEngine.assembleQueryPlan(semanticSelector);

        return selectorDocument.aggregate(selectorQuery, constraint);
    }
}

