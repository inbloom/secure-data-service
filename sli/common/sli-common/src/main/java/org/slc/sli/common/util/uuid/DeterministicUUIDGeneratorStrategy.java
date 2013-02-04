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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;

/**
 *
 * TODO: add class javadoc
 *
 */
@Component
@Qualifier("deterministicUUIDGeneratorStrategy")
public class DeterministicUUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(DeterministicUUIDGeneratorStrategy.class);

    public static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String DELIMITER_1 = "|";
    private static final String DELIMITER_1_REGEX = "\\|";
    private static final String DELIMITER_1_REPLACEMENT = DELIMITER_1 + DELIMITER_1;
    private static final String DELIMITER_2 = "~";
    private static final String DELIMITER_2_REGEX = "~";
    private static final String DELIMITER_2_REPLACEMENT = DELIMITER_2 + DELIMITER_2;
    private static final String DELIMITER = DELIMITER_1 + DELIMITER_2;

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    ShardType1UUIDGeneratorStrategy uuidStrategy;

    @Override
    public String generateId() {
        return uuidStrategy.generateId();
    }

    @Override
    public String generateId(NaturalKeyDescriptor naturalKeyDescriptor) {

        // if no natural keys exist, can't generate deterministic id
        if (naturalKeyDescriptor == null || naturalKeyDescriptor.getNaturalKeys() == null
                || naturalKeyDescriptor.getNaturalKeys().isEmpty()) {
            return generateId();
        }

        // Get values in alphabetical order
        Map<String, String> naturalKeys = naturalKeyDescriptor.getNaturalKeys();
        List<String> keyList = new ArrayList<String>(naturalKeys.keySet());
        Collections.sort(keyList);

        // Concatenate values together into one string
        StringBuffer keyValues = new StringBuffer();
        keyValues.append(escapeDelimiters(naturalKeyDescriptor.getEntityType())).append(DELIMITER);
        keyValues.append(escapeDelimiters(naturalKeyDescriptor.getTenantId())).append(DELIMITER);
        for (String key : keyList) {
            keyValues.append(escapeDelimiters(naturalKeys.get(key))).append(DELIMITER);
        }
        // Digest keyValue string into hash
        String hexHash = DigestUtils.shaHex(keyValues.toString().getBytes());
        if (naturalKeyDescriptor.getParentId() != null) {
            hexHash = naturalKeyDescriptor.getParentId() + hexHash;
        }
        return hexHash + "_id";
    }

    private String escapeDelimiters(String input) {
        if (input == null) {
            return "";
        }
        String output = input.replaceAll(DELIMITER_1_REGEX, DELIMITER_1_REPLACEMENT).replaceAll(DELIMITER_2_REGEX,
                DELIMITER_2_REPLACEMENT);
        return output;
    }

    protected static UUID generateUuid(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        long msb = byteBuffer.getLong(0);
        long lsb = byteBuffer.getLong(8);

        UUID uuid = new UUID(msb, lsb);

        return uuid;
    }

}
