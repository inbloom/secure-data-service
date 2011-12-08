package org.slc.sli.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface SecurityTokenResolver {
    public Authentication resolve(String token) throws AuthenticationException;
}
