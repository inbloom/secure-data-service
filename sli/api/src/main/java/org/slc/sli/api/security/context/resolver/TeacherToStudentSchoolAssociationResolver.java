/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.security.context.resolver;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;

/**
 * Resolves which StudentSchoolAssociation a given teacher is allowed to see.
 */
@Component
public class TeacherToStudentSchoolAssociationResolver implements
        EntityContextResolver {
    private static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private AssociativeContextHelper helper;

    @Autowired
    private StudentGracePeriodNodeFilter graceFilter;



    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        return false;
    }

    @Override
    public List<String> findAccessible(Entity principal) {
        List<String> studentIds = helper.findAccessible(principal, Arrays.asList(
                ResourceNames.TEACHER_SECTION_ASSOCIATIONS, ResourceNames.STUDENT_SECTION_ASSOCIATIONS));
        List<String> associationIds = helper.findEntitiesContainingReference(EntityNames.STUDENT_SCHOOL_ASSOCIATION,
                "studentId", studentIds, Arrays.asList((NodeFilter) graceFilter));

        debug("Accessable student-school association IDS [ {} ]", associationIds);
        return associationIds;

    }

}
