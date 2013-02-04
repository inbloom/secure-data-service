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


package org.slc.sli.ingestion.dal;

import java.util.Map;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;

/**
 * Provides encryption for staging database by extending the EntityEncryption. It creates a Pii Map by reading the configuration from the entity's json file.
 *
 * @author npandey
 *
 */
public class StagingEntityEncryption extends EntityEncryption {
    private EntityConfigFactory entityConfigFactory;

    public EntityConfigFactory getEntityConfigFactory() {
        return entityConfigFactory;
    }

    public void setEntityConfigFactory(EntityConfigFactory entityConfigFactory) {
        this.entityConfigFactory = entityConfigFactory;
    }

    @Override
    protected Map<String, Object> buildPiiMap(String entityType) {
        String realEntityType = entityType;
        //change the transformed entity type to get the
        //right configureation file
        if (entityType.contains("_transformed")) {
            realEntityType = realEntityType.substring(0, realEntityType.indexOf('_'));
        }
        EntityConfig ec = entityConfigFactory.getEntityConfiguration(realEntityType);
        return ec != null ? ec.getPiiFields() : null;
    }
}
