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


package org.slc.sli.api.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.Entity;

import java.util.List;

/**
 * Interface for objects used to transform entities between their database representations and their
 * exposed representations on the ReST URI.
 *
 * The following invariants must hold:
 * --Treatment.toStored(Transformer.toExposed(object)).equals(object)
 * --Treatment.toExposed(Transformer.toStored(object)).equals(object)
 *
 * @author nbrown
 *
 */
public interface Treatment {

    /**
     * Transform from an exposed entity to a stored entity
     *
     * @param exposed
     *            The entity in the form it is exposed via ReST
     * @param defn
     *            
     * @return The entity in the form it is stored in the DB
     */
    public List<EntityBody> toStored(List<EntityBody> exposed, EntityDefinition defn);

    /**
     * Transform from a stored entity to an exposed entity
     *
     * @param stored 
     *            
     * @param defn 
     *            
     * @param entity
     *            The entity in the form it is stored in the DB
     * @return The entity in the form it is exposed via ReST
     */
    public EntityBody toExposed(EntityBody stored, EntityDefinition defn, Entity entity);

}
