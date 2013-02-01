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


package org.slc.sli.validation.schema;

/**
 * Interface for defining a neutral schema string write operation. A transform operation
 * takes the provided neutral schema, applies transform logic, and returns a string
 * representing the resulting schema representation.
 *
 * @author asaarela
 *
 */
public interface NeutralSchemaStringWriter {

    /**
     * Transform the provided NeutralSchema object and return the resulting representation.
     *
     * @param schema
     *            NeutralSchema to transform
     * @return String containing the new representation.
     */
    String transform(NeutralSchema schema);

}
