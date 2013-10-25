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
package org.slc.sli.bulk.extract.context.resolver.impl;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Resolver for gradebook entries
 * 
 * @author nbrown
 *
 */
@Component
public class GradebookEntryContextResolver implements ContextResolver {

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;

    @Autowired
    private StudentContextResolver studentResolver;

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        Set<String> edOrgs = new HashSet<String>();

        Iterator<Entity> studentGradebookEntries = repo.findEach(EntityNames.STUDENT_GRADEBOOK_ENTRY,
                Query.query(Criteria.where("body.gradebookEntryId").is(entity.getEntityId())));

        while(studentGradebookEntries.hasNext()) {
            Entity studentGradebookEntry = studentGradebookEntries.next();
            edOrgs.addAll(studentResolver.findGoverningEdOrgs((String) studentGradebookEntry.getBody().get(ParameterConstants.STUDENT_ID), entity));
        }
        return edOrgs;
    }

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity, Entity entityToExtract) {
        return null;
    }
}
