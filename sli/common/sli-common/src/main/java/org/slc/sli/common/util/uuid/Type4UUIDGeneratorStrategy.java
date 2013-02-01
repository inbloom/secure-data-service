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

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 * Generates Type 4 (random) UUIDs.
 *
 * @author smelody
 *
 */
@Component
@Qualifier("type4UUIDGeneratorStrategy")
public class Type4UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    /**
     * Generate a type 4 random UUID.
     */
    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateId(NaturalKeyDescriptor naturalKeyDescriptor) {
        return generateId();
    }

}
