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

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * @author ablum tke
 */
@Component
public class PathDateRetriever implements DateRetriever{

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private SimpleDateRetriever simpleDateRetriever;

    @Override
    public String retrieve(Entity entity) {
        String entityType = entity.getType();
        Map<String, String> targetAndField = EntityDates.ENTITY_PATH_FIELDS.get(entityType);

        if (targetAndField == null) {
            return simpleDateRetriever.retrieve(entity);
        }

        Entity targetEntity = getPathEntity(entity);

        if (targetEntity == null) {
            return null;
        }

        return retrieve(targetEntity);
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

    public SimpleDateRetriever getSimpleDateRetriever() {
        return simpleDateRetriever;
    }

    public void setSimpleDateRetriever(SimpleDateRetriever simpleDateRetriever) {
        this.simpleDateRetriever = simpleDateRetriever;
    }

    public Repository<Entity> getRepo() {
        return repo;
    }

    public void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }
}
