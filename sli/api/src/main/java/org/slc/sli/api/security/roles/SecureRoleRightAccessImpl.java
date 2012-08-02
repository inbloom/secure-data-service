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

package org.slc.sli.api.security.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

/**
 * A basic implementation of RoleRightAccess
 * 
 * @author rlatta
 */
@Component
public class SecureRoleRightAccessImpl implements RoleRightAccess {
    @Autowired
    private EntityDefinitionStore store;
    
    @Value("${sli.sandbox.enabled}")
    protected boolean isSandboxEnabled;

    private EntityService service;
    
    @Resource(name = "validationRepo")
    private Repository<Entity> repo;
    
    public static final String EDUCATOR = "Educator";
    public static final String LEADER = "Leader";
    public static final String AGGREGATOR = "Aggregate Viewer";
    public static final String IT_ADMINISTRATOR = "IT Administrator";
    
    public static final String LEA_ADMINISTRATOR = "LEA Administrator";
    public static final String SEA_ADMINISTRATOR = "SEA Administrator";
    public static final String APP_DEVELOPER = "Application Developer";
    public static final String SLC_OPERATOR = "SLC Operator";
    public static final String REALM_ADMINISTRATOR = "Realm Administrator";
    public static final String INGESTION_USER = "Ingestion User";
    public static final String SANDBOX_SLC_OPERATOR = "Sandbox SLC Operator";
    public static final String SANDBOX_ADMINISTRATOR = "Sandbox Administrator";
    
    private final Map<String, Role> adminRoles = new HashMap<String, Role>();
    
    @PostConstruct
    public void init() {
        EntityDefinition def = store.lookupByResourceName("roles");
        setService(def.getService());
        
        adminRoles.put(
                LEA_ADMINISTRATOR,
                RoleBuilder
                        .makeRole(LEA_ADMINISTRATOR)
                        .addRights(
                                new Right[] { Right.ADMIN_ACCESS, Right.EDORG_APP_AUTHZ, Right.READ_PUBLIC,
                                        Right.CRUD_LEA_ADMIN }).setAdmin(true).build());
        adminRoles.put(
                SEA_ADMINISTRATOR,
                RoleBuilder
                        .makeRole(SEA_ADMINISTRATOR)
                        .addRights(
                                new Right[] { Right.ADMIN_ACCESS, Right.EDORG_DELEGATE, Right.READ_PUBLIC,
                                        Right.CRUD_SEA_ADMIN, Right.CRUD_LEA_ADMIN }).setAdmin(true).build());
        adminRoles.put(
                SANDBOX_SLC_OPERATOR,
                RoleBuilder
                        .makeRole(SANDBOX_SLC_OPERATOR)
                        .addRights(
                                new Right[] { Right.ADMIN_ACCESS, Right.CRUD_SANDBOX_SLC_OPERATOR,
                                        Right.CRUD_SANDBOX_ADMIN }).setAdmin(true).build());
        adminRoles
                .put(SANDBOX_ADMINISTRATOR,
                        RoleBuilder.makeRole(SANDBOX_ADMINISTRATOR)
                                .addRights(new Right[] { Right.ADMIN_ACCESS, Right.CRUD_SANDBOX_ADMIN }).setAdmin(true)
                                .build());
        adminRoles.put(
                REALM_ADMINISTRATOR,
                RoleBuilder
                        .makeRole(REALM_ADMINISTRATOR)
                        .addRights(
                                new Right[] { Right.ADMIN_ACCESS, Right.READ_GENERAL, Right.CRUD_REALM_ROLES,
                                        Right.READ_PUBLIC, Right.CRUD_ROLE }).setAdmin(true).build());
        
        Right[] appDevRights = null;
        if (isSandboxEnabled) {
            appDevRights = new Right[] { Right.ADMIN_ACCESS, Right.DEV_APP_CRUD, Right.READ_GENERAL,
                            Right.READ_PUBLIC, Right.CRUD_ROLE};
        } else {
            appDevRights = new Right[] { Right.ADMIN_ACCESS, Right.DEV_APP_CRUD, Right.READ_GENERAL,
                    Right.READ_PUBLIC};
        }
        adminRoles.put(
                APP_DEVELOPER,
                RoleBuilder
                        .makeRole(APP_DEVELOPER)
                        .addRights(appDevRights).setAdmin(true).build());
        
        adminRoles.put(INGESTION_USER,
                RoleBuilder.makeRole(INGESTION_USER).addRights(new Right[] { Right.INGEST_DATA, Right.ADMIN_ACCESS })
                        .setAdmin(true).build());
        adminRoles.put(
                SLC_OPERATOR,
                RoleBuilder
                        .makeRole(SLC_OPERATOR)
                        .addRights(
                                new Right[] { Right.ADMIN_ACCESS, Right.SLC_APP_APPROVE, Right.READ_GENERAL,
                                        Right.READ_PUBLIC, Right.CRUD_SLC_OPERATOR, Right.CRUD_SEA_ADMIN,
                                        Right.CRUD_LEA_ADMIN }).setAdmin(true).build());
        
    }
    
