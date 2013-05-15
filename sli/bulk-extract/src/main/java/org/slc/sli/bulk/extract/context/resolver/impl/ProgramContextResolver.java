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

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Context resolver for programs
 * Programs go with any edorg that references it (along with any in that hierarchy), and with any
 * edorg that contains a student that has a student program association with the program
 * 
 * @author nbrown
 * 
 */
@Component
public class ProgramContextResolver implements ContextResolver {
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Autowired
    private StudentContextResolver studentResolver;
    
    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        String programId = entity.getEntityId();
        Iterator<Entity> edOrgs = repo.findEach(EntityNames.EDUCATION_ORGANIZATION,
                Query.query(Criteria.where("body.programReference").is(programId)));
        Set<String> leas = new HashSet<String>();
        while (edOrgs.hasNext()) {
            leas.addAll(edOrgResolver.findGoverningLEA(edOrgs.next()));
        }
        Iterator<Entity> students = repo.findEach(EntityNames.STUDENT,
                Query.query(Criteria.where("studentProgramAssociation.body.programId").is(programId)));
        while (students.hasNext()) {
            leas.addAll(studentResolver.findGoverningLEA(students.next()));
        }
        return leas;
    }
    
}
