package org.slc.sli.sandbox.idp.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Returns available role information
 */
@Component
public class RoleService {
    
    /**
     * Holds role information.
     * 
     */
    public static class Role {
        String name;
        
        public Role(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String getId() {
            return name;
        }
    }
    
    /**
     * Returns roles available to the user
     */
    public List<Role> getAvailableRoles() {
        return Arrays.asList(new Role("IT Administrator"), new Role("Leader"), new Role("Educator"), new Role(
                "Aggregator"));
    }
}
