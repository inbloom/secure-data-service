package org.slc.sli.api.init;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.enums.Right;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleBuilder;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
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
    public static final String EDUCATOR = "Educator";
    public static final String AGGREGATE_VIEWER = "Aggregate Viewer";
    public static final String IT_ADMINISTRATOR = "IT Administrator";
    public static final String LEADER = "Leader";
    public static final String SLI_ADMINISTRATOR = "SLI Administrator";
    
    private static final Logger LOG = LoggerFactory.getLogger(RoleInitializer.class);
    public static final String ROLES = "roles";
    private static final String AGGREGATE_READ = "AGGREGATE_READ";
    private static final String AGGREGATE_WRITE = "AGGREGATE_WRITE";
    private static final String READ_GENERAL = "READ_GENERAL";
    private static final String READ_RESTRICTED = "READ_RESTRICTED";
    private static final String WRITE_GENERAL = "WRITE_GENERAL";
    private static final String WRITE_RESTRICTED = "WRITE_RESTRICTED";

    @Autowired
    private EntityRepository repository;


    private void init() {
        buildRoles();
    }

    public int buildRoles() {
        Iterable<Entity> subset = repository.findAll(ROLES);
        Set<Role> createdRoles = new HashSet<Role>();

        boolean hasEducator = false;
        boolean hasLeader = false;
        boolean hasIT = false;
        boolean hasAggregate = false;
        boolean hasSLIAdmin = false;

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
            }
        }
        if (!hasAggregate)
            createdRoles.add(buildAggregate());
        if (!hasLeader)
            createdRoles.add(buildLeader());
        if (!hasIT)
            createdRoles.add(buildIT());
        if (!hasEducator)
            createdRoles.add(buildEducator());

        if (!hasSLIAdmin)
            createdRoles.add(buildSLIAdmin());
        for (EntityBody body : createdRoles) {
            repository.create(ROLES, body);
        }
        return createdRoles.size();

    }

    private Role buildAggregate() {
        LOG.info("Building Aggregate Viewer default role.");
        return RoleBuilder.makeRole(AGGREGATE_VIEWER).addRights(new Right[] { Right.AGGREGATE_READ }).build();
    }

    private Role buildEducator() {
        LOG.info("Building Educator default role.");
        return RoleBuilder.makeRole(EDUCATOR).addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL })
                .build();
    }

    private Role buildLeader() {
        LOG.info("Building Leader default role.");
        return RoleBuilder.makeRole(LEADER)
                .addRights(new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED }).build();
    }

    private Role buildIT() {
        LOG.info("Building IT Administrator default role.");
        return RoleBuilder
                .makeRole(IT_ADMINISTRATOR)
                .addRights(
                        new Right[] { Right.AGGREGATE_READ, Right.READ_GENERAL, Right.READ_RESTRICTED,
                                Right.WRITE_GENERAL, Right.WRITE_RESTRICTED }).build();
    }
    
    private EntityBody buildSLIAdmin() {
        LOG.info("Building SLI Administrator default role.");
        return RoleBuilder.makeRole(SLI_ADMINISTRATOR).addRights(new Right[] { Right.READ_ROLES }).build();
    }
    
    public void setRepository(EntityRepository repo) {
        repository = repo;
    }
    
}
