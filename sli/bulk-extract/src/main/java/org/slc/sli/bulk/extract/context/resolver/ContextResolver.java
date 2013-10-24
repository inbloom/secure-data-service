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

package org.slc.sli.bulk.extract.context.resolver;

import java.util.Set;

import org.slc.sli.domain.Entity;

/**
 * Given an entity, the context resolver will find the IDs of the top level
 * Local Education Agencies that have context to view this entity
 * 
 * The entity could belong to more than one top level LEA
 */
public interface ContextResolver {


    /**
     * Return a list of LEAs IDs given the entity
     * 
     * @param an
     *            entity
     * @return a set of Strings which are IDs of the top level LEA
     */
    public Set<String> findGoverningEdOrgs(Entity entity);

    /**
     * Return a set of edorg Ids given the base entity and entity to extract
     *
     *
     * @param id        base entity the entity to extract relies on
     * @param entityToExtract   entity to extract
     * @return                  set of edorg Ids
     */
    public Set<String> findGoverningEdOrgs(String id, Entity entityToExtract);

    /**
     * Return a set of edorg Ids given the base entity and entity to extract
     *
     * @param baseEntity        base entity the entity to extract relies on
     * @param entityToExtract   entity to extract
     * @return                  set of edorg Ids
     */
    //TODO: Remove after F316 is done
    public Set<String> findGoverningEdOrgs(Entity baseEntity, Entity entityToExtract);

}
