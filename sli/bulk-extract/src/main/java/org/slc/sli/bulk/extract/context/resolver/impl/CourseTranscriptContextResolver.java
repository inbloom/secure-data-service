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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Course Transcript resolver
 * 
 * @author ycao
 * 
 */
@Component
public class CourseTranscriptContextResolver implements ContextResolver {
    
    public static final String ED_ORG_REFERENCE = "educationOrganizationReference";
    public static final String STUDENT_ACADEMIC_RECORD_ID = "studentAcademicRecordId";
    public static final String STUDENT_ID = "studentId";

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    
    @Autowired
    StudentContextResolver studentResolver;

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        Set<String> edOrgs = new HashSet<String>();

        if (entity == null) {
            return edOrgs;
        }

        //Get edOrgs based on studentId in the body
        String studentId = (String) entity.getBody().get(STUDENT_ID);
        if (studentId != null) {
            edOrgs.addAll(studentResolver.findGoverningEdOrgs(studentId, entity));
        }

        //Get edOrgs based on the referenced studentAcademicRecord
        String studentAcademicRecordId = (String) entity.getBody().get(STUDENT_ACADEMIC_RECORD_ID);
        if (studentAcademicRecordId != null) {
            Entity studentAcademicRecord = repo.findById(EntityNames.STUDENT_ACADEMIC_RECORD, studentAcademicRecordId);
            if (studentAcademicRecord != null) {
                  edOrgs.addAll(studentResolver.findGoverningEdOrgs((String) studentAcademicRecord.getBody().get(STUDENT_ID), entity));
              }
            }

        return edOrgs;
    }

    @Override
    public Set<String> findGoverningEdOrgs(String id, Entity entityToExtract) {
        return null;
    }

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity, Entity entityToExtract) {
        return null;
    }
    
}
