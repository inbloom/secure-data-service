package org.slc.sli.api.init;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A simple initializing bean to initialize our Mongo instance with default roles.
 * 
 * @author rlatta
 */
@Component
public class RoleInitializer {
    public static final String  EDUCATOR          = "Educator";
    public static final String  AGGREGATE_VIEWER  = "Aggregate Viewer";
    public static final String  IT_ADMINISTRATOR  = "IT Administrator";
    public static final String  LEADER            = "Leader";
    public static final String  SLI_ADMINISTRATOR = "SLI Administrator";
    public static final String  LEA_ADMINISTRATOR = "LEA Administrator";
    public static final String APP_DEVELOPER = "Application Developer";
    public static final String SLC_OPERATOR = "SLC Operator";
    
    private static final Logger LOG               = LoggerFactory.getLogger(RoleInitializer.class);
    public static final String  ROLES             = "roles";
    
    @Autowired
    private Repository<Entity>    repository;
    
    @PostConstruct
    public void init() {
        dropRoles();
        buildRoles();
    }
    
    private void dropRoles() {
        repository.deleteAll(ROLES);
    }
    
    public int buildRoles() {
        Iterable<Entity> subset = repository.findAll(ROLES);
        Set<Role> createdRoles = new HashSet<Role>();
        
        boolean hasEducator = false;
        boolean hasLeader = false;
        boolean hasIT = false;
        boolean hasAggregate = false;
        boolean hasSLIAdmin = false;
        boolean hasLEAAdmin = false;
        boolean hasAppDeveloper = false;
        boolean hasSLCOperator = false;
        
        for (Entity entity : subset) {
            Map<String, Object> body = entity.getBody();
            if (body.get("name").equals(EDUCATOR)) {
                hasEducator = true;
            } else if (body.get("name").equals(AGGREGATE_VIEWER)) {
                hasAggregate = true;
            } else if (body.get("name").equals(IT_ADMINISTRATOR)) {
                hasIT = true;
            } else if (body.get("name").equals(LEADER)) {
                hasLeader = true;
            } else if (body.get("name").equals(SLI_ADMINISTRATOR)) {
                hasSLIAdmin = true;
            } else if (body.get("name").equals(LEA_ADMINISTRATOR)) {
                hasLEAAdmin = true;
            } else if (body.get("name").equals(APP_DEVELOPER)) {
                hasAppDeveloper = true;
            } else if (body.get("name").equals(SLC_OPERATOR)) {
                hasSLCOperator = true;
            }
        }
        if (!hasAggregate) {
            createdRoles.add(buildAggregate());
        }
        if (!hasLeader) {
            createdRoles.add(buildLeader());
        }
        if (!hasIT) {
            createdRoles.add(buildIT());
        }
        if (!hasEducator) {
            createdRoles.add(buildEducator());
        }
        if (!hasSLIAdmin) {
            createdRoles.add(buildSLIAdmin());
        }
        if (!hasLEAAdmin) {
            createdRoles.add(buildLEAAdmin());
        }
        if (!hasAppDeveloper) {
            createdRoles.add(buildAppDeveloper());
        }
        
        for (Role body : createdRoles) {
            repository.create(ROLES, body.getRoleAsEntityBody());
        }
        return createdRoles.size();
        
    }
    
    private Role buildAggregate() {
        LOG.info("Building Aggregate Viewer default role.");
        return RoleBuilder.makeRole(AGGREGATE_VIEWER).addRights(new Right[] { Right.AGGREGATE_READ }).build();
    }
    
    private Role buildSLCOperator() {
        LOG.info("Building Application Developer role.");
        return RoleBuilder.makeRole(SLC_OPERATOR)
                .addRights(new Right[] { Right.ADMIN_ACCESS, Right.APP_REGISTER, Right.APP_EDORG_SELECT }).build();
    }

    private Role buildAppDeveloper() {
        LOG.info("Building Application Developer role.");
        return RoleBuilder.makeRole(APP_DEVELOPER)
                .addRights(new Right[] { Right.ADMIN_ACCESS, Right.APP_CREATION, Right.APP_EDORG_SELECT }).build();
    }

    private Role buildEducator() {
        LOG.info("Building Educator default role.");
        return RoleBuilder.makeRole(EDUCATOR).addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL }).build();
    }
    
    private Role buildLeader() {
        LOG.info("Building Leader default role.");
        return RoleBuilder.makeRole(LEADER).addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED }).build();
    }
    
    private Role buildIT() {
        LOG.info("Building IT Administrator default role.");
        return RoleBuilder.makeRole(IT_ADMINISTRATOR).addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED, Right.WRITE_GENERAL, Right.WRITE_RESTRICTED }).build();
    }
    
    private Role buildSLIAdmin() {
        LOG.info("Building SLI Administrator default role.");
        return RoleBuilder.makeRole(SLI_ADMINISTRATOR).addRights(new Right[] { Right.ADMIN_ACCESS }).setAdmin(true).build();
    }
    
    private Role buildLEAAdmin() {
        LOG.info("Building LEA Administrator default role.");
        return RoleBuilder.makeRole(LEA_ADMINISTRATOR).addRights(new Right[] { Right.ADMIN_ACCESS }).setAdmin(true).build();
    }
    
    public void setRepository(Repository repo) {
        repository = repo;
    }
    
}
