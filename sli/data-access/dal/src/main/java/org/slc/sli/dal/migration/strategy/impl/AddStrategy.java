/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.TransformStrategy;
import org.slc.sli.domain.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 12/10/12
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */

public class AddStrategy implements TransformStrategy {

    private static final String FIELD_NAME = "fieldName";
    private static final String DEFAULT_VALUE = "defaultValue";
    
    private String fieldName;
    private Object defaultValue;
    
    @Override
    public Entity transform(Entity entity) throws MigrationException {
        try {
            PropertyUtils.setProperty(entity, fieldName, defaultValue);
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
    public void setParameters(Map<String, Object> parameters) {
        
        if (!parameters.containsKey(FIELD_NAME)) {
            throw new IllegalArgumentException("Add strategy missing required argument: fieldName");
        }
        
        this.fieldName = parameters.get(FIELD_NAME).toString();
        this.defaultValue = parameters.get(DEFAULT_VALUE);
    }
    
}
