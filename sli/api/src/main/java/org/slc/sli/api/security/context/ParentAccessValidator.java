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
package org.slc.sli.api.security.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ResourceNames;

@Component
public class ParentAccessValidator extends AccessValidator {

    @Autowired
    private StudentAccessValidator studentAccessValidator;

    /*
     * only difference between student and parent
     * allow:
     * /parents/{id}/studentParentAssociations
     * /parents/{id}/studentParentAssociations/students
     * 
     * deny:
     * /students/{id}/studentParentAssociations
     * /students/{id}/studentParentAssociations/parents
     */
    private static final Set<List<String>> ALLOWED_DELTA;
    private static final Set<List<String>> DENIED_DELTA;

    static {
        Set<List<String>> allowed_delta = new HashSet<List<String>>();
        allowed_delta.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS));
        allowed_delta.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, ResourceNames.STUDENTS));
        ALLOWED_DELTA = Collections.unmodifiableSet(allowed_delta);
        
        Set<List<String>> denied_delta = new HashSet<List<String>>();
        denied_delta.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS));
        denied_delta.add(Arrays.asList(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, ResourceNames.PARENTS));
        DENIED_DELTA = Collections.unmodifiableSet(denied_delta);
    }

    /**
     * check if a path can be accessed according to stored business rules
     * 
     * @param
     *        List<String> paths url segments in string without version
     * @param
     *        MultivaluedMap<String, String> query Paramaters
     * 
     * @return true if accessible by parent
     */
    @Override
    protected boolean isReadAllowed(List<String> path, MultivaluedMap<String, String> queryParameters) {
        List<String> subPath = null;
        if (path.size() == 3) {
            subPath = Arrays.asList(path.get(2));
        } else if (path.size() == 4) {
            subPath = Arrays.asList(path.get(2), path.get(3));
        }
        
        if (subPath != null && path.get(0).equals(ResourceNames.PARENTS) && ALLOWED_DELTA.contains(subPath)) {
            return true;
        }
        
        if (subPath != null && path.get(0).equals(ResourceNames.STUDENTS) && DENIED_DELTA.contains(subPath)) {
            return false;
        }
        
        return studentAccessValidator.isReadAllowed(path, queryParameters);
    }
    
    @Override
    protected boolean isWriteAllowed(List<String> path) {
        return false;
    }
    
}
