package org.slc.sli.api.resources;

import org.slc.sli.api.resources.security.SamlFederationResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.MongoTokenStore;
import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.roles.RoleRightAccess;
import org.slc.sli.api.util.OAuthTokenUtil;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * System resource class for security session context.
 * Hosted at the URI path "/system/session"
 */
@Path("{a:v1/|}system/session")
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class SecuritySessionResource {

    @Autowired
    private RoleRightAccess roleRightAccess;

    @Autowired
    private ClientRoleResolver roleResolver;

    @Autowired
    private OAuthTokenUtil oAuthTokenUtil;

    @Autowired
    private MongoTokenStore tokenStore;
    
    @Autowired
    private SamlFederationResource federationResource;

    @Value("${sli.security.noSession.landing.url}")
    private String realmPage;

    /**
     * Method processing HTTP GET requests, producing "application/json" MIME media
     * type.
     *
     * @return SecurityContext that will be send back as a response of type "application/json".
     */
    @GET
    @Path("logout")
    public Object logoutUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication oAuth = ((OAuth2Authentication) auth).getUserAuthentication();

        final String noLoginMsg = "User must be logged in to logout";
        if (oAuth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException(noLoginMsg);
        }

        RemoveTokensTask removeTokensTask = new RemoveTokensTask();
        removeTokensTask.setOAuth(oAuth);
        Object result = SecurityUtil.sudoRun(removeTokensTask);

        if ( federationResource.logoutOfIdp((SLIPrincipal) oAuth.getPrincipal()) ) {
            return "{logout: true}";
        }
        return "{logout: false}";
    }
    
    private class RemoveTokensTask implements SecurityTask<Object> {

        private Authentication oAuth;

        @Override
        public java.lang.Object execute() {
            Collection<String> appTokens = oAuthTokenUtil.getTokensForPrincipal((SLIPrincipal) oAuth.getPrincipal());
            for( String token : appTokens) {
                tokenStore.removeAccessToken(token);
            }
            return true;
        }

        public void setOAuth(Authentication oAuth) {
            this.oAuth = oAuth;
        }
    }

    /**
     * Method processing HTTP GET requests, producing "application/json" MIME media
     * type.
     *
     * @return SecurityContext that will be send back as a response of type "application/json".
     */
    @GET
    @Path("debug")
    public SecurityContext getSecurityContext() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication oAuth = ((OAuth2Authentication) auth).getUserAuthentication();

        final String noLoginMsg = "User must be logged in";
        if (oAuth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException(noLoginMsg);
        }

        SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
        principal.setSliRoles(roleResolver.resolveRoles(principal.getRealm(), principal.getRoles()));
        return SecurityContextHolder.getContext();
    }

    @GET
    @Path("check")
    public Object sessionCheck() {

        final Map<String, Object> sessionDetails = new TreeMap<String, Object>();

        if (isAuthenticated(SecurityContextHolder.getContext())) {
            sessionDetails.put("authenticated", true);
            sessionDetails.put("sessionId", SecurityContextHolder.getContext().getAuthentication().getCredentials());

            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sessionDetails.put("user_id", principal.getId());
            sessionDetails.put("full_name", principal.getName());
            sessionDetails.put("granted_authorities", principal.getRoles());
            sessionDetails.put("realm", principal.getRealm());
            sessionDetails.put("adminRealm", principal.getAdminRealm());
            sessionDetails.put("edOrg", principal.getEdOrg());
            sessionDetails.put("sliRoles", roleResolver.resolveRoles(principal.getRealm(), principal.getRoles()));

            List<Role> allRoles = SecurityUtil.sudoRun(new SecurityTask<List<Role>>() {
                @Override
                public List<Role> execute() {
                    return roleRightAccess.fetchAllRoles();
                }
            });

            sessionDetails.put("all_roles", allRoles);

        } else {
            sessionDetails.put("authenticated", false);
            sessionDetails.put("redirect_user", realmPage);
        }

        return sessionDetails;
    }

    private boolean isAuthenticated(SecurityContext securityContext) {
        return !(securityContext.getAuthentication().getCredentials().equals(""));
    }
}
