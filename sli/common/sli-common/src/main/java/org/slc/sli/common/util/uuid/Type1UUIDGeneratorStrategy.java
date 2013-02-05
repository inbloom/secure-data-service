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

package org.slc.sli.common.util.uuid;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 * Generates Type 1 (time-based) UUIDs.
 *
 * @author smelody
 *
 */
@Component
@Qualifier("type1UUIDGeneratorStrategy")
public class Type1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    private TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

    /**
     * Generate a type 1 random UUID.
     */
    @Override
    public String generateId() {
        return generator.generate().toString();
    }

    @Override
    public String generateId(NaturalKeyDescriptor naturalKeyDescriptor) {
        return generateId();
    }
}
