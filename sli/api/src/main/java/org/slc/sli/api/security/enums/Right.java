package org.slc.sli.api.security.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * A simple enum describing our basic rights that are required.
 * 
 */
public enum Right implements GrantedAuthority {
    READ_GENERAL, WRITE_GENERAL, READ_RESTRICTED, WRITE_RESTRICTED, AGGREGATE_READ, AGGREGATE_WRITE, READ_ROLES, FULL_ACCESS;
    
    @Override
    public String getAuthority() {
        return this.toString();
    }
    
}
