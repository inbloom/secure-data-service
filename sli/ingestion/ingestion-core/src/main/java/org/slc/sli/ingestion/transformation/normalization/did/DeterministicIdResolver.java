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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Resolver for deterministic Id resolution.
 *
 * @author jtully
 *
 */
@Component
public class DeterministicIdResolver {

//    @Autowired
//    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    private static final Logger LOG = LoggerFactory.getLogger(IdNormalizer.class);

    public void resolveInternalIds(Entity entity, String tenantId, DidEntityConfig entityConfig, DidRefConfig refConfig,
            ErrorReport errorReport) {

        // for each reference in entityConfig

        // pull out reference type map from entityConfig
        // if a list, resolve to list of ids (iterate over list)
        // if a single object, resolve to single id

        // resolve reference type map to single id

        // function which, given reference type map (source object) and refConfig, return a did

        Map<String, String> naturalKeys = new HashMap<String, String>();

        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeys, tenantId, entity.getType());
//        String uuid = uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
    }
}
