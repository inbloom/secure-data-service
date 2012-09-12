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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
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

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    private DidEntityConfigFactory didEntityConfigurations;

    @Autowired
    private DidRefConfigFactory didRefConfigurations;

    private static final Logger LOG = LoggerFactory.getLogger(IdNormalizer.class);

    public void resolveInternalIds(Entity entity, String tenantId, ErrorReport errorReport) {

        DidEntityConfig entityConfig = didEntityConfigurations.getDidEntityConfiguration(entity.getType());

        if (entityConfig == null) {
            return;
        }

        // for each reference in entityConfig
        for (DidRefSource didRefSource : entityConfig.getReferenceSources()) {
            // pull out reference type map from entityConfig
            // if a list, resolve to list of ids (iterate over list)
            // if a single object, resolve to single id

            DidRefConfig didRefConfig = didRefConfigurations.getDidRefConfiguration(didRefSource.getEntityType());

            if (didRefConfig == null) {
                continue;
            }

            // resolve reference type map to single id

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> reference = (Map<String, Object>) PropertyUtils.getProperty(entity, didRefSource.getSourceRefPath());
                String uuid = getId(reference, tenantId, didRefConfig);
                PropertyUtils.setProperty(entity, didRefSource.getDidFieldPath(), uuid);
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // function which, given reference type map (source object) and refConfig, return a did
    private String getId(Map<String, Object> reference, String tenantId, DidRefConfig refConfig) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Map<String, String> naturalKeys = new HashMap<String, String>();

        for (KeyFieldDef keyFieldDef : refConfig.getKeyFields()) {
            // populate naturalKeys
            String value = null;
            if (keyFieldDef.getRefConfig() != null) {
                value = getId(reference, tenantId, keyFieldDef.getRefConfig());
            } else {
                value = (String) PropertyUtils.getProperty(reference, keyFieldDef.getValueSource());
            }

            String fieldName = keyFieldDef.getKeyFieldName();

            naturalKeys.put(fieldName, value);
        }

        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeys, tenantId, refConfig.getEntityType());
        return uuidGeneratorStrategy.generateId(naturalKeyDescriptor);
    }
}
