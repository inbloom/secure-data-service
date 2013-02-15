/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates
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

package org.slc.sli.dal.convert;

import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jstokes
 */
@Component
public class ContainerDocumentAccessor {

    @Autowired
    private ContainerDocumentHolder containerDocumentHolder;

//    @Autowired
//    @Qualifier("deterministicUUIDGeneratorStrategy")
//    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy;

    public boolean isContainerDocument(final String entity) {
        return containerDocumentHolder.isContainerDocument(entity);
    }

    public String createParentKey(final  Entity entity) {
        ContainerDocument containerDocument = containerDocumentHolder.getContainerDocument(entity.getType());
        Map<String,String> parentKeyMap = containerDocument.getParentNaturalKeyMap();
        Map<String, String> naturalKeyMap = new HashMap<String, String>();
        for(String key : parentKeyMap.keySet()) {
           String value = (String) entity.getBody().get(key);
            naturalKeyMap.put(key,value);
        }
        final NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeyMap);
       return deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);
    }
}
