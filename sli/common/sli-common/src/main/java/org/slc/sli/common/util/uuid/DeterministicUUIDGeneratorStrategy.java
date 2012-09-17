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
package org.slc.sli.common.util.uuid;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("deterministicUUIDGeneratorStrategy")
public class DeterministicUUIDGeneratorStrategy implements UUIDGeneratorStrategy {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(DeterministicUUIDGeneratorStrategy.class);
    
    public static final String DIGEST_ALGORITHM = "SHA-256";
    private String deliminator = "||";
    
    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    ShardType1UUIDGeneratorStrategy oldStrategy;
    
    @Override
    public String generateId() {
        return oldStrategy.generateId();
    }
    
    @Override
    public String generateId(NaturalKeyDescriptor naturalKeyDescriptor) {
        
        // if no natural keys exist, can't generate deterministic id
        if (naturalKeyDescriptor == null || naturalKeyDescriptor.getNaturalKeys() == null
                || naturalKeyDescriptor.getNaturalKeys().isEmpty()) {
            return generateId();
        }
        
        UUID uuid = null;
        
        try {
            // Get values in alphabetical order
            Map<String, String> naturalKeys = naturalKeyDescriptor.getNaturalKeys();
            List<String> keyList = new ArrayList<String>(naturalKeys.keySet());
            Collections.sort(keyList);
            
            // Concatenate values together into one string
            StringBuffer keyValues = new StringBuffer();
            keyValues.append(naturalKeyDescriptor.getEntityType()).append(deliminator);
            keyValues.append(naturalKeyDescriptor.getTenantId()).append(deliminator);
            for (String key : keyList) {
                keyValues.append(naturalKeys.get(key)).append(deliminator);
            }
            // Digest keyValue string into hash
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] keyValueBytes = keyValues.toString().getBytes();
            messageDigest.update(keyValueBytes);
            byte[] digestBytes = messageDigest.digest();
            messageDigest.reset();
            uuid = generateUuid(digestBytes);
            
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            LOG.error(noSuchAlgorithmException.getMessage());
        }
        return uuid.toString();
    }
    
    protected static UUID generateUuid(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        
        long msb = byteBuffer.getLong(0);
        long lsb = byteBuffer.getLong(8);
        
        UUID uuid = new UUID(msb, lsb);
        
        return uuid;
    }
    
}
