/**
 * 
 */
package org.slc.sli.api.security.context.validator;

/**
 * @author ldalgado
 *
 */

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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Validates Write context to a global bellSchedule.
 */
@Component
public class GenericToGlobalBellScheduleWriteValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isTransitive && EntityNames.BELL_SCHEDULE.equals(entityType) && !isStudentOrParent();
    }

    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.BELL_SCHEDULE, entityType, ids)) {
            return Collections.emptySet();
        }

        Set<String> edOrgLineage = getEdorgDescendents(getDirectEdorgs());

        NeutralQuery classPeriodsQuery = new NeutralQuery();
        classPeriodsQuery.addCriteria(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_ID, NeutralCriteria.CRITERIA_IN, edOrgLineage));
        List<String> myClassPeriods = Lists.newArrayList(getRepo().findAllIds("classPeriod", classPeriodsQuery));

        NeutralQuery bellSchedulesQuery = new NeutralQuery();
        bellSchedulesQuery.addCriteria(new NeutralCriteria("meetingTime.classPeriodId", NeutralCriteria.CRITERIA_IN, myClassPeriods));
        bellSchedulesQuery.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids, false));
        Set<String> myBellSchedules = Sets.newHashSet(getRepo().findAllIds("bellSchedule", bellSchedulesQuery));

        return myBellSchedules;
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.DUAL_CONTEXT;
    }

}
