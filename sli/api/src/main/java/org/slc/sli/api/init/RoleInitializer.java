package org.slc.sli.api.init;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.enums.Right;

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

    private static final Logger LOG               = LoggerFactory.getLogger(RoleInitializer.class);
    public static final String  ROLES             = "roles";

    @Autowired
    private EntityRepository    repository;

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

        createdRoles.add(buildAggregate());
        createdRoles.add(buildLeader());
        createdRoles.add(buildIT());
        createdRoles.add(buildEducator());
        createdRoles.add(buildSLIAdmin());

        for (Role body : createdRoles) {
            repository.create(ROLES, body.getRoleAsEntityBody());
        }
        return createdRoles.size();

    }

    private Role buildAggregate() {
        LOG.info("Building Aggregate Viewer default role.");
        return RoleBuilder.makeRole(AGGREGATE_VIEWER).addRights(new Right[] { Right.AGGREGATE_READ }).build();
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
        return RoleBuilder.makeRole(SLI_ADMINISTRATOR).addRights(new Right[] { Right.ADMIN_ACCESS }).build();
    }

    public void setRepository(EntityRepository repo) {
        repository = repo;
    }

}
