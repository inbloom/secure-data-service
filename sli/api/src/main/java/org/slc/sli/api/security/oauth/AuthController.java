/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.security.oauth;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.api.representation.OAuthAccessExceptionHandler;
import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sun.jersey.core.util.Base64;

/**
 * Controller for Discovery Service
 * 
 * @author dkornishev
 */
@Controller
@Scope("request")
@RequestMapping("/oauth")
public class AuthController {

    private static final Pattern BASIC_AUTH = Pattern.compile("Basic (.+)", Pattern.CASE_INSENSITIVE);

    @Autowired
    private SamlHelper saml;

    @Autowired
    private OauthSessionManager sessionManager;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    /**
     * Calls api to list available realms and injects into model
     * 
     * @param model
     *            spring injected model
     * @return name of the template to use
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "authorize", method = RequestMethod.GET)
    public String listRealms(@RequestParam(value = "redirect_uri", required = false) final String redirectUri,
            @RequestParam(value = "Realm", required = false) final String realmUniqueId,
            @RequestParam(value = "client_id", required = true) final String clientId,
            @RequestParam(value = "state", required = false) final String state,
            @CookieValue(value = "_tla", required = false) final String sessionId, final HttpServletResponse res,
            final Model model) throws IOException {

        if (sessionId != null) {
            String realmId = getRealmId(sessionId);
            if (realmId != null) {
                debug("found valid session --> user authenticated in realm with id: {}", realmId);
                return ssoInit(realmId, sessionId, redirectUri, clientId, state, res, model);
            } else {
                debug("session does not map to a valid oauth session");
            }
        }

        Map<String, String> map = getRealmMap(realmUniqueId);
        
        // Only one realm, so let's bypass the realm selection and direct them straight to that
        // realm
        if (map.size() == 1) {
            return ssoInit(map.keySet().iterator().next(), sessionId, redirectUri, clientId, state, res, model);
        }

        model.addAttribute("dummy", new HashMap<String, String>());
        model.addAttribute("realms", map);
        model.addAttribute("redirect_uri", redirectUri != null ? redirectUri : "");
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);

        return "realms";
    }

    private Map<String, String> getRealmMap(final String realmUniqueId) {
        Map<String, String> result = SecurityUtil.runWithAllTenants(new SecurityTask<Map<String, String>>() {
            
            @Override
            public Map<String, String> execute() {
                return SecurityUtil.sudoRun(new SecurityTask<Map<String, String>>() {
                    @Override
                    public Map<String, String> execute() {
                        Iterable<Entity> realmList = repo.findAll("realm", new NeutralQuery());
                        Map<String, String> map = new HashMap<String, String>();
                        for (Entity realmEntity : realmList) {
                            map.put(realmEntity.getEntityId(), (String) realmEntity.getBody().get("name"));
                            
                            // We found the requested realm, so let's only return a map with just
                            // that entry
                            // so that we can short-circuit the realm selection
                            if (realmUniqueId != null && !realmUniqueId.isEmpty()) {
                                if (realmUniqueId.equals(realmEntity.getBody().get("uniqueIdentifier"))) {
                                    map.clear();
                                    map.put(realmEntity.getEntityId(), (String) realmEntity.getBody().get("name"));
                                    return map;
                                }
                            }
                        }
                        return map;
                    }
                });
            }
        });
        return result;
    }

    @RequestMapping(value = "token", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity<String> getAccessToken(@RequestParam("code") String authorizationCode,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestHeader(value = "Authorization", required = false) String authz,
            @RequestParam("client_id") String clientId, @RequestParam("client_secret") String clientSecret, Model model)
            throws BadCredentialsException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("code", authorizationCode);
        parameters.put("redirect_uri", redirectUri);

        String token;
        try {
            token = this.sessionManager.verify(authorizationCode, Pair.of(clientId, clientSecret));
        } catch (OAuthAccessException e) {
            return handleAccessException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String response = String.format("{\"access_token\":\"%s\"}", token);
        info("Access Token: \n{}", response);

        return new ResponseEntity<String>(response, headers, HttpStatus.OK);
    }

    // TODO: Normally we would let the ExceptionHandler for OauthAccessException handle the
    // exception automatically, but since it gets thrown as part of a Spring request handler
    // and not jax-rs, it doesn't get invoked automatically.
    private ResponseEntity<String> handleAccessException(OAuthAccessException e) {
        OAuthAccessExceptionHandler handler = new OAuthAccessExceptionHandler();
        Response resp = handler.toResponse(e);
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, resp.getEntity());
        } catch (Exception e1) {
            error("Error handling exception", e1);
        }
        return new ResponseEntity<String>(writer.getBuffer().toString(), HttpStatus.valueOf(resp.getStatus()));
    }

    /**
     * Redirects user to the sso init url given valid id
     * 
     * @param realmId
     *            id of the realm
     * @return directive to redirect to sso init page
     * @throws IOException
     */
    @RequestMapping(value = "sso", method = { RequestMethod.GET, RequestMethod.POST })
    public String ssoInit(@RequestParam(value = "realmId", required = true) final String realmIndex,
            @RequestParam(value = "sessionId", required = false) final String sessionId,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "clientId", required = true) final String clientId,
            @RequestParam(value = "state", required = false) final String state, HttpServletResponse res, Model model)
            throws IOException {

        String realmId = getRealmId(sessionId);
        boolean isExpired = isSessionExpired(sessionId);
        boolean forceAuthn = (sessionId != null && realmId != null && !isExpired) ? false : true;
        
        // Ugly, but we need both sudo access and full tenant access
        Entity realmEnt = SecurityUtil.sudoRun(new SecurityTask<Entity>() {
            
            @Override
            public Entity execute() {
                return SecurityUtil.runWithAllTenants(new SecurityTask<Entity>() {
                    @Override
                    public Entity execute() {
                        Entity ent = repo.findById("realm", realmIndex);
                        if (ent == null) {
                            throw new IllegalArgumentException("couldn't locate idp for realm: " + realmIndex);
                        }
                        return ent;
                    }
                });
            }
        });

        @SuppressWarnings("unused")
        String tenantId = (String) realmEnt.getBody().get("tenantId");

        @SuppressWarnings("unchecked")
        Map<String, String> idpData = (Map<String, String>) realmEnt.getBody().get("idp");
        String endpoint = idpData.get("redirectEndpoint");
        String idpTypeString = idpData.get("idpType");

        if (endpoint == null) {
            throw new IllegalArgumentException("realm " + realmIndex + " doesn't have an endpoint");
        }

        debug("creating saml authnrequest with ForceAuthn equal to {}", forceAuthn);

        int idpType = 1;

        if (idpTypeString != null && idpTypeString.equalsIgnoreCase("Siteminder")) {
            idpType = 4;
        }

        // {messageId,encodedSAML}
        Pair<String, String> tuple = saml.createSamlAuthnRequestForRedirect(endpoint, forceAuthn, idpType);

        sessionManager.createAppSession(sessionId, clientId, redirectUri, state, tenantId, tuple.getLeft(), isExpired);

        debug("redirecting to: {}", endpoint);

        String redirectUrl = endpoint.contains("?") ? endpoint + "&SAMLRequest=" + tuple.getRight() : endpoint
                + "?SAMLRequest=" + tuple.getRight();

        return "redirect:" + redirectUrl;
    }

    @SuppressWarnings("unchecked")
    private String getRealmId(final String sessionId) {
        String realmId = null;
        if (sessionId != null) {
            Entity session = sessionManager.getSession(sessionId);
            if (session != null) {
                Map<String, Object> principal = (Map<String, Object>) session.getBody().get("principal");
                realmId = (String) principal.get("realm");
            }
        }

        return realmId;
    }

    @SuppressWarnings("unchecked")
    private boolean isSessionExpired(final String sessionId) {
        boolean isExpired = true;
        if (sessionId != null) {
            Entity session = sessionManager.getSession(sessionId);
            if (session != null) {
                Map<String, Object> body = session.getBody();
                long expiration = (Long) body.get("expiration");
                long hardLogout = (Long) body.get("hardLogout");
                long now = System.currentTimeMillis();
                isExpired = (now >= expiration || now >= hardLogout);
            }
        }
        return isExpired;
    }

    @SuppressWarnings("unused")
    private Pair<String, String> extractClientCredentials(String authz) {
        Matcher m = BASIC_AUTH.matcher(authz);

        if (authz != null && m.find()) {
            String decoded = Base64.base64Decode(m.group(1));
            String[] values = decoded.split(":");
            return Pair.of(values[0], values[1]);
        } else {
            throw new IllegalArgumentException("Client auth must be provided");
        }
    }
}
