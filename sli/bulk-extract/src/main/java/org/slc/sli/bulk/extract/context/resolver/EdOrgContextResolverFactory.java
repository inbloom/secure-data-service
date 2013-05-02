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

package org.slc.sli.bulk.extract.context.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.impl.EducationOrganizationContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentContextResolver;
import org.slc.sli.bulk.extract.context.resolver.impl.StudentDirectRelatedContextResolver;

/**
 * Factory class for context resolvers, which are used to
 * enforce business visibility rule as in determines which
 * LEAs own the entity
 * 
 * @author ycao
 * 
 */
@Component
public class EdOrgContextResolverFactory {
    
    @Autowired
    EducationOrganizationContextResolver edOrgContextResolver;
    
    /**
     * Two things must in common for a entity to be a student direct related entity:
     * 1. the entity must have a "body.studentId" field
     * 2. the business rule for visibility for those entities must be: all for all current students,
     * i.e. we only check if the student belongs to a certain LEA
     */
    @Autowired
    StudentDirectRelatedContextResolver studentDirectRelatedContextResolver;
    
    @Autowired
    StudentContextResolver studentResolver;

    /**
     * find responsible resolver for this entity type
     * 
     * @param entityType
     * @return context resolver for this entity type
     */
    public ContextResolver getResolver(String entityType) {
        
        if ("educationOrganization".equals(entityType)) {
            return edOrgContextResolver;
        }
        
        if ("student".equals(entityType)) {
            return studentResolver;
        }

        if ("studentSchoolAssociation".equals(entityType)
                || "studentAssessment".equals(entityType)) {
            return studentDirectRelatedContextResolver;
        }
        
        if ("studentGradebookEntry".equals(entityType)) {
            // for now use the simple resolver, but be advised this
            // entity may have additional business rules that is depended
            // on section. Need to revisit this entity when we play
            // "section" story
            return studentDirectRelatedContextResolver;
        }

        return null;
    }
}
