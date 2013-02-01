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

import java.security.SecureRandom;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 * Generates Type 1 (time-based) UUIDs, preceeded by a shard key 'YYYYRR-'.
 *
 * @author wscott
 */
@Component
@Qualifier("shardType1UUIDGeneratorStrategy")
public class ShardType1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    private TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    SecureRandom r = new SecureRandom();

    /**
     * Generate a shardable type 1 random UUID .
     */
    @Override
    public String generateId() {
        StringBuilder builder = new StringBuilder();
        char c1 = (char) (r.nextInt(26) + 'a');
        char c2 = (char) (r.nextInt(26) + 'a');
        builder.append(new DateTime().getYear());
        builder.append(c1);
        builder.append(c2);
        builder.append("-");
        builder.append(generator.generate().toString());
        String uuid = builder.toString();
        return uuid;
    }

    @Override
    public String generateId(NaturalKeyDescriptor naturalKeyDescriptor) {
        return generateId();
    }
}
