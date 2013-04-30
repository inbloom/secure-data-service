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
import java.util.Set;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.domain.Entity;

/**
 * Context resolver for students
 * 
 * @author nbrown
 *
 */
public class StudentContextResolver implements ContextResolver {
    
    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Find the LEAs based on a given student id
     * Will use local cache if available, otherwise will pull student from Mongo
     * 
     * @param id the ID of the student
     * @return the set of LEAs
     */
    public Set<String> getLEAsForStudentId(String id){
        return new HashSet<String>();
    }
    
}
