package org.slc.sli.api.resources.security;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slc.sli.api.ldap.LdapService;
import org.slc.sli.api.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author dliu
 * 
 */

@Component
@Scope("request")
@Path("/adminAccounts")
@Produces({ Resource.JSON_MEDIA_TYPE + ";charset=utf-8" })
public class AdminAccountResource {

    @Autowired
    LdapService ldapservice;
    
}
