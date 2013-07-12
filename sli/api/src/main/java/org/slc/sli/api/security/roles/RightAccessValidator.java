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
package org.slc.sli.api.security.roles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.ContextValidator;
import org.slc.sli.common.constants.EntityNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/** Class to validate if the context has the right to access the entity.
 *
 * @author: tke
 */
@Component
public class RightAccessValidator {

    private static final String ADMIN_SPHERE = "Admin";

    @Autowired
    EntityEdOrgRightBuilder entityEdOrgRightBuilder;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> securityRepo;

    /**
     * Validates that user roles allow access to fields.
     *
     * @param isRead  whether operation is "read" or "write"
     * @param isSelf  whether operation is being done in "self" context
     * @param entity item under inspection
     */
    public void checkAccess(boolean isRead, boolean isSelf, Entity entity, String entityType, Collection<GrantedAuthority> auths) {

        EntityBody body = null;
        if(entity != null) {
            body = new EntityBody(entity.getBody());
        }
        checkAccess(isRead, body, entityType, auths);
    }

    public void checkAccess(boolean isRead, String entityId, EntityBody content, String entityType, String collectionName, Repository<Entity> repo, Collection<GrantedAuthority> auths) {

        checkSecurity(isRead, entityId, entityType, collectionName, repo);
        checkAccess(isRead, content, entityType, auths);
    }

    /**
     * Validates that user roles allow access to fields, based on the provided authorities.
     *
     * @param isRead       whether operation is "read" or "write"
     * @param entityBody   the entity body to be checked.
     * @param entityType   entity type
     * @param auths        collection of authorities to validate
     */
    public void checkAccess(boolean isRead, EntityBody entityBody, String entityType, Collection<GrantedAuthority> auths) {
        checkAccess(isRead, entityBody, entityType, auths, null);
    }

    /**
     * Validates that user roles allow access to fields, based on the provided authorities.
     *
     * @param isRead       whether operation is "read" or "write"
     * @param entityBody   the entity body to be checked.
     * @param entityType   entity type
     * @param auths        collection of authorities to validate
     */
    public void checkAccess(boolean isRead, EntityBody entityBody, String entityType, Collection<GrantedAuthority> auths, Entity entity) {
        SecurityUtil.ensureAuthenticated();
        Set<Right> neededRights = new HashSet<Right>();

        info("Granted authorizies are :" ,  auths);
        boolean allow = false;
        if (auths.contains(Right.FULL_ACCESS)) {
            debug("User has full access");
            allow = true;
        } else if (ADMIN_SPHERE.equals(provider.getDataSphere(entityType))) {
            neededRights = new HashSet<Right>(Arrays.asList(Right.ADMIN_ACCESS));
            allow = intersection(auths, neededRights);
        } else if (!isRead) {
            debug("Evaluating rights for write...");
            if (entityBody == null) {
                neededRights.addAll(provider.getAllFieldRights(entityType, isRead));
                allow = intersection(auths, neededRights);
            } else {
                allow = determineWriteAccess(entityBody, "", auths, entityType);
            }
        } else if (isRead) {
            debug("Evaluating rights for read...");
            neededRights.addAll(provider.getAllFieldRights(entityType, isRead));
            allow = intersection(auths, neededRights);
        } else {
            throw new IllegalStateException("Unknown security validation path for Read/Write/Admin");
        }

        if (!allow) {
            throw new APIAccessDeniedException("Insufficient Privileges", entity);
        }
    }

    /**
     * Checks query params for access restrictions
     *
     * @param query   The query to check
     * @param isSelf  whether operation is being done in "self" context
     * @param entity item under inspection
     */
    public void checkFieldAccess(NeutralQuery query, boolean isSelf, Entity entity, String entityType, Collection<GrantedAuthority> auths) {

        if (query != null) {
            // get the authorities
            if (isSelf) {
                auths.addAll(SecurityUtil.getSLIPrincipal().getSelfRights());
            }

            try {
                checkFieldAccess(query, entityType, auths);
            } catch (APIAccessDeniedException e) {
                Set<Entity> entities = new HashSet<Entity>();
                entities.add(entity);

                // we know the target entity now so rethrow with target data
                e.setEntityType(entityType);
                e.setEntities(entities);
                throw e;
            }
        }
    }

    /**
     * Checks query params for access restrictions, based on provided authorities.
     *
     * @param query        the query to be checked.
     * @param entityType   the entity type
     * @param auths        collection of authorities to check.
     */
    public void checkFieldAccess(NeutralQuery query, String entityType, Collection<GrantedAuthority> auths) {
        if (!auths.contains(Right.FULL_ACCESS) && !auths.contains(Right.ANONYMOUS_ACCESS)) {

            if (null != query.getSortBy()) {
                Set<Right> rightsOnSort = getNeededRights(query.getSortBy(), entityType);

                if (!rightsOnSort.isEmpty() && !intersection(auths, rightsOnSort)) {
                    debug("Denied user sorting on field {}", query.getSortBy());
                    throw new APIAccessDeniedException("Cannot search on restricted field " + query.getSortBy());
                }
            }

            for (NeutralCriteria criteria : query.getCriteria()) {
                // get the needed rights for the field
                Set<Right> neededRights = getNeededRights(criteria.getKey(), entityType);

                if (!neededRights.isEmpty() && !intersection(auths, neededRights)) {
                    debug("Denied user searching on field {}", criteria.getKey());
                    throw new QueryParseException("Cannot search on restricted field", criteria.getKey());
                }
            }
        }
    }

