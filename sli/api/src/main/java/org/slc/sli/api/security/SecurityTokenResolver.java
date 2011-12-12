package org.slc.sli.api.security;

import org.springframework.security.core.Authentication;

/**
 * Interface to resolve a security token to a Spring Authentication object.
 *
 */
public interface SecurityTokenResolver {
    public Authentication resolve(String token);
}
