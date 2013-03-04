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
package org.slc.sli.api.migration.strategy.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.api.migration.TransformationRule;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Component
public class AddFieldStrategy implements MigrationStrategy<EntityBody> {

    public static final String FIELD_NAME = "fieldName";
    public static final String RULE_SET = "ruleSet";

    private String fieldName;
    private List<TransformationRule> ruleSet;

    public void setRepository(PagingRepositoryDelegate<Entity> repository) {
        this.repository = repository;
    }

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    @Override
    public EntityBody migrate(EntityBody entity) throws MigrationException {
        throw new MigrationException(new IllegalAccessException("This method is not yet implemented"));

    }

    @Override
    public void setParameters(Map<String, Object> parameters) throws MigrationException {
        if (parameters == null || !parameters.containsKey(FIELD_NAME)) {
            throw new MigrationException(new IllegalArgumentException("Add strategy missing required argument: fieldName"));
        }

        this.fieldName = parameters.get(FIELD_NAME).toString();
        this.ruleSet = parseRules(parameters.get(RULE_SET));
    }

    @Override
    public List<EntityBody> migrate(List<EntityBody> entityList) throws MigrationException {
        for(EntityBody entityBody: entityList) {
            try {
                PropertyUtils.setProperty(entityBody, fieldName, resolveRules(entityBody));
            } catch (IllegalAccessException e) {
                throw new MigrationException(e);
            } catch (InvocationTargetException e) {
                throw new MigrationException(e);
            } catch (NoSuchMethodException e) {
                throw new MigrationException(e);
            }
        }
        return entityList;
    }

    private List<TransformationRule> parseRules(Object ruleSet) {
        List<TransformationRule> rules = new ArrayList<TransformationRule>();
        for(Object ruleKey:(ArrayList) ruleSet) {
            Map<String, Object> rule = (Map<String, Object>) ruleKey;
            rules.add(TransformationRule.init((Integer) rule.get("rank"), (HashMap<String, String>) rule.get("rule")));
        }
        Collections.sort(rules, new Comparator<TransformationRule>() {
            @Override
            public int compare(TransformationRule transformationRule, TransformationRule transformationRule2) {
                return (transformationRule.compareTo(transformationRule2));
            }
        });
        return rules;
    }

    private String resolveRules(EntityBody entityBody) {
        String id = (String) entityBody.get(ruleSet.get(0).getField());
        for (int i = 1; i < ruleSet.size(); i++) {
            TransformationRule rule = ruleSet.get(i);
            Entity e = repository.findById(rule.getCollection(), id);
            List<String> fields = Arrays.asList(rule.getField().split(","));
            Map<String, Object> body = e.getBody();
            Object value = null;
            for(String field: fields) {
                value = body.get(field);
                body.clear();
                if(value instanceof Map) {
                    body.putAll((Map<? extends String,?>) value);
                }
            }
            id = (String) value;
        }
        return id;
    }
}