    /**
     *  Get the authorities from the context
     *
     * @param isSelf       whether operation is being done in "self" context
     * @param entity       item under inspection
     * @param entityType   entity type
     *                     we need entityType because type within Entity can be different from its actual type(etc. localEducationAgency)
     *
     * @return a set of granted authorities
     */
    public Collection<GrantedAuthority> getContextualAuthorities(boolean isSelf, Entity entity, String entityType){
        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();

        SLIPrincipal principal = SecurityUtil.getSLIPrincipal();
        if (isSelf) {
            auths.addAll(principal.getSelfRights());
        }

        if (SecurityUtil.isStaffUser()) {
            if (entity == null) {
                debug("No authority for null");
            } else {

                if ((entity.getMetaData() != null && SecurityUtil.principalId().equals(entity.getMetaData().get("createdBy"))
                        && "true".equals(entity.getMetaData().get("isOrphaned")))
                        || EntityNames.isPublic(entityType)) {
                    // Orphaned entities created by the principal are handled the same as before.
                    auths.addAll(principal.getAllRights());
                } else {
                    auths.addAll(entityEdOrgRightBuilder.buildEntityEdOrgRights(principal.getEdOrgRights(), entity));
                }
            }
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            auths.addAll(auth.getAuthorities());
        }

        return auths;
    }

    /**
     * Returns the needed right for a field by examining the schema
     *
     * @param fieldPath The field name
     *
     * @return a set of rights needed.
     */
    public Set<Right> getNeededRights(String fieldPath, String entityType) {
        Set<Right> neededRights = provider.getRequiredReadLevels(entityType, fieldPath);

        if (ADMIN_SPHERE.equals(provider.getDataSphere(entityType))) {
            neededRights.add(Right.ADMIN_ACCESS);
        }

        return neededRights;
    }

    /**
     * Determines if there is an intersection of a single needed right within the user's collection
     * of granted authorities.
     *
     * @param authorities  User's collection of granted authorities.
     * @param neededRights Set of rights needed for accessing a given field.
     * @return True if the user can access the field, false otherwise.
     */
    public boolean intersection(Collection<GrantedAuthority> authorities, Set<Right> neededRights) {
        return neededRights.isEmpty() || !Collections.disjoint(authorities, neededRights);
    }

    @SuppressWarnings("unchecked")
    protected boolean determineWriteAccess(Map<String, Object> eb, String prefix, Collection<GrantedAuthority> auths, String entityType) {

        boolean allow = true;
        if (!ADMIN_SPHERE.equals(provider.getDataSphere(entityType))) {
            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                List<Object> list = null;
                if (value instanceof List) {
                    list = (List<Object>) value;
                } else {
                    list = Collections.singletonList(value);
                }

                for (Object obj : list) {
                    String fieldPath = prefix + fieldName;
                    Set<Right> neededRights = provider.getRequiredWriteLevels(entityType, fieldPath);

                    debug("Field {} requires {}", fieldPath, neededRights);

                    if (neededRights.isEmpty() && obj instanceof Map) {
                        allow &= determineWriteAccess((Map<String, Object>) obj, prefix + "." + fieldName + ".", auths, entityType);    // Mixing recursion and iteration, very bad
                    } else {
                        if (!intersection(auths, neededRights)) {
                            allow = false;
                            break;
                        }
                    }

                }
            }
        }

        return allow;
    }

    public void checkSecurity(boolean isRead, String entityId, String entityType, String collectionName, Repository<Entity> repo) {
        // Check that target entity actually exists
        if (securityRepo.findById(collectionName, entityId) == null) {
            warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }
        Set<Right> rights = provider.getAllFieldRights(entityType, isRead);
        if (rights.equals(new HashSet<Right>(Arrays.asList(Right.ANONYMOUS_ACCESS)))) {
            // Check that target entity is accessible to the actor
            if (entityId != null && !isEntityAllowed(entityId, collectionName, entityType, repo)) {
                throw new APIAccessDeniedException("No association between the user and target entity", entityType, entityId);
            }
        }
    }

    /**
     * Checks to see if the entity id is allowed by security
     *
     * @param entityId The id to check
     * @param collectionName the collection name
     * @param toType the entity type
     * @return
     */
    public boolean isEntityAllowed(String entityId, String collectionName, String toType, Repository<Entity> repo) {
        SecurityCriteria securityCriteria = new SecurityCriteria();
        NeutralQuery query = new NeutralQuery();
        query = securityCriteria.applySecurityCriteria(query);
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(entityId)));
        Entity found = repo.findOne(collectionName, query);
        return found != null;
    }
}
