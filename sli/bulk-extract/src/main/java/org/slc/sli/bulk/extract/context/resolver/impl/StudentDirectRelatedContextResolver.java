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
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;

/**
 * This resolver can resolve an entity that is directly related to a student.
 * 
 * To be considered directly related to students, there are two constraints:
 * 1. the entity must have a "body.studentId" field
 * 2. the business rule for visibility for those entities must be: all for all current students
 * 
 * @author ycao
 * 
 */
@Component
public class StudentDirectRelatedContextResolver implements ContextResolver {
    
    @Autowired
    private StudentContextResolver studentContextResolver;
    
    private final static String STUDENT_ID = "studentId";
    
    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        if (entity.getBody() == null) {
            return Collections.emptySet();
        }
        
        String studentId = (String) entity.getBody().get(STUDENT_ID);
        if (studentId == null) {
            return Collections.emptySet();
        }
        
        return studentContextResolver.getLEAsForStudentId(studentId);
    }
    
}
