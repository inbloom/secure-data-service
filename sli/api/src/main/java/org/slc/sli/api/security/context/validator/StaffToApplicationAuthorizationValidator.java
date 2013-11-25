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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.*;

/**
 * Resolves which applications any given staff member can access.
 * 
 * @author ldalgado
 *
 */
@Component
public class StaffToApplicationAuthorizationValidator extends AbstractContextValidator {
    
    @Override
    public boolean canValidate(String entityType, boolean isTransitive) {
        return isStaff() && EntityNames.APPLICATION_AUTHORIZATION.equals(entityType);
    }
    
    /**
     * The rule is you can see those applications that have YOUR edOrg in application.body.authorized_ed_orgs. Then find the application authorization for those applications.
     */
    @Override
    public Set<String> validate(String entityType, Set<String> appAuthIds) throws IllegalStateException {
        if (!areParametersValid(EntityNames.APPLICATION_AUTHORIZATION, entityType, appAuthIds)) {
            return Collections.emptySet();
        }

        Set<String> staffEdOrgs                        = edorgHelper.getStaffEdOrgsAndChildren();
        NeutralCriteria staffEdOrgsInAuthorizedEdOrgs  = new NeutralCriteria("authorized_ed_orgs", NeutralCriteria.CRITERIA_IN, staffEdOrgs);
        NeutralCriteria authorizedForAll               = new NeutralCriteria("allowed_for_all_edorgs",NeutralCriteria.OPERATOR_EQUAL, true);

        NeutralQuery p                                 = new NeutralQuery();
                                                         p.addCriteria(authorizedForAll); 
        NeutralQuery q                                 = new NeutralQuery();
                                                         q.addCriteria(staffEdOrgsInAuthorizedEdOrgs);
        NeutralQuery finalQuery                        = new NeutralQuery();
        												finalQuery.addOrQuery(p);
        												finalQuery.addOrQuery(q);

        Iterable<String> myApplicationIds              = getRepo().findAllIds(EntityNames.APPLICATION, finalQuery);
        Set<String> myAppsList                         = new HashSet<String>();
        if(myApplicationIds != null) {
        	for(String appId: myApplicationIds) {
        		myAppsList.add(appId);
        	}
        }

        NeutralCriteria idInAppAuthIdList              = new NeutralCriteria("_id",                 NeutralCriteria.CRITERIA_IN, new LinkedList<String>(appAuthIds));
        NeutralCriteria aAuthappIdInMyAppList          = new NeutralCriteria("applicationId",       NeutralCriteria.CRITERIA_IN, new LinkedList<String>(myAppsList));
        q                                              = new NeutralQuery();
        q.addCriteria(idInAppAuthIdList);
        q.addCriteria(aAuthappIdInMyAppList);
        Iterable<String> myAppAuths                    = getRepo().findAllIds(EntityNames.APPLICATION_AUTHORIZATION, q);

        if(myAppAuths == null) {
            return Collections.emptySet();
        } else {
            Set<String>  myApplicationAuthIdsSet = Sets.newHashSet(myAppAuths);
            return  myApplicationAuthIdsSet;
        }
    }

    @Override
    public SecurityUtil.UserContext getContext() {
        return SecurityUtil.UserContext.STAFF_CONTEXT;
    }
}