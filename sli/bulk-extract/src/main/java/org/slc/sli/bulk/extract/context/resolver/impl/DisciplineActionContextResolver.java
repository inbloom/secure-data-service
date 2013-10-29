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
import java.util.List;
import java.util.Set;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Context resolver for discipline actions
 * Discipline actions go with any edorg that references it (along with any in that hierarchy), and
 * with any edorg that contains a student that is mentioned in the action
 * Just similar enough to other resolvers like programs and discipline incidents for me to attempt
 * to refactor out common code, but just different enough to make not feasible...
 * 
 * @author nbrown
 * 
 */
@Component
public class DisciplineActionContextResolver implements ContextResolver {
    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    @Autowired
    private StudentContextResolver studentResolver;
    
    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        Set<String> edOrgs = new HashSet<String>();

        List<String> studentIds = (List<String>) entity.getBody().get(ParameterConstants.STUDENT_ID);
        for(String studentId: studentIds){
            edOrgs.addAll(studentResolver.findGoverningEdOrgs(studentId, entity));
        }
        return edOrgs;
    }
}
