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

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.selectors.doc.SelectorDocument;
import org.slc.sli.api.selectors.doc.SelectorQuery;
import org.slc.sli.api.selectors.doc.SelectorQueryEngine;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private ResourceHelper resourceHelper;

    private static final List<String> UNSUPPORTED_RESOURCE_LIST = new ArrayList<String>();
    static {
        UNSUPPORTED_RESOURCE_LIST.add("application");
        UNSUPPORTED_RESOURCE_LIST.add("tenant");
    }

    @Override
    public List<EntityBody> getEntities(final ApiQuery apiQuery, final String resourceName) {

        if (apiQuery == null) {
            throw new IllegalArgumentException("apiQuery");
        }

        if (apiQuery.getSelector() == null) {
            throw new UnsupportedSelectorException("No selector to parse");
        }

        final EntityDefinition typeDef = resourceHelper.getEntityDefinition(resourceName);
        final ClassType entityType = provider.getClassType(StringUtils.capitalize(typeDef.getType()));

        if (UNSUPPORTED_RESOURCE_LIST.contains(resourceName)) {
            throw new UnsupportedSelectorException("Selector is not supported yet for this resource");
        }

        final SemanticSelector semanticSelector = selectorSemanticModel.parse(apiQuery.getSelector(), entityType);
        final SelectorQuery selectorQuery = selectorQueryEngine.assembleQueryPlan(semanticSelector);

        return selectorDocument.aggregate(selectorQuery, apiQuery);
    }
}

