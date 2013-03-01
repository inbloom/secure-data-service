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

import org.slc.sli.common.migration.strategy.MigrationException;
import org.slc.sli.common.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;

import java.util.List;
import java.util.Map;

/**
 * Supports the migration of entities by renaming a data field.
 * Will not work with nested fields.
 * 
 * @author kmyers
 */

public class RenameFieldStrategy implements MigrationStrategy<Entity> {

    public static final String OLD_FIELD_NAME = "oldFieldName";
    public static final String NEW_FIELD_NAME = "newFieldName";
    
    private String oldFieldName;
    private String newFieldName;
    
    @Override
    public Entity migrate(Entity entity) throws MigrationException {
        
        entity.getBody().put(newFieldName, entity.getBody().remove(oldFieldName));
        
        return entity;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) throws MigrationException {

        if (parameters == null) {
            throw new MigrationException(new IllegalArgumentException("Rename strategy missing required arguments: "));
        }

        if (!parameters.containsKey(OLD_FIELD_NAME)) {
            throw new MigrationException(new IllegalArgumentException("Rename strategy missing required argument: " + OLD_FIELD_NAME));
        }

        if (!parameters.containsKey(NEW_FIELD_NAME)) {
            throw new MigrationException(new IllegalArgumentException("Rename strategy missing required argument: " + NEW_FIELD_NAME));
        }
        
        this.oldFieldName = parameters.get(OLD_FIELD_NAME).toString();
        this.newFieldName = parameters.get(NEW_FIELD_NAME).toString();
    }

    @Override
    public List<Entity> migrate(List<Entity> entityList) throws MigrationException {
        throw new MigrationException(new IllegalAccessException("This method is not yet implemented"));
    }

}
