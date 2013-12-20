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

package org.slc.sli.api.service;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.exceptions.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.SecurityEventContextResolver;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom service for accessing the security event entity from the database.
 *
 * @author Andrew D. Ball
 * @since 2013-11-22
 */
@Scope("prototype")
@Component
public class SecurityEventService extends BasicService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityEventService.class);

    @Autowired
    SecurityEventContextResolver securityEventContextResolver;

    public SecurityEventService(String collectionName, List<Treatment> treatments, Repository<Entity> repo) {
        super(collectionName, treatments, repo);
    }

    private void checkWhetherUserHasAccessToSecurityEvent(String securityEventId) {
        List<String> accessibleIds = securityEventContextResolver.findAccessible(null);
        if (!accessibleIds.contains(securityEventId)) {
            throw new APIAccessDeniedException("Access denied");
        }
    }

    /**
     * Generic function to list security events based on a NeutralQuery.
     *
     * This is mostly the same as the corresponding method in BasicService, except that
     * it does security checking differently, based on the SecurityEventContextResolver.
     *
     * @param neutralQuery
     * @return
     */
    @Override
    public Iterable<EntityBody> list(NeutralQuery neutralQuery) {
        neutralQuery.setSortBy("timeStamp");
        neutralQuery.setSortOrder(NeutralQuery.SortOrder.descending);

        return super.list(neutralQuery);
    }

    /**
     * Uses the {@link SecurityEventContextResolver} to add conditions to neutralQuery to
     * limit the security events which the current user can view.
     *
     * @param neutralQuery
     */
    @Override
    protected void listSecurityCheck(NeutralQuery neutralQuery) {
        Set<String> idsToFilter = new HashSet<String>();
        idsToFilter.addAll(securityEventContextResolver.findAccessible(null));

        // At present, multiple "in" queries do not get joined together properly by the Mongo Query converter.
        // Therefore, explicitly check for pre-existing "in" queries or "=" queries and combine them
        // here manually.

        boolean foundIdInQuery = false;
        for (NeutralCriteria criterion : neutralQuery.getCriteria()) {
            if ("_id".equals(criterion.getKey())) {
                foundIdInQuery = true;
                if (NeutralCriteria.CRITERIA_IN.equals(criterion.getOperator())) {
                    Set<String> values = new HashSet<String>();
                    for (String idValue : (Iterable<String>) criterion.getValue()) {
                        values.add(idValue);
                    }
                    values.retainAll(idsToFilter);
                    criterion.setValue(values);
                } else if (NeutralCriteria.OPERATOR_EQUAL.equals(criterion.getOperator())) {
                    Set<String> values = new HashSet<String>();
                    values.add((String) criterion.getValue());
                    values.retainAll(idsToFilter);
                    criterion.setOperator(NeutralCriteria.CRITERIA_IN);
                    criterion.setValue(values);
                } else {
                    throw new UnsupportedOperationException(
                            "do not know how to handle additional _id criterion with operator " +
                            criterion.getOperator());
                }
            }
        }

        if (!foundIdInQuery) {
            neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, idsToFilter));
        }
    }

    /**
     * This particular service doesn't really use the contextual roles system, with the list method
     * handling all that is needed for security checks through the SecurityEventContextResolver.
     *
     * However, {@link org.slc.sli.api.resources.generic.service.DefaultResourceService#getEntityBodies}
     * will use listBasedOnContextualRoles if SecurityUtil.isStaffUser() returns true, so we need to
     * have a suitable listBasedOnContextualRoles implementation as well.
     *
     * @param neutralQuery
     * @return
     */
    @Override
    public Iterable<EntityBody> listBasedOnContextualRoles(NeutralQuery neutralQuery) {
        return list(neutralQuery);
    }

    /**
     * Determines whether the current user can access the custom document associated with the
     * entity with a given id, without concerning itself with the client id of the application
     * the user is using.
     *
     * @param id
     * @return
     * @throws APIAccessDeniedException
     */
    @Override
    protected void getCustomSecurityCheck(String id) {
        checkWhetherUserHasAccessToSecurityEvent(id);
    }


    /**
     * Uses the SecurityEventContextResolver to determine whether the current user can delete the custom
     * document attached to the entity with a given id.
     *
     * @param id
     */
    @Override
    public void deleteCustomSecurityCheck(String id) {
        checkWhetherUserHasAccessToSecurityEvent(id);
    }

    /**
     * Uses the SecurityEventContextResolver to determine whether the current user can create or update
     * a custom document attached to the entity with a given id.
     *
     * @param id
     * @return
     */
    @Override
    public void createOrUpdateCustomSecurityCheck(String id, EntityBody customEntity) throws EntityValidationException {
        checkWhetherUserHasAccessToSecurityEvent(id);
    }

}
