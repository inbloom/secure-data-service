package org.slc.sli.api.security.oauth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import org.slc.sli.api.security.SLIPrincipal;

/**
 * 
 * @author jnanney
 *
 */
public class SLIAuthenticationToken extends AbstractAuthenticationToken {

    private SLIPrincipal principal;
    
    public SLIAuthenticationToken(Collection<? extends GrantedAuthority> authorities, SLIPrincipal principal) {
        super(authorities);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
    
}
