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

import org.apache.commons.lang.StringUtils;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.api.selectors.model.SelectorSemanticModel;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.service.query.SelectionConverter;
import org.slc.sli.api.service.query.Selector2MapOfMaps;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a semantic selector for the override operation
 *
 * @author srupasinghe
 *
 */
@Component("includeAllRepository")
public class IncludeAllSelectorRepository implements SelectorRepository {

    private Map<String, SemanticSelector> selectors = new HashMap<String, SemanticSelector>();

    @Autowired
    private SelectorSemanticModel selectorSemanticModel;

    @Autowired
    private ModelProvider modelProvider;

    @Override
    public SemanticSelector getSelector(String type) {

        if (selectors.containsKey(type)) {
            return selectors.get(type);
        }

        ClassType classType = modelProvider.getClassType(type);
        String selectorString = constructSelectorString(classType);

        SemanticSelector selector = constructSelector(classType, selectorString);
        selectors.put(type, selector);

        return selector;
    }

    protected String constructSelectorString(Type type) {
        List<String> features = new ArrayList<String>();
        List<AssociationEnd> ends = modelProvider.getAssociationEnds(type.getId());

        features.add("$");
        for (AssociationEnd end : ends) {
            features.add(end.getName());
        }

        return ":(" + StringUtils.join(features, ",") + ")";
    }

    protected SemanticSelector constructSelector(ClassType type, String selectorString) {
        SelectionConverter selectionConverter = new Selector2MapOfMaps(false);

        return selectorSemanticModel.parse(selectionConverter.convert(selectorString), type);
    }
}
