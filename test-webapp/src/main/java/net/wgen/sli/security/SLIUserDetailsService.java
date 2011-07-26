package net.wgen.sli.security;

import java.util.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;


public class SLIUserDetailsService implements UserDetailsService 
{
 
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
 
        // Setting the password field to the username
        User user = new User(username, username, true, true, true, true, getAuthorities(true));
        return user;
    }
 
    private List<GrantedAuthority> getAuthorities(boolean isAdmin) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(2);
        authList.add(new GrantedAuthorityImpl("ROLE_USER"));
        if (isAdmin) {
            authList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        }
        return new ArrayList<GrantedAuthority>(authList);
    } 
 
}