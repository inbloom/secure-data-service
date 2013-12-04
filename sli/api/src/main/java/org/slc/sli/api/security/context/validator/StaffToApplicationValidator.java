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
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Resolves which applications any given staff member can access.
 * 
 * @author ldalgado
 *
 */
@Component
public class StaffToApplicationValidator extends AbstractContextValidator {

    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.APPLICATION.equals(entityType);
    }

    /**
     * The rule is you can see those applications that have YOUR edOrg in application.body.authorized_ed_orgs
     */
    @Override
    public Set<String> validate(String entityType, Set<String> ids) throws IllegalStateException {
        if (!areParametersValid(EntityNames.APPLICATION, entityType, ids)) {
            return Collections.emptySet();
        }

        Set<String> staffEdOrgs                        = edorgHelper.getStaffEdOrgsAndChildren();
        NeutralCriteria idInAppIdList                  = new NeutralCriteria("_id",                   NeutralCriteria.CRITERIA_IN, new LinkedList<String>(ids));
        NeutralCriteria staffEdOrgsInAuthorizedEdOrgs  = new NeutralCriteria("authorized_ed_orgs",    NeutralCriteria.CRITERIA_IN, staffEdOrgs);
        NeutralCriteria authorizedForAll               = new NeutralCriteria("allowed_for_all_edorgs",NeutralCriteria.OPERATOR_EQUAL, true);

        NeutralQuery p                                 = new NeutralQuery();
                                                         p.addCriteria(idInAppIdList);
                                                         p.addCriteria(authorizedForAll); 
        NeutralQuery q                                 = new NeutralQuery();
                                                         q.addCriteria(idInAppIdList);
                                                         q.addCriteria(staffEdOrgsInAuthorizedEdOrgs); 
        NeutralQuery finalQuery                        = new NeutralQuery();
                                                         finalQuery.addOrQuery(p);
                                                         finalQuery.addOrQuery(q);
        Iterable<String> myApplicationIds              = getRepo().findAllIds(EntityNames.APPLICATION, finalQuery);
        
        Set<String>  myApplicationIdsSet = new HashSet<String>();
        for(String appId: myApplicationIds) {
            myApplicationIdsSet.add(appId);
        }

        return  myApplicationIdsSet;

    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.STAFF_CONTEXT;
    }
}
