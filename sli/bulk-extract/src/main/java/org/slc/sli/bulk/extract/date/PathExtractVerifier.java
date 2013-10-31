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
package org.slc.sli.bulk.extract.date;

import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * @author tke tshewchuk
 */
@Component
public class PathExtractVerifier implements ExtractVerifier{

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;

    @Override
    public boolean shouldExtract(Entity entity, DateTime upToDate) {
        Entity targetEntity = getPathEntity(entity);

        if (targetEntity == null) {
            return false;
        }

        ExtractVerifier extractVerifier = getExtractVerifer(targetEntity.getType());

        return extractVerifier.shouldExtract(targetEntity, upToDate);
    }

    protected Entity getPathEntity(Entity entity) {
        String entityType = entity.getType();
        Map<String, String> targetAndField = EntityDates.ENTITY_PATH_FIELDS.get(entityType);

        NeutralQuery query = new NeutralQuery();

        for (Map.Entry<String, String> entry : targetAndField.entrySet()) {
            String targetId = (String) entity.getBody().get(entry.getValue());
            query.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, targetId));
            return repo.findOne(entry.getKey(), query);
        }

        return null;
    }

    public Repository<Entity> getRepo() {
        return repo;
    }

    public void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }

    protected ExtractVerifier getExtractVerifer(String entityType) {
        return ExtractVerifierFactory.retrieveExtractVerifier(entityType);
    }

}
