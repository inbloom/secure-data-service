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

package org.slc.sli.api.security.context.validator;

import com.google.common.collect.Sets;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates teacher's access to given teacherSchoolAssociation
 * This is 'through' validator, and it is invoked if there are
 * additional path-segments.
 * General rule is that you can see things through associations 
 * that have your id on them
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherToTeacherSchoolAssociationValidator extends AbstractContextValidator {

    @Resource
    private PagingRepositoryDelegate<Entity> repo;

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType) && isTeacher() && !isTransitive;
    }

	@Override
	public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.TEACHER_SCHOOL_ASSOCIATION, entityType, ids)) {
            return Collections.EMPTY_SET;
        }

        NeutralQuery nq = new NeutralQuery(new NeutralCriteria(ParameterConstants.TEACHER_ID,
                NeutralCriteria.OPERATOR_EQUAL, SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
        Iterable<String> it = this.repo.findAllIds(EntityNames.TEACHER_SCHOOL_ASSOCIATION, nq);
        
        Set<String> fin = Sets.newHashSet(it);
        fin.retainAll(ids);

        return fin;
    }

}
