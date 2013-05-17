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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Context resolver for discipline incidents
 * 
 * @author nbrown
 * 
 */
@Component
public class DisciplineIncidentContextResolver extends EdOrgRelatedReferrableResolver {
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    @Autowired
    private StudentContextResolver studentResolver;
    
    @Override
    protected Set<String> getTransitiveAssociations(Entity entity) {
        Set<String> leas = new HashSet<String>();
        Iterator<Entity> students = repo.findEach(EntityNames.STUDENT,
                Query.query(Criteria.where("studentDisciplineIncidentAssociation.body.disciplineIncidentId").is(entity.getEntityId())));
        while (students.hasNext()) {
            leas.addAll(studentResolver.findGoverningLEA(students.next()));
        }
        return leas;
    }
    
    @Override
    protected String getCollection() {
        return EntityNames.DISCIPLINE_INCIDENT;
    }
    
}
