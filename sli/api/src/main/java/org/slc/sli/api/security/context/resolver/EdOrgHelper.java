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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
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
	private AssociativeContextHelper helper;

	@Autowired
    private StaffEdOrgEdOrgIDNodeFilter staffEdOrgEdOrgIDNodeFilter;

	/**
	 * Traverse the edorg hierarchy and find all the SEAs the user is associated with, directly or indirectly.
	 *
	 * @param user
	 * @return a list of entity IDs
	 */
	public List<String> getSEAs(Entity user) {
		List<String> directAssoc = getDirectEdOrgAssociations(user);
		NeutralQuery query = new NeutralQuery(0);
		query.addCriteria(new NeutralCriteria("_id", "in", directAssoc, false));

		Set<String> entities = new HashSet<String>();
		for (Entity entity : repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query)) {
			entities.add(getSEAOfEdOrg(entity));
		}

		return new ArrayList<String>(entities);
	}

	/**
	 * Determine the district of the user.
	 *
	 * If the user is directly associated with an SEA, this is any LEA directly below the SEA. If the user is directly
	 * associated with an LEA, this is the top-most LEA i.e. the LEA directly associated with the SEA.
	 *
	 * @param user
	 * @return a list of entity IDs
	 */
	public List<String> getDistricts(Entity user) {
		List<String> directAssoc = getDirectEdOrgAssociations(user);
		NeutralQuery query = new NeutralQuery(0);
		query.addCriteria(new NeutralCriteria("_id", "in", directAssoc, false));

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
    public List<String> getParentEdOrgs(Entity edOrg) {
        List<String> toReturn = new ArrayList<String>();
        if (edOrg != null && edOrg.getBody() != null) {
            while (edOrg.getBody().get("parentEducationAgencyReference") != null) {
                String parentId = (String) edOrg.getBody().get("parentEducationAgencyReference");
                toReturn.add(parentId);
                edOrg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, parentId);
            }
        }
        return toReturn;
    }

    public List<String> getDirectEdOrgAssociations(Entity principal) {
        List<String> ids = new ArrayList<String>();
        if (isTeacher(principal)) {
            ids.addAll(helper.findAccessible(principal, Arrays.asList(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS)));
        } else {
            ids.addAll(helper.findAccessible(principal,
                    Arrays.asList(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS)));
        }
        return ids;
    }

	/**
     * Walks the edorg hierarchy to get all schools
     *
     * @param principal
     * @return
     */
    public List<String> getUserSchools(Entity principal) {
        List<String> schools = new ArrayList<String>();

        // Get direct associations
        List<String> ids = getDirectEdOrgAssociations(principal);

        // get edorg entities
        while (!ids.isEmpty()) {
            NeutralQuery nq = new NeutralQuery();
            nq.addCriteria(new NeutralCriteria("parentEducationAgencyReference", "in", ids));
            Iterable<Entity> childEdorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, nq);

            ids.clear();
            for (Entity e : childEdorgs) {
                if (isSchool(e)) {
                    schools.add(e.getEntityId());
                } else {
                    ids.add(e.getEntityId());
                }
            }
        }

        return schools;
    }
    

    /**
     * Finds schools directly associated to this user
     * @param principal
     * @return
     */
    public List<String> getDirectSchools(Entity principal) {
        List<String> ids = getDirectEdOrgAssociations(principal);
        Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery(
                new NeutralCriteria("_id", "in", ids, false)));
        
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
	 * @param edOrgs
	 * @return
	 */
    public Set<String> getChildEdOrgs(Set<String> edOrgs) {
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
        Entity parentEdorg = repo.findById(EntityNames.EDUCATION_ORGANIZATION,
                (String) entity.getBody().get("parentEducationAgencyReference"));
        if (isLEA(parentEdorg)) {
            return getTopLEAOfEdOrg(parentEdorg);
        } else { // sea
            return entity;
        }
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



    public Collection<String> getUserEdOrgs(Entity principal) {
        return (isTeacher(principal)) ? getDirectSchools(principal) : getStaffEdOrgLineage(principal);
    }

     /**
     * Will go through staffEdorgAssociations that are current and get the descendant
     * edorgs that you have.
     *
     * @return a set of the edorgs you are associated to and their children.
     */
    public Set<String> getStaffEdOrgLineage(Entity principal) {
        Set<String> edOrgLineage = getStaffCurrentAssociatedEdOrgs(principal);
        edOrgLineage.addAll(getChildEdOrgs(edOrgLineage));
        return edOrgLineage;
    }

    public Set<String> getStaffCurrentAssociatedEdOrgs(Entity principal) {
        NeutralQuery basicQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE,
                NeutralCriteria.OPERATOR_EQUAL, principal.getEntityId()));
        Iterable<Entity> staffEdOrgs = repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, basicQuery);
        List<Entity> staffEdOrgAssociations = new LinkedList<Entity>();
        if (staffEdOrgs != null) {
            for (Entity staffEdOrg : staffEdOrgs) {
                staffEdOrgAssociations.add(staffEdOrg);
            }
        }
        List<Entity> currentStaffEdOrgAssociations = staffEdOrgEdOrgIDNodeFilter.filterEntities(staffEdOrgAssociations, null);
        Set<String> edOrgIds = new HashSet<String>();
        for (Entity association : currentStaffEdOrgAssociations) {
            edOrgIds.add((String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE));
        }
        return edOrgIds;
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
    public boolean isSEA(Entity entity) {
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");

        if (category.contains("State Education Agency")) {
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

    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }

}
