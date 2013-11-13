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

package org.slc.sli.api.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

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
    public static final String TEACHER = "Teacher";
    public static final String AGGREGATE_VIEWER = "Aggregate Viewer";
    public static final String SPECIALIST_CONSULTANT = "Specialist/Consultant";
    public static final String IT_ADMINISTRATOR = "IT Administrator";
    public static final String SCHOOL_ADMIN = "School Administrative Support Staff";
    public static final String LEA_ADMIN = "LEA System Administrator";
    public static final String LEADER = "Leader";
    public static final String PRINCIPAL = "Principal";
    public static final String SUPERINTENDENT = "Superintendent";
    public static final String STUDENT = "Student";
    public static final String PARENT = "Parent";
    public static final String ROLES = "customRole";

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

    public void dropAndBuildRoles(String realmId) {
        dropRoles(realmId);
        buildRoles(realmId);
    }

    public void dropRoles(String realmId) {
        NeutralQuery query = new NeutralQuery(0);
        query.addCriteria(new NeutralCriteria("realmId", "=", realmId));

        Entity entity = repository.findOne(ROLES, query);
        if (entity != null) {
            repository.delete(ROLES, entity.getEntityId());
            info("Successfully dropped roles from realm: {}", new Object[] { realmId });
        } else {
            info("No roles exist to drop for realm: {}", new Object[] { realmId });
        }

    }

    public int buildRoles(String realmId) {
        if (realmId != null) {
            info("Building roles for realm: {}", new Object[] { realmId });
            Map<String, Object> rolesBody = new HashMap<String, Object>();
            List<Map<String, Object>> groups = getDefaultRoles();
            rolesBody.put("realmId", realmId);
            rolesBody.put("roles", groups);
            rolesBody.put("customRights", new ArrayList<String>());
            repository.create(ROLES, rolesBody);
            return groups.size();
        } else {
            warn("Null realm id --> not building roles.");
        }
        return 0;
    }

    public List<Map<String, Object>> getDefaultRoles() {
        List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
        groups.add(buildRoleGroup(buildAggregate()));
        groups.add(buildRoleGroup(buildLeader()));
        groups.add(buildRoleGroup(buildIT()));
        groups.add(buildRoleGroup(buildEducator()));
        groups.add(buildRoleGroup(RoleBuilder.makeRole(Arrays.asList("Student")).addGroupTitle("Student").addRights(new Right[]{Right.READ_PUBLIC, Right.READ_STUDENT_GENERAL}).addSelfRights(new Right[]{Right.READ_STUDENT_OWNED}).setAdmin(false).build()));
        groups.add(buildRoleGroup(RoleBuilder.makeRole(Arrays.asList("Parent")).addGroupTitle("Parent").addRights(new Right[]{Right.READ_PUBLIC,
                Right.READ_STUDENT_GENERAL}).addSelfRights(new Right[]{Right.READ_STUDENT_OWNED, Right.READ_STUDENT_RESTRICTED}).setAdmin(false).build()));
        return groups;
    }

    private Map<String, Object> buildRoleGroup(Role role) {
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("groupTitle", role.getGroupTitle());
        group.put("names", role.getName());
        group.put("rights", iterableToList(role.getRightsAsStrings()));
        group.put("selfRights", iterableToList(role.getSelfRightsAsStrings()));
        group.put("isAdminRole", role.isAdmin());
        return group;
    }

    private List<String> iterableToList(Set<String> original) {
        List<String> list = new ArrayList<String>();
        for (String authority : original) {
            list.add(authority);
        }
        return list;
    }

    private Role buildAggregate() {
        info("Building Aggregate Viewer default role.");
        ArrayList<String> roleNames = new ArrayList<String>();
        roleNames.add(AGGREGATE_VIEWER);
        roleNames.add(SPECIALIST_CONSULTANT);
        Role role = RoleBuilder.makeRole(roleNames)
                .addGroupTitle(AGGREGATE_VIEWER)
                .addRights(new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.STAFF_CONTEXT })
                .addSelfRights(new Right[] { Right.READ_GENERAL, Right.READ_RESTRICTED}).build();
        role.setAdmin(false);
        return role;
    }

    private Role buildEducator() {
        info("Building Educator default role.");
        ArrayList<String> roleNames = new ArrayList<String>();
        roleNames.add(EDUCATOR);
        roleNames.add(TEACHER);
        Role role = RoleBuilder.makeRole(roleNames)
                .addGroupTitle(EDUCATOR)
                .addRights(new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL, Right.TEACHER_CONTEXT })
                .addSelfRights(new Right[]{ Right.READ_RESTRICTED}).build();
        role.setAdmin(false);
        return role;
    }

    private Role buildLeader() {
        info("Building Leader default role.");
        ArrayList<String> roleNames = new ArrayList<String>();
        roleNames.add(LEADER);
        roleNames.add(PRINCIPAL);
        roleNames.add(SUPERINTENDENT);
        Role role = RoleBuilder.makeRole(roleNames)
                .addGroupTitle(LEADER)
                .addRights(
                        new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL,
                                Right.READ_RESTRICTED, Right.STAFF_CONTEXT }).build();
        role.setAdmin(false);
        return role;
    }

    private Role buildIT() {
        info("Building IT Administrator default role.");
        ArrayList<String> roleNames = new ArrayList<String>();
        roleNames.add(IT_ADMINISTRATOR);
        roleNames.add(LEA_ADMINISTRATOR);
        roleNames.add(SCHOOL_ADMIN);
        roleNames.add(LEA_ADMIN);
        Role role = RoleBuilder.makeRole(roleNames)
                .addGroupTitle(IT_ADMINISTRATOR)
                .addRights(
                        new Right[] { Right.READ_PUBLIC, Right.AGGREGATE_READ, Right.READ_GENERAL,
                                Right.READ_RESTRICTED, Right.WRITE_PUBLIC, Right.WRITE_GENERAL, Right.WRITE_RESTRICTED, Right.STAFF_CONTEXT,
                                Right.SECURITY_EVENT_VIEW}).build();
        role.setAdmin(true);
        return role;
    }

    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }
}
