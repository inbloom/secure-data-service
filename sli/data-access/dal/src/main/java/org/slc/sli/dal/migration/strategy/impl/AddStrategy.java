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

package org.slc.sli.dal.migration.strategy.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Supports the migration of entities by adding a new field with a default value.
 *
 * @author pghosh
 * @author kmyers
 */

@Scope("prototype")
@Component
public class AddStrategy implements MigrationStrategy<Entity> {

    public static final String FIELD_NAME = "fieldName";
    public static final String DEFAULT_VALUE = "defaultValue";

    private String fieldName;
    private Object defaultValue;

    @Override
    public Entity migrate(Entity entity) throws MigrationException {
        try {
            PropertyUtils.setProperty(entity.getBody(), fieldName, defaultValue);
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
        this.defaultValue = parameters.get(DEFAULT_VALUE);
    }

    @Override
    public List<Entity> migrate(List<Entity> entityList) throws MigrationException {
        throw new MigrationException(new IllegalAccessException("This method is not yet implemented"));
    }

}
