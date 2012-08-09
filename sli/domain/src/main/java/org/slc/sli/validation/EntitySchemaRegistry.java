/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.validation;

import org.apache.avro.Schema;

import org.slc.sli.domain.Entity;

/**
 * Provides a registry for fetching avro schema.
 *
 * @author Sean Melody <smelody@wgen.net>
 *
 */

public interface EntitySchemaRegistry {

    /**
     * Returns the schema for the given type, or null if this schema has not been registered.
     *
     * @param entity
     * @return
     */
    public Schema findSchemaForType(Entity entity);

    /**
     * Returns the schema for the given type, or null if this schema has not been registered.
     *
     * @param entityType
     * @return
     */
    public Schema findSchemaForName(String entityType);

}
