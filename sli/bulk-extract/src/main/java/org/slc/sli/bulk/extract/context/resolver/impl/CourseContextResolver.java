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
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

@Component
public class CourseContextResolver extends EdOrgRelatedReferrableResolver {
    
    public static final String COURSE_ID = "courseId";

    @Autowired
    private CourseOfferingContextResolver courseOfferingResolver;

    @Override
    protected String getCollection() {
        return EntityNames.COURSE;
    }
    
    @Override
    protected Set<String> getTransitiveAssociations(Entity entity) {
        Set<String> leas = new HashSet<String>();
        // follow all the courseOfferings
        String courseId = entity.getEntityId();
        if (courseId != null) {
            Iterable<Entity> courseOfferings = getRepo().findAll(EntityNames.COURSE_OFFERING,
                    new NeutralQuery(new NeutralCriteria(COURSE_ID, NeutralCriteria.OPERATOR_EQUAL, courseId)));
            for (Entity courseOffering : courseOfferings) {
                leas.addAll(courseOfferingResolver.findGoverningLEA(courseOffering));
            }
        }

        return leas;
    }

}
