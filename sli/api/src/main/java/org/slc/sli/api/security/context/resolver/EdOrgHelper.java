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

package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.security.context.EntityOwnershipValidator;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.validator.DateHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Contains helper methods for traversing the edorg hierarchy.
 *
 * Assumptions it makes
 *
 * <ul>
 * <li>SEAs, LEAs, and Schools are all edorgs with organizationCategories of 'State Education
 * Agency' 'Local Education Agency', and 'School' respectively.</li>
 * <li>The parentEducationAgencyReference of a school always points to an LEA</li>
 * <li>The parentEducationAgencyReference of an LEA can point to either an SEA or another LEA</li>
 * <li>SEAs don't have a parentEducationAgencyReference and therefore are always at the top of the
 * tree</li>
 * </ul>
 *
 *
 */
@Component
public class EdOrgHelper {

    @Autowired
    protected PagingRepositoryDelegate<Entity> repo;

    @Autowired
    protected DateHelper dateHelper;

    @Autowired
    protected EntityOwnershipValidator ownership;

    /**
     * Determine the district of the user.
     *
     * If the user is directly associated with an SEA, this is any LEA directly below the SEA. If
     * the user is directly
     * associated with an LEA, this is the top-most LEA i.e. the LEA directly associated with the
     * SEA.
     *
     * @param user
     * @return a list of entity IDs
     */
    public List<String> getDistricts(Entity user) {
        Set<String> directAssoc = getDirectEdorgs(user);
        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, directAssoc, false));

        Set<String> entities = new HashSet<String>();
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (isLEA(entity)) {
                entities.add(getTopLEAOfEdOrg(entity).getEntityId());
            } else if (isSchool(entity)) {
                entities.add(getTopLEAOfEdOrg(entity).getEntityId());
            } else { // isSEA
                entities.addAll(getChildLEAsOfEdOrg(entity));
            }
        }

        return new ArrayList<String>(entities);
    }

    public List<String> getChildLEAsOfEdOrg(Entity edorgEntity) {
        List<String> toReturn = new ArrayList<String>();
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "=", edorgEntity.getEntityId()));
        for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
            if (isLEA(entity)) {
                toReturn.add(entity.getEntityId());

            }
        }

        return toReturn;
    }

    /**
     * Get an ordered list of the parents of an edorg.
     *
     * The order of the list starts with the direct parent of the edorg and ends with the SEA
     *
     * @param edOrg
     * @return
     */
    public List<String> getParentEdOrgs(final Entity edOrg) {
        List<String> toReturn = new ArrayList<String>();

        Entity currentEdOrg = edOrg;

        if (currentEdOrg != null && currentEdOrg.getBody() != null) {
            while (currentEdOrg.getBody().get("parentEducationAgencyReference") != null) {
                String parentId = (String) currentEdOrg.getBody().get("parentEducationAgencyReference");
                toReturn.add(parentId);
                currentEdOrg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, parentId);
            }
        }
        return toReturn;
    }

    public Entity byId(String edOrgId) {
        return repo.findById(EntityNames.EDUCATION_ORGANIZATION, edOrgId);
    }

    /**
     * Finds schools directly associated to this user
     *
     * @param principal
     * @return
     */
    public List<String> getDirectSchools(Entity principal) {
        Set<String> ids = getDirectEdorgs(principal);
        Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(
                new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, ids, false)));

        List<String> schools = new ArrayList<String>();
        for (Entity e : edorgs) {
            if (isSchool(e)) {
                schools.add(e.getEntityId());
            }
        }

        return schools;
    }

    /**
     * Recursively returns the list of all child edorgs
     *
     * @param edOrgs
     * @return
     */
    public Set<String> getChildEdOrgs(Collection<String> edOrgs) {
        if (edOrgs.isEmpty()) {
            return new HashSet<String>();
        }

        NeutralQuery query = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, edOrgs));
        Iterable<Entity> childrenIds = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
        Set<String> children = new HashSet<String>();
        for (Entity child : childrenIds) {
            children.add(child.getEntityId());
        }
        if (!children.isEmpty()) {
            children.addAll(getChildEdOrgs(children));
        }
        return children;
    }

    private Entity getTopLEAOfEdOrg(Entity entity) {
        if (entity.getBody().containsKey("parentEducationAgencyReference")) {
            Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION,
                    (String) entity.getBody().get("parentEducationAgencyReference"));
            if (isLEA(parentEdorg)) {
                return getTopLEAOfEdOrg(parentEdorg);
            }
        }
        return entity;
    }

    private String getSEAOfEdOrg(Entity entity) {
        if (isSEA(entity)) {
            return entity.getEntityId();
        } else {
            Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION,
                    (String) entity.getBody().get("parentEducationAgencyReference"));
            if (parentEdorg != null) {
                return getSEAOfEdOrg(parentEdorg);
            } else {
                warn("EdOrg {} is missing parent SEA", entity.getEntityId());
                return null;
            }
        }
    }

    /**
     * Get the collection of ed-orgs that will determine a user's security context
     *
     * @param principal
     * @return
     */
    public Collection<String> getUserEdOrgs(Entity principal) {
        return (isTeacher(principal)) ? getDirectSchools(principal) : getStaffEdOrgsAndChildren();
    }

    /**
     * Will go through staffEdorgAssociations that are current and get the descendant
     * edorgs that you have.
     *
     * @return a set of the edorgs you are associated to and their children.
     */
    public Set<String> getStaffEdOrgsAndChildren() {
        Set<String> edOrgLineage = getDirectEdorgs();
        return getEdorgDescendents(edOrgLineage);
    }

    public Set<String> getEdorgDescendents(Set<String> edOrgLineage) {
        edOrgLineage.addAll(getChildEdOrgs(edOrgLineage));
        return edOrgLineage;
    }

    /**
     * Calls date helper to check whether the specified field on the input body is expired.
     *
     * @param body
     *            Map representing entity's body.
     * @param fieldName
     *            Name of field to extract from entity's body.
     * @param useGracePeriod
     *            Flag indicating whether to allow for grace period when determining expiration.
     * @return True if field is expired, false otherwise.
     */
    public boolean isFieldExpired(Map<String, Object> body, String fieldName, boolean useGracePeriod) {
        return dateHelper.isFieldExpired(body, fieldName, useGracePeriod);
    }

    @SuppressWarnings("unchecked")
    public boolean isSEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("State Education Agency")) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isLEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("Local Education Agency")) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isSchool(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("School")) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the specified principal is of type 'teacher'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'teacher', false otherwise.
     */
    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

    /**
     * Determines if the specified principal is of type 'staff'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'staff', false otherwise.
     */
    private boolean isStaff(Entity principal) {
        return principal.getType().equals(EntityNames.STAFF);
    }

    /**
     * Determines if the specified principal is of type 'student'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'student', false otherwise.
     */
    private boolean isStudent(Entity principal) {
        return principal.getType().equals(EntityNames.STUDENT);
    }

    /**
     * Determines if the specified principal is of type 'parent'.
     *
     * @param principal
     *            Principal to check type for.
     *
     * @return True if the principal is of type 'parent', false otherwise.
     */
    private boolean isParent(Entity principal) {
        return principal.getType().equals(EntityNames.PARENT);
    }

    /**
     * Get directly associated education organizations for the authenticated principal.
     */
    public Set<String> getDirectEdorgs() {
        return getDirectEdorgs(SecurityUtil.getSLIPrincipal().getEntity());
    }

    /**
     * Get directly associated education organizations for the specified principal, filtered by
     * data ownership.
     */
    public Set<String> getDirectEdorgs(Entity principal) {
        if (isStaff(principal) || isTeacher(principal)) {
            return getStaffDirectlyAssociatedEdorgs(principal, true);
        } else if (isStudent(principal)) {
            return getStudentsCurrentAssociatedEdOrgs(Collections.singleton(principal.getEntityId()), true);
        } else if (isParent(principal)) {
            // will need logic to get student -> parent associations
            // assemble set of students that parent can see
            // -> call getStudentCurrentAssociatedEdOrgs(Set<String> studentIds)
        }

        return new HashSet<String>();
    }

    /**
     * Get current education organizations for the specified staff member.
     */
    private Set<String> getStaffDirectlyAssociatedEdorgs(Entity staff, boolean filterByOwnership) {
        Set<String> edorgs = new HashSet<String>();
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, staff.getEntityId()));
        Iterable<Entity> associations = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        for (Entity association : associations) {
            if (!filterByOwnership || ownership.canAccess(association)) {
                if (!dateHelper.isFieldExpired(association.getBody(), ParameterConstants.END_DATE, false)) {
                    edorgs.add((String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
                }
            }
        }
        return edorgs;
    }

    /**
     * Get current education organizations for the specified students.
     */
    private Set<String> getStudentsCurrentAssociatedEdOrgs(Set<String> studentIds, boolean filterByOwnership) {
        Set<String> edOrgIds = new HashSet<String>();

        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STUDENT_ID,
                NeutralCriteria.CRITERIA_IN, studentIds));
        Iterable<Entity> associations = repo.findAll(EntityNames.STUDENT_SCHOOL_ASSOCIATION, basicQuery);

        if (associations != null) {
            for (Entity association : associations) {
                if (!filterByOwnership || ownership.canAccess(association)) {
                    if (!isFieldExpired(association.getBody(), ParameterConstants.EXIT_WITHDRAW_DATE, false)) {
                        edOrgIds.add((String) association.getBody().get(ParameterConstants.SCHOOL_ID));
                    }
                }
            }
        }
        return edOrgIds;
    }

    /**
     * Set the entity ownership validator (used primarily for unit testing).
     */
    protected void setEntityOwnershipValidator(EntityOwnershipValidator newOwner) {
        this.ownership = newOwner;
    }
}
