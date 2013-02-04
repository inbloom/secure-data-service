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

package org.slc.sli.api.selectors.doc;

import org.codehaus.plexus.util.StringUtils;
import org.slc.sli.api.selectors.SelectorRepository;
import org.slc.sli.api.selectors.model.SemanticSelector;
import org.slc.sli.api.selectors.model.elem.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.elem.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.elem.EmptySelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeDefaultSelectorElement;
import org.slc.sli.api.selectors.model.elem.IncludeXSDSelectorElement;
import org.slc.sli.api.selectors.model.elem.SelectorElement;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default query engine
 *
 * @author srupasinghe
 */
@Component
public class DefaultSelectorQueryEngine implements SelectorQueryEngine, SelectorQueryVisitor {

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    @Qualifier("defaultSelectorRepository")
    private SelectorRepository defaultSelectorRepository;

    @Autowired
    @Qualifier("includeAllRepository")
    private SelectorRepository includeAllSelectorRespository;

    @Override
    public SelectorQuery assembleQueryPlan(SemanticSelector semanticSelector) {
        return buildQueryPlan(semanticSelector);
    }

    protected SelectorQuery buildQueryPlan(SemanticSelector semanticSelector) {
        SelectorQuery queries = new SelectorQuery();

        for (Map.Entry<Type, List<SelectorElement>> entry : semanticSelector.entrySet()) {
            Type type = entry.getKey();
            List<SelectorElement> attributes = entry.getValue();

            SelectorQueryPlan selectorQueryPlan = handleAttributes(attributes);

            queries.put(type, selectorQueryPlan);
        }

        return queries;

    }

    protected SelectorQueryPlan handleAttributes(List<SelectorElement> attributes) {
        SelectorQueryPlan mainPlan = new SelectorQueryPlan();

        for (SelectorQueryVisitable visitableSelector : attributes) {
            SelectorQueryPlan plan = visitableSelector.accept(this);

            if (plan != null) {
                mainPlan.getChildQueryPlans().addAll(plan.getChildQueryPlans());
                mainPlan.getIncludeFields().addAll(plan.getIncludeFields());
                mainPlan.getExcludeFields().addAll(plan.getExcludeFields());
            }
        }

        return mainPlan;
    }

    @Override
    public SelectorQueryPlan visit (SelectorQueryVisitable visitable) {
        if (visitable instanceof SemanticSelector) {
            return visit((SemanticSelector)visitable);
        }
        if (visitable instanceof BooleanSelectorElement) {
            return visit((BooleanSelectorElement)visitable);
        }
        if (visitable instanceof ComplexSelectorElement) {
            return visit((ComplexSelectorElement)visitable);
        }
        if (visitable instanceof IncludeAllSelectorElement) {
            return visit((IncludeAllSelectorElement)visitable);
        }
        if (visitable instanceof IncludeXSDSelectorElement) {
            return visit((IncludeXSDSelectorElement)visitable);
        }
        if (visitable instanceof IncludeDefaultSelectorElement) {
            return visit((IncludeDefaultSelectorElement)visitable);
        }
        if (visitable instanceof EmptySelectorElement) {
            return visit((EmptySelectorElement)visitable);
        }
        return null;
    }

    public SelectorQueryPlan visit(SemanticSelector semanticSelector) {
        // No op
        return null;
    }

    public SelectorQueryPlan visit(BooleanSelectorElement booleanSelectorElement) {
        SelectorQueryPlan plan = new SelectorQueryPlan();
        String attr = booleanSelectorElement.getElementName();

        if (booleanSelectorElement.isAttribute()) {
            if (booleanSelectorElement.getQualifier()) {
                plan.getIncludeFields().add(attr);
            } else {
                plan.getExcludeFields().add(attr);
            }
        } else {
            ClassType type = (ClassType) booleanSelectorElement.getLHS();
            if (booleanSelectorElement.getQualifier()) {
                SemanticSelector defaultSelectorForType = defaultSelectorRepository.getSelector(type.getName());
                SelectorQuery defaultQueryForType = buildQueryPlan(defaultSelectorForType);

                plan.getChildQueryPlans().add(defaultQueryForType);
            } else {
                plan.getExcludeFields().add(type.getName());
            }
        }

        return plan;
    }

    public SelectorQueryPlan visit(ComplexSelectorElement complexSelectorElement) {
        SelectorQuery queries = buildQueryPlan(complexSelectorElement.getSelector());
        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.getChildQueryPlans().add(queries);

        return plan;
    }

    public SelectorQueryPlan visit(IncludeAllSelectorElement includeAllSelectorElement) {
        Type type = (Type) includeAllSelectorElement.getLHS();

        SemanticSelector selector = includeAllSelectorRespository.getSelector(type.getName());
        SelectorQuery queries = buildQueryPlan(selector);

        SelectorQueryPlan plan = queries.get(type);

        return plan;
    }

    public SelectorQueryPlan visit(IncludeXSDSelectorElement includeXSDSelectorElement) {
        Type type = (Type) includeXSDSelectorElement.getLHS();

        SelectorQueryPlan plan = new SelectorQueryPlan();
        plan.getIncludeFields().addAll(getXSDElements(type));

        return plan;
    }

    public SelectorQueryPlan visit(IncludeDefaultSelectorElement includeDefaultSelectorElement) {
        Type type = (Type) includeDefaultSelectorElement.getLHS();

        SemanticSelector selector = defaultSelectorRepository.getSelector(type.getName());
        SelectorQuery queries = buildQueryPlan(selector);

        SelectorQueryPlan plan = queries.get(type);

        return plan;
    }

    public SelectorQueryPlan visit(EmptySelectorElement emptySelectorElement) {
        return new SelectorQueryPlan();
    }

    protected Set<String> getXSDElements(Type type) {
        NeutralSchema schema = schemaRepository.getSchema(StringUtils.uncapitalise(type.getName()));

        return (schema != null) ? schema.getFields().keySet() : Collections.EMPTY_SET;
    }
}
