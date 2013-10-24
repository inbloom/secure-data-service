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
package org.slc.sli.domain.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Common abstraction to retrieve the EducationOrganization's hierarchy
 * 
 * @author ycao
 * 
 */
public class EdOrgHierarchyHelper {
    private static final Logger LOG = LoggerFactory.getLogger(EdOrgHierarchyHelper.class);

    /*
     * This class is not spring managed bean because the repo required for
     * mongo queries are different depending on the situation, and I don't want to define
     * a lot of them in the xml configuration...
     * 
     * The repo must be passed in from the beans managed by spring. This is
     * a POJO
     */

    static final String SEA_CATEGORIES = "State Education Agency";

    private Repository<Entity> repo;
    
    public EdOrgHierarchyHelper(Repository<Entity> repo) {
        this.repo = repo;
    }
    
    /**
     * Determine if this edorg is a SEA
     * 
     * @param entity
     * @return boolean
     */
    public boolean isSEA(Entity entity) {
        return isType(SEA_CATEGORIES, entity);
    }
    
    /**
     * Determine if this edorg is a LEA
     * 
     * @param entity
     * @return boolean
     */
    public boolean isLEA(Entity entity) {
        return isType("Local Education Agency", entity);
    }
    
    /**
     * Determine if this edorg is a school
     * 
     * @param entity
     * @return boolean
     */
    public boolean isSchool(Entity entity) {
        return isType("School", entity);
    }
    
    @SuppressWarnings("unchecked")
    private boolean isType(String type, Entity entity) {
        if (entity == null) {
            return false;
        }
        
        List<String> category = (List<String>) entity.getBody().get("organizationCategories");
        
        if (category != null && category.contains(type)) {
            return true;
        }

        return false;
    }

    // TODO this logic will need to support multiple parentIds - see us5821
    private List<Entity> getParentEdOrg(Entity entity) {
        if (entity.getBody().containsKey("parentEducationAgencyReference")) {
            @SuppressWarnings("unchecked")
            List<String> parentIds = (List<String>) entity.getBody().get("parentEducationAgencyReference");
            List<Entity> parents = new ArrayList<Entity>();
            if(parentIds!=null) {
                for(String parentId: parentIds) {
                    if(parentId!=null) {
                        Entity parent = repo.findById(EntityNames.EDUCATION_ORGANIZATION, parentId);
                        if(parent!=null) {
                            parents.add(parent);
                        }
                    }
                }
            return parents;
            }
        }
        return null;
    }
    
    /**
     * Given an school or LEA level entity, returns the top LEA it belongs to
     * 
     * if input is SEA, returns null
     * 
     * @param entity
     * @return top level LEA
     */
    public List<Entity> getTopLEAOfEdOrg(Entity entity) {
    	List<Entity> topLEAs = new ArrayList<Entity>();
        if (entity.getBody().containsKey("parentEducationAgencyReference")) {
            List<Entity> parentEdorgs = getParentEdOrg(entity);
            if(parentEdorgs!=null) {
                for(Entity parentEdorg: parentEdorgs) {
                    if (isLEA(parentEdorg)) {
                        List<Entity> leas = getTopLEAOfEdOrg(parentEdorg);
                        if(leas!=null) {
                            topLEAs.addAll(leas);
                        }
                    }
                }
            }
            if(!topLEAs.isEmpty()) {
            	return topLEAs;
            }
        }
        
        if (isLEA(entity)) {
        	topLEAs.add(entity);
        	return topLEAs;
        }
        
        return null;
    }

    /**
     * Given an school or LEA level entity, returns all ancestor edOrgs it belongs to (excluding SEAs and including the passed egOrg itself)
     *
     * If input is SEA, returns null.
     *
     * @param entity
     * @return Ancestors
     */
    public Set<Entity> getAncestorsOfEdOrg(Entity entity) {
        if (isSEA(entity)) {
            return null;
        }

        // using a set of visited edOrg id's to avoid any issues that might occur with hashCode on Entity
        Set<String> visitedIds = new HashSet<String>();
        Set<Entity> ancestors = new HashSet<Entity>();

        List<Entity> stack = new ArrayList<Entity>(10);
        // we are intentionally including the original edOrg -- not doing so breaks the bulk extract in delta mode
        ancestors.add(entity);
        stack.add(entity);

        while (!stack.isEmpty()) {
            Entity cur = stack.remove(stack.size() - 1);

            // don't let cycles cause us to loop forever
            if (visitedIds.contains(cur.getEntityId())) {
                continue;
            } else {
                visitedIds.add(cur.getEntityId());
            }

            if (cur.getBody().containsKey("parentEducationAgencyReference")) {
                List<Entity> parentEdOrgs = getParentEdOrg(cur);
                if (parentEdOrgs != null) {
                    for (Entity parent:parentEdOrgs) {
                        stack.add(parent);
                        // do not include SEA in the list of ancestors returned.
                        if (!isSEA(parent)) {
                            ancestors.add(parent);
                        }
                    }
                }
            }
        }

        return ancestors;
    }

    /**
     * Given an edorg entity, returns the SEA it belongs to
     * 
     * @param entity
     * @return SEA
     */
    public String getSEAOfEdOrg(Entity entity) {
        if (isSEA(entity)) {
            return entity.getEntityId();
        } else {
            List<Entity> parentEdorgs = getParentEdOrg(entity);
            if(parentEdorgs!=null) {
            	for(Entity parentEdorg: parentEdorgs) {
            		String sea = getSEAOfEdOrg(parentEdorg);
            		if(sea != null) {
            			return sea;
            		}
            	}
            }         
        }
        LOG.warn("EdOrg {} is missing parent SEA", entity.getEntityId());
        return null;
    }

    public String getSEAId() {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(ParameterConstants.ORGANIZATION_CATEGORIES, NeutralCriteria.OPERATOR_EQUAL, SEA_CATEGORIES));

        Entity sea = repo.findOne(EntityNames.EDUCATION_ORGANIZATION, query);
        if (sea == null) {
            return null;
        }
        return sea.getEntityId();
    }
}
