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

import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for walking student associations
 * 
 * @author nbrown
 *
 */
@Component
public class StudentAssociationWalker {
    private static final Logger LOG = LoggerFactory.getLogger(StudentAssociationWalker.class);

    @Autowired
    private StudentContextResolver studentResolver;
    
    public Set<String> walkStudentAssociations(List<Entity> studentAssociations) {
        Set<String> leas = new HashSet<String>();
        if (studentAssociations != null) {
            for(Entity studentAssociation: studentAssociations) {
                String studentId = (String) studentAssociation.getBody().get("studentId");
                if (studentId == null) {
                    LOG.warn("Student Section association without a student id: {}", studentAssociation);
                } else {
                    leas.addAll(studentResolver.findGoverningEdOrgs(studentId));
                }
            }
        }
        return leas;
    }


}