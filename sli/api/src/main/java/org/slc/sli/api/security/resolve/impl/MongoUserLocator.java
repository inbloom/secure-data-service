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

package org.slc.sli.api.security.resolve.impl;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

/**
 * Attempts to locate a user in SLI mongo data-store
 *
 * @author dkornishev
 */
@Component
public class MongoUserLocator implements UserLocator {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;
    @Autowired
    private EdOrgHelper edorgHelper;

    @Override
    public SLIPrincipal locate(String tenantId, String externalUserId, String userType, String clientId) {
    	 info("Locating user {}@{} of type: {}", new Object[]{externalUserId, tenantId, userType});
         SLIPrincipal user = new SLIPrincipal(externalUserId + "@" + tenantId);
         user.setExternalId(externalUserId);
         user.setTenantId(tenantId);
         user.setUserType(userType);

         TenantContext.setTenantId(tenantId);

         if (EntityNames.STUDENT.equals(userType)) {
             NeutralQuery neutralQuery = new NeutralQuery(new NeutralCriteria(
                     ParameterConstants.STUDENT_UNIQUE_STATE_ID, NeutralCriteria.OPERATOR_EQUAL, externalUserId));
             neutralQuery.setOffset(0);
             neutralQuery.setLimit(1);
             user.setEntity(repo.findOne(EntityNames.STUDENT, neutralQuery, true));
         } else if (EntityNames.PARENT.equals(userType)) {
             NeutralQuery neutralQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_UNIQUE_STATE_ID, NeutralCriteria.OPERATOR_EQUAL, externalUserId));
             neutralQuery.setOffset(0);
             neutralQuery.setLimit(1);
             user.setEntity(repo.findOne(EntityNames.PARENT, neutralQuery, true));
         } else if (isStaff(userType)) {

             NeutralQuery neutralQuery = new NeutralQuery();
             neutralQuery.setOffset(0);
             neutralQuery.setLimit(1);
             neutralQuery.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_UNIQUE_STATE_ID,
                     NeutralCriteria.OPERATOR_EQUAL, externalUserId));

             Iterable<Entity> staff = repo.findAll(EntityNames.STAFF, neutralQuery);

             if (staff != null && staff.iterator().hasNext()) {
                 Entity entity = staff.iterator().next();
                 Set<String> edorgs = edorgHelper.locateDirectEdorgs(entity);
                 if (edorgs.size() == 0) {
                     warn("User {} is not currently associated to a school/edorg", user.getId());
                     throw new APIAccessDeniedException("User is not currently associated to a school/edorg", user, clientId);
                 }
                 user.setEntity(entity);
             }
         }

         if (user.getEntity() == null) {
             warn("Failed to locate user {} in the datastore", user.getId());
             Entity entity = new MongoEntity("user", "-133", new HashMap<String, Object>(),
                     new HashMap<String, Object>());
             user.setEntity(entity);
         } else {
             info("Matched user: {}@{} -> {}", new Object[]{externalUserId, tenantId, user.getEntity().getEntityId()});
         }

         return user;
    }
    
    @Override
    public SLIPrincipal locate(String tenantId, String externalUserId, String userType) {
    	return locate(tenantId, externalUserId, userType, null);
    }

    private boolean isStaff(String userType) {
        return userType == null || userType.isEmpty() || EntityNames.STAFF.equals(userType);
    }

    /**
     * Used by auto-wiring to set the entity repository.
     *
     * @param repo repository to be used by mongo user locator.
     */
    public void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }
}
