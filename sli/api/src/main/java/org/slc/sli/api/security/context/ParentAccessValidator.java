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

import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    final Pattern parentToAssociations = Pattern.compile("^parents/[0-9a-f]{40}_id/studentParentAssociations$");
    final Pattern parentToAssociationsStudents = Pattern.compile("^parents/[0-9a-f]{40}_id/studentParentAssociations/students$");
    final Pattern studentToAssociations = Pattern.compile("^students/[0-9a-f]{40}_id/studentParentAssociations$");
    final Pattern studentToAssociationsStudents = Pattern.compile("^students/[0-9a-f]{40}_id/studentParentAssociations/students$");
    
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
        String url = StringUtils.join(path, "/");
        if (parentToAssociations.matcher(url).matches() || parentToAssociationsStudents.matcher(url).matches()) {
            return true;
        }
        
        if (studentToAssociations.matcher(url).matches() || studentToAssociationsStudents.matcher(url).matches()) {
            return false;
        }
        
        return studentAccessValidator.isReadAllowed(path, queryParameters);
    }
    
    @Override
    protected boolean isWriteAllowed(List<String> path) {
        return false;
    }
    
}
