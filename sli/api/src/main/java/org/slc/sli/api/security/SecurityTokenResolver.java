package org.slc.sli.api.security;

import org.springframework.security.core.Authentication;

public interface SecurityTokenResolver {
    public Authentication resolve(String token);
}
