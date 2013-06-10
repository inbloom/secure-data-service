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

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Resolver for studentCompetency
 * 
 * @author ycao
 * 
 */
@Component
public class StudentCompetencyContextResolver implements ContextResolver {

    public static final String STUDENT_SECTION_ASSOCIATION_ID = "studentSectionAssociationId";
    public static final String STUDENT_ID = "studentId";

    @Autowired
    StudentContextResolver studentResolver;
    
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        if (entity == null) {
            return Collections.emptySet();
        }
        
        String studentSectionAssociationId = (String) entity.getBody().get(STUDENT_SECTION_ASSOCIATION_ID);
        if (studentSectionAssociationId == null) {
            return Collections.emptySet();
        }
        
        Entity studentSectionAssociation = repo.findById(EntityNames.STUDENT_SECTION_ASSOCIATION, studentSectionAssociationId);
        
        if (studentSectionAssociation == null) {
            return Collections.emptySet();
        }

        String studentId = (String) studentSectionAssociation.getBody().get(STUDENT_ID);

        return studentResolver.findGoverningEdOrgs(studentId);
    }
    
}
