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

package org.slc.sli.api.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * A simple initializing bean to initialize our Mongo instance with default roles.
 * 
 * IMPORTANT: If you add new SLI Administrative roles, make sure you set the admin flag to true.
 * Failure to do so can introduce a large security hole.
 * 
 * @author rlatta
 */
@Component
public class RoleInitializer {
    public static final String EDUCATOR = "Educator";
    public static final String AGGREGATE_VIEWER = "Aggregate Viewer";
    public static final String IT_ADMINISTRATOR = "IT Administrator";
    public static final String LEADER = "Leader";
    public static final String ROLES = "roles";
    
    public static final String LEA_ADMINISTRATOR = "LEA Administrator";
    public static final String SEA_ADMINISTRATOR = "SEA Administrator";
    public static final String APP_DEVELOPER = "Application Developer";
    public static final String SLC_OPERATOR = "SLC Operator";
    public static final String REALM_ADMINISTRATOR = "Realm Administrator";
    public static final String INGESTION_USER = "Ingestion User";
    public static final String SANDBOX_SLC_OPERATOR = "Sandbox SLC Operator";
    public static final String SANDBOX_ADMINISTRATOR = "Sandbox Administrator";
    
    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repository;
    
    public void dropAndBuildRoles(String tenantId, String realmId) {
        dropRoles(tenantId, realmId);
        buildRoles(realmId);
    }
    
    private void dropRoles(String tenantId, String realmId) {
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", tenantId, false));
        query.addCriteria(new NeutralCriteria("realmId", "=", realmId));
        
        Entity entity = repository.findOne(ROLES, query);
        if (entity != null) {
            repository.delete(ROLES, entity.getEntityId());
            info("Successfully dropped roles from realm: {}", new Object[] {realmId});
        } else {
            info("No roles exist to drop for realm: {}", new Object[] {realmId});
        }
    }
    
    public int buildRoles(String realmId) {
        Map<String, Object> rolesBody = new HashMap<String, Object>();
        rolesBody.put("realmId", realmId);
        
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        groups.add(buildRoleGroup(buildAggregate()));
        groups.add(buildRoleGroup(buildLeader()));
        groups.add(buildRoleGroup(buildIT()));
        groups.add(buildRoleGroup(buildEducator()));
        rolesBody.put("roles", groups);
        rolesBody.put("customRights", new ArrayList<String>());
        
        repository.create(ROLES, rolesBody);        
        info("Successfully built roles for realm: {}", new Object[] {realmId});
        return groups.size();
    }
    
    private Map<String, Object> buildRoleGroup(Role role) {
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("groupTitle", role.getName());
        group.put("names", Arrays.asList(role.getName()));
        group.put("rights", iterableToList(role.getRights()));
        return group;
    }
    
    private List<GrantedAuthority> iterableToList(Set<GrantedAuthority> original) {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        for (GrantedAuthority authority : original) {
            list.add(authority);
        }
        return list;
    }
    
    private Role buildAggregate() {
        info("Building Aggregate Viewer default role.");
        return RoleBuilder.makeRole(AGGREGATE_VIEWER)
                .addRights(new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ }).build();
    }
    
    private Role buildEducator() {
        info("Building Educator default role.");
        return RoleBuilder.makeRole(EDUCATOR)
                .addRights(new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL }).build();
    }
    
    private Role buildLeader() {
        info("Building Leader default role.");
        return RoleBuilder
                .makeRole(LEADER)
                .addRights(
                        new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL,
                                Right.READ_RESTRICTED }).build();
    }
    
    private Role buildIT() {
        info("Building IT Administrator default role.");
        return RoleBuilder
                .makeRole(IT_ADMINISTRATOR)
                .addRights(
                        new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL,
                                Right.READ_RESTRICTED, Right.WRITE_GENERAL, Right.WRITE_RESTRICTED }).build();
    }
    
    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }    
}
