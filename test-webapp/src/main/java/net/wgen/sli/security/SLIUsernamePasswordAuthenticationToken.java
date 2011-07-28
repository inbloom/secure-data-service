package net.wgen.sli.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SLIUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String m_directory;

    SLIUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    SLIUsernamePasswordAuthenticationToken(Object principal, Object credentials, java.util.Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
       
    public String getDirectory() {
        return m_directory;
    }
    
    public void setDirectory(String directory) {
        m_directory = directory;
    }
}