    @Override
    public List<Role> findAdminRoles(List<String> roleNames) {
        List<Role> roles = new ArrayList<Role>();
        
        for (String roleName : roleNames) {
            if (adminRoles.containsKey(roleName)) {
                roles.add(adminRoles.get(roleName));
            }
        }
        
        return roles;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Role> findRoles(String tenantId, String realmId, List<String> roleNames) {
        List<Role> roles = new ArrayList<Role>();
        
        if (roleNames != null) {
            final NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.addCriteria(new NeutralCriteria("metaData.tenantId", "=", tenantId, false));
            neutralQuery.addCriteria(new NeutralCriteria("realmId", "=", realmId));
            
            Entity doc = SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {
                
                @Override
                public Entity execute() {
                    return repo.findOne("customRole", neutralQuery);
                }
            });
            
            if (doc != null) {
                Map<String, Object> roleDefs = doc.getBody();
                
                if (roleDefs != null) {
                    List<Map<String, List<String>>> roleData = (List<Map<String, List<String>>>) roleDefs.get("roles");
                    
                    for (Map<String, List<String>> role : roleData) {
                        List<String> names = role.get("names");
                        
                        for (String roleName : names) {
                            if (roleNames.contains(roleName)) {
                                List<String> rights = role.get("rights");
                                roles.add(RoleBuilder.makeRole(roleName).addGrantedAuthorities(rights).build());
                            }
                        }
                    }
                }
            }
        }
        return roles;
    }
    
    @Override
    public Role findRoleByName(String name) {
        Role admin = this.adminRoles.get(name);
        
        if (admin != null) {
            return admin;
        }
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);
        
        // TODO find a way to "findAll" from entity service
        Iterable<String> ids = service.listIds(neutralQuery);
        for (String id : ids) {
            EntityBody body;
            body = service.get(id); // FIXME massive hack for roles disappearing sporadically
            
            if (body.get("name").equals(name)) {
                return getRoleWithBodyAndID(id, body);
            }
        }
        return null;
    }
    
    @Override
    public Role findRoleBySpringName(String springName) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);
        
        Iterable<String> ids = service.listIds(neutralQuery);
        for (String id : ids) {
            EntityBody body = service.get(id);
            Role tempRole = getRoleWithBodyAndID(id, body);
            if (tempRole.getSpringRoleName().equals(springName)) {
                return tempRole;
            }
        }
        return null;
    }
    
    private Role getRoleWithBodyAndID(String id, EntityBody body) {
        return RoleBuilder.makeRole(body).addId(id).build();
    }
    
    @Override
    public List<Role> fetchAllRoles() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(100);
        
        List<Role> roles = new ArrayList<Role>();
        Iterable<String> ids = service.listIds(neutralQuery);
        for (String id : ids) {
            EntityBody body = service.get(id);
            roles.add(getRoleWithBodyAndID(id, body));
        }
        return roles;
    }
    
    @Override
    public boolean addRole(Role role) {
        return service.create(role.getRoleAsEntityBody()) != null;
    }
    
    @Override
    public boolean deleteRole(Role role) {
        if (role.getId().length() > 0) {
            try {
                service.delete(role.getId());
                return true;
            } catch (Exception e) {
                return false;
            }
            
        }
        return false;
    }
    
    @Override
    public boolean updateRole(Role role) {
        if (role.getId().length() > 0) {
            try {
                service.update(role.getId(), role.getRoleAsEntityBody());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    // Injection method.
    public void setStore(EntityDefinitionStore store) {
        this.store = store;
    }
    
    // Injection method.
    public void setService(EntityService service) {
        this.service = service;
    }
    
    @Override
    public Role getDefaultRole(String name) {
        return findRoleByName(name);
    }
}
