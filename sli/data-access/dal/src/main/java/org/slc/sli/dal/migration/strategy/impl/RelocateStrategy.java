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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Supports moving fields between body and outside of body
 * 
 * @author ycao 
 */

public class RelocateStrategy implements MigrationStrategy {

    public static final String TO = "to";
    public static final String FROM = "from";

    private String from;
    private String to;

    @Override
    public Entity migrate(Entity entity) throws MigrationException {
        if (from == null || to == null) {
            throw new MigrationException(new IllegalArgumentException("RelocateStrategy missing required relocation instructions, please set parameters first"));
        }


        List<Map<String, Object>> fromObjectList = getFromObject(entity);

        // "from" doesn't point to anything, nothing to relocate
        if (fromObjectList == null) {
            return entity;
        }

        if (this.to.startsWith(".")) {
            //moving to outside body
            String type = this.from;
            List<Entity> entities = new ArrayList<Entity>();

            for (Map<String, Object> map : fromObjectList) {
                entities.add(new MongoEntity(type, null, map, null));
            }

            entity.getEmbeddedData().put(this.to.substring(1), entities);
        } else {
            //moving inside body
            entity.getBody().put(this.to, fromObjectList);
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFromObject(Entity entity) throws MigrationException {
        Object fromObject = null;
        boolean outsideBody = this.from.startsWith(".");

        if (outsideBody) {
            fromObject = entity.getEmbeddedData().remove(this.from.substring(1));
        } else {
            fromObject = entity.getBody().remove(this.from);
        }

        if (fromObject == null) {
            //from field doesn't exists
            return null;
        }

        if (!(fromObject instanceof List<?>)) {
            throw new MigrationException(new RuntimeException("RelocateStrategy can not relocate simple types"));
        } 

        List<Map<String, Object>> fromObjectList;
        try {
            if(outsideBody) {
                fromObjectList = new ArrayList<Map<String, Object>>();
                List<Entity> embeddedList = (List<Entity>) fromObject;
                for (Entity e : embeddedList) {
                    fromObjectList.add(e.getBody());
                }
            } else {
                fromObjectList = (List<Map<String, Object>>) fromObject;
            }
        } catch (ClassCastException e) {
            throw new MigrationException(e);
        }

        return fromObjectList;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) throws MigrationException {
        if (parameters == null || parameters.size() == 0 || !parameters.containsKey(TO) || !parameters.containsKey(FROM)) {
            throw new MigrationException(new IllegalArgumentException("RelocateStrategy missing required relocation instructions"));
        }

        Object from = parameters.get(FROM);
        Object to = parameters.get(TO);
        if (!(from instanceof String) || !(to instanceof String)) {
            throw new MigrationException(new IllegalArgumentException("RelocateStrategy can not interprete relocation instructions"));
        }

        this.from = (String) from;
        this.to = (String) to;
    }

}
