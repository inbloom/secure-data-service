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
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFieldStrategy implements MigrationStrategy<Entity> {

    public static final String FIELD_NAME = "fieldName";
    public static final String RULE_SET = "ruleSet";

    private String fieldName;
    private List<TransformationRule> ruleSet;

    @Override
    public Entity migrate(Entity entity) throws MigrationException {
        try {
            // TODO: DO SOMETHING ELSE HERE!
            PropertyUtils.setProperty(entity.getBody(), fieldName, ruleSet);
        } catch (IllegalAccessException e) {
            throw new MigrationException(e);
        } catch (InvocationTargetException e) {
            throw new MigrationException(e);
        } catch (NoSuchMethodException e) {
            throw new MigrationException(e);
        }
        return entity;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) throws MigrationException {
        if (parameters == null || !parameters.containsKey(FIELD_NAME)) {
            throw new MigrationException(new IllegalArgumentException("Add strategy missing required argument: fieldName"));
        }

        this.fieldName = parameters.get(FIELD_NAME).toString();
        this.ruleSet = parseRules(parameters.get(RULE_SET));
    }

    private List<TransformationRule> parseRules(Object ruleSet) {
        List<TransformationRule> rules = new ArrayList<TransformationRule>();
        return rules;
    }
}
