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


package org.slc.sli.ingestion.transformation;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

/**
 * The purpose behind overriding matchEntity is to set a flag that will be used to determine if a new schoolSessionAssociation record
 * is to be created.
 *
 * @author ablum
 * @author shalka
 */
public class SessionEdFi2SLITransformer extends SmooksEdFi2SLITransformer {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEdFi2SLITransformer.class);

    /**
     * Matches a Session with a Session in the database.
     *
     * @param entity
     *            The entity that needs to be matched in the database
     * @param errorReport
     *            The errorReport used to report errors
     */

    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {

    /* This code is identical to EdFiSLITransformer. We will remove this as the schoolSessionAssociat is removed */

        EntityConfig entityConfig = getEntityConfigurations().getEntityConfiguration(entity.getType());
        if (entityConfig == null || entityConfig.getReferences().isEmpty()) {
            LOG.warn("Cannot find reference configuration for entity of type: {}", entity.getType());
            return;
        }
        LOG.debug("Found reference configuration for entity of type: {}", entity.getType());

        Query query = createEntityLookupQuery(entity, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        @SuppressWarnings("deprecation")
        Iterable<Entity> match = getEntityRepository().findByQuery(entity.getType(), query, 0, 0);
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
            entity.getMetaData().putAll(matched.getMetaData());
        }

    }
}
