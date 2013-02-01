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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;

/**
 *
 * SLI Long Schema which validates long entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class LongSchema extends PrimitiveNumericSchema<Long> {

    // Constructors
    public LongSchema() {
        super(NeutralSchemaType.LONG);
    }
    

    @Override
    public Object convert(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException nfe) {
                throw (IllegalArgumentException) new IllegalArgumentException(value + " cannot be parsed to a long").initCause(nfe);
            }
        }
        
        throw new IllegalArgumentException(value + " is not a long");
    }

}
