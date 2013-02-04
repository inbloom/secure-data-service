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


package org.slc.sli.validation;

import java.util.List;

import org.slc.sli.validation.schema.NeutralSchema;


/**
 * Interface for reading schemas from XSD and converting them into neutral schema objects
 *
 * @author nbrown
 *
 */
public interface SchemaRepository {

    /**
     * Gets the schema for the type
     *
     * @param type the type for the schema to look up
     * @return the actual schema
     */
    public NeutralSchema getSchema(String type);

    /**
     * Gets the schema for an underlying field on the given type
     *
     * @param type the type for the schema to look up
     * @param field the potentially nested field whose schema is to be returned
     */
    public NeutralSchema getSchema(String type, String field);

    /**
     * Returns the NeutralSchemas in this repository.
     *
     * @return the NeutralSchemas in this repository.
     */
    public List<NeutralSchema> getSchemas();

}
