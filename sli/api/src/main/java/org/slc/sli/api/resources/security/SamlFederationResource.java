/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.resources.security;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.saml2.binding.decoding.HTTPArtifactDecoder;
import org.opensaml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.opensaml.saml2.binding.artifact.SAML2ArtifactType0004;
import org.opensaml.saml2.core.ArtifactResolve;
import org.opensaml.saml2.core.ArtifactResponse;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.soap.soap11.impl.EnvelopeImpl;
import org.opensaml.xml.XMLObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.representation.CustomStatus;
import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.roles.EdOrgContextualRoleBuilder;
import org.slc.sli.api.security.roles.Role;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Process SAML assertions
 *
 * @author dkornishev
 */
@Component
@Path("saml")
public class SamlFederationResource {

    @Autowired
    private SamlHelper samlHelper;

    @Autowired
    private KeyStoreAccessor apiKeyStoreAccessor;

    @Autowired
    private ArtifactBindingHelper artifactBindingHelper;

    @Autowired
    private SOAPHelper soapHelper;

    @Autowired
    @Qualifier("validationRepo")
    private Repository<Entity> repo;

    @Autowired
    private UserLocator users;

    @Autowired
    private SamlAttributeTransformer transformer;

    @Autowired
    private RolesToRightsResolver resolver;

    @Autowired
    private OauthSessionManager sessionManager;

    @Value("${sli.security.sp.issuerName}")
    private String issuerName;

    @Value("${sli.api.cookieDomain}")
    private String apiCookieDomain;

    @Value("${sli.api.digital.signature.alias}")
    String keyStoreAlias;

    @Value("#{encryptor.decrypt('${sli.encryption.ldapKeyAlias}', '${sli.encryption.ldapKeyPass}', '${sli.api.keystore.entry.password}')}")
    String keyStorEntryPassword;

    @Autowired
    @Value("${sli.sandbox.enabled}")
    private boolean sandboxEnabled;

    @Autowired
    private RealmHelper realmHelper;

    @Context
    private HttpServletRequest httpServletRequest;

    private String metadata;

    private  KeyStore.PrivateKeyEntry pkEntry;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    @Autowired
    private EdOrgContextualRoleBuilder edOrgRoleBuilder;

    @Value("${sli.security.idp.url}")
    private String idpUrl;

    public static SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    private static final String METADATA_TEMPLATE = "saml/samlMetadata-template.vm";
    private static final String TEMPLATE_ISSUER_REFERENCE = "spIssuerName";
    private static final String TEMPLATE_CERTIFICATE_REFERENCE = "certificateText";

    @SuppressWarnings("unused")
    @PostConstruct
    private void processKeyStoreAndMetadata() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {
        pkEntry = apiKeyStoreAccessor.getPrivateKeyEntry(keyStoreAlias, keyStorEntryPassword);

        StringWriter writer = new StringWriter();

        Template veTemplate = getTemplate();
        VelocityContext context = new VelocityContext();
        context.put(TEMPLATE_ISSUER_REFERENCE, issuerName);
        context.put(TEMPLATE_CERTIFICATE_REFERENCE, fetchCertificateText());
        veTemplate.merge(context, writer);

        metadata = writer.toString();
    }


    @POST
    @Path("sso/post")
    @SuppressWarnings("unchecked")
    public Response consume(@FormParam("SAMLResponse") String postData, @Context UriInfo uriInfo) {
        info("Received a SAML post for SSO...");
        TenantContext.setTenantId(null);
        Document doc = null;

        try {
            doc = samlHelper.decodeSamlPost(postData);
        } catch (Exception e) {
            handleSAMLDecodeError(e, uriInfo.getRequestUri());
        }

        String inResponseTo = doc.getRootElement().getAttributeValue("InResponseTo");
        String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);

        Entity session = sessionManager.getSessionForSamlId(inResponseTo);

        String requestedRealmId = (String) session.getBody().get("requestedRealmId");
        Entity realm = getRealm(issuer, requestedRealmId);

        String targetEdOrg = getTargetEdOrg(realm);

        Element assertion = doc.getRootElement().getChild("Assertion", SamlHelper.SAML_NS);

        Element conditions = assertion.getChild("Conditions", SamlHelper.SAML_NS);
        if (conditions != null) {
            validateTimeRange("SAML Conditions failed.", conditions.getAttributeValue("NotBefore"), conditions.getAttributeValue("NotOnOrAfter"), targetEdOrg);
        }

        Element subject = assertion.getChild("Subject", SamlHelper.SAML_NS);
        if (subject != null) {
            Element subjConfirmationData = subject.getChild("SubjectConfirmation", SamlHelper.SAML_NS).getChild("SubjectConfirmationData", SamlHelper.SAML_NS);
            validateSubject(uriInfo.getRequestUri(), subjConfirmationData.getAttributeValue("Recipient"), subjConfirmationData.getAttributeValue("NotBefore"),
                    subjConfirmationData.getAttributeValue("NotOnOrAfter"), targetEdOrg);
        } else {
            raiseSamlValidationError("SAML response is missing Subject.", targetEdOrg);
        }

        LinkedMultiValueMap<String, String> attributes = getAttributesFromAssertion(assertion);

        return authenticateUser(attributes, realm, targetEdOrg, inResponseTo, session, uriInfo.getRequestUri());
    }

    private void handleSAMLDecodeError(Exception e, URI uri) {
        SecurityEvent event = securityEventBuilder.createSecurityEvent(this.getClass().getName(), uri, "", false);

        try {
            event.setExecutedOn(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ue) {
            info("Could not find hostname for security event logging!");
        }

        if (httpServletRequest != null) {
            event.setUserOrigin(httpServletRequest.getRemoteHost());
            event.setAppId(httpServletRequest.getHeader("User-Agent"));
            event.setUser(httpServletRequest.getRemoteUser());

            // The origin header contains the URI info of the IDP server that sends the SAML data.
            event.setLogMessage("SAML message received from " + httpServletRequest.getHeader("Origin")
                    + " is invalid!");
            event.setLogLevel(LogLevelType.TYPE_WARN);
        } else {
            event.setLogMessage("HttpServletRequest is missing, and this should never happen!!");
            event.setLogLevel(LogLevelType.TYPE_ERROR);
        }

        audit(event);

        raiseSamlValidationError(e.getMessage(), null);
    }

    private void raiseSamlValidationError(String message, String targetEdOrg) {
        error(message);
        throw new APIAccessDeniedException("Authorization could not be verified.", targetEdOrg);
    }

    private Entity getRealm(String issuer, String realmId) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);

        neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", issuer));
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", realmId));

        Entity realm = repo.findOne("realm", neutralQuery);
        if (realm == null) {
            raiseSamlValidationError("Invalid realm: " + issuer, null);
        }

        return realm;
    }

    private String getTargetEdOrg(Entity realm) {
        String targetEdOrg = "";
        if (realm.getBody() != null) {
            targetEdOrg = (String) realm.getBody().get("edOrg");
        }

        return targetEdOrg;
    }

    private void validateTimeRange(String errorMessagePrefix, String notBefore, String notOnOrAfter, String targetEdOrg) {
        if (!isTimeInRange(notBefore, notOnOrAfter)) {
            raiseSamlValidationError(errorMessagePrefix + "  Current time not in range " + notBefore + " to "
                    + notOnOrAfter + ".", targetEdOrg);
        }
    }

    private void validateSubject(URI uri, String recipient, String notBefore, String notOnOrAfter, String targetEdOrg) {
        if (!uri.toString().equals(recipient)) {
            raiseSamlValidationError("SAML Recipient was invalid, was " + recipient, targetEdOrg);
        }

        validateTimeRange("SAML Subject failed.", notBefore, notOnOrAfter, targetEdOrg);
    }

    private LinkedMultiValueMap<String, String> getAttributesFromAssertion(Element assertion) {
        Element stmt = assertion.getChild("AttributeStatement", SamlHelper.SAML_NS);
        List<Element> attributeNodes = stmt.getChildren("Attribute", SamlHelper.SAML_NS);

        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();
        for (Element attributeNode : attributeNodes) {
            String samlAttributeName = attributeNode.getAttributeValue("Name");
            List<Element> valueNodes = attributeNode.getChildren("AttributeValue", SamlHelper.SAML_NS);
            for (Element valueNode : valueNodes) {
                attributes.add(samlAttributeName, valueNode.getText());
            }
        }

        return attributes;
    }

    /**
     * Authenticate SSO user.
     *
     * @param attributes - SAML assertion attributes
     * @param realm - User's realm
     * @param targetEdOrg - Target edOrg name
     * @param inResponseTo - Assertion recipient
     * @param session - User session
     * @param requestUri - Request URI
     *
     * @return - SAML response from API
     */
    protected Response authenticateUser(LinkedMultiValueMap<String, String> rawAttributes, Entity realm, String targetEdOrg,
            String inResponseTo, Entity session, URI requestUri) {
        // Apply attribute transforms.
        LinkedMultiValueMap<String, String> attributes = transformer.apply(realm, rawAttributes);

        String tenant = null;
        String sandboxTenant = null;  // For developers coming from developer realm.
        String realmTenant = (String) realm.getBody().get("tenantId");
        String samlTenant = attributes.getFirst("tenant");

        Boolean isAdminRealm = (Boolean) realm.getBody().get("admin");
        isAdminRealm = (isAdminRealm != null) ? isAdminRealm : Boolean.FALSE;
        Boolean isDevRealm = (Boolean) realm.getBody().get("developer");
        isDevRealm = (isDevRealm != null) ? isDevRealm : Boolean.FALSE;
        if (isAdminRealm && sandboxEnabled) {
            // Sandbox mode using the SimpleIDP.
            // Reset isAdminRealm based on the value of the saml isAdmin attribute, since this same realm is used for impersonation and admin logins.
            isAdminRealm = Boolean.valueOf(attributes.getFirst("isAdmin"));
            // Accept the tenantId from the Sandbox-IDP, or if none then it's an admin user.
            if (isAdminRealm){
                tenant = null;  // Operator admin.
            } else {
                // Impersonation login; require tenant in the saml.
                if (samlTenant != null) {
                    tenant = samlTenant;
                } else {
                    error("Attempted login by a user in sandbox mode but no tenant was specified in the saml message.");
                    throw new APIAccessDeniedException("Invalid user configuration.", (String) realm.getBody().get("edOrg"));
                }
            }
        } else if (isAdminRealm){
            // Prod mode, admin login.
            tenant = null;
        } else if (isDevRealm) {
            // Prod mode, developer login.
            tenant = null;
            sandboxTenant = samlTenant;
            samlTenant = null;
        } else {
            // Regular IDP login.
            tenant = realmTenant;
        }

        SLIPrincipal principal = createPrincipal(isAdminRealm, tenant, attributes, isDevRealm, targetEdOrg, realm, samlTenant, sandboxTenant);

        Map<String, Object> appSession = sessionManager.getAppSession(inResponseTo, session);
        String authorizationCode = (String) ((Map<String, Object>) appSession.get("code")).get("value");

        auditSuccessfulLogin(principal, session, requestUri, authorizationCode, realm, appSession, tenant);

        return getLoginResponse(appSession, authorizationCode, requestUri, session.getEntityId());
    }

    /**
     * Create an SLIPrincipal instance for the user's login session.
     *
     * @param isAdminRealm - Indicates if user is using admin realm or not
     * @param tenant - User's tenant ID
     * @param attributes - SAML assertion attributes
     * @param isDevRealm - Indicates if user is using developer realm or not
     * @param targetEdOrg - Target edOrg name
     * @param realm - User's realm
     * @param samlTenant - User's SAML tenant ID
     * @param sandboxTenant - User's sandbox tenant ID
     *
     * @return - Newly created SLIPrincipal instance
     */
    protected SLIPrincipal createPrincipal(boolean isAdminRealm, String tenant, LinkedMultiValueMap<String, String> attributes, boolean isDevRealm,
            String targetEdOrg, Entity realm, String samlTenant, String sandboxTenant) {
        debug("Authenticating user is an admin: " + isAdminRealm);
        SLIPrincipal principal = users.locate(tenant, attributes.getFirst("userId"), attributes.getFirst("userType"));
        String userName = getUserNameFromEntity(principal.getEntity());
        if (userName != null) {
            principal.setName(userName);
        } else {
            if (isAdminRealm || isDevRealm) {
                principal.setFirstName(attributes.getFirst("givenName"));
                principal.setLastName(attributes.getFirst("sn"));
                principal.setVendor(attributes.getFirst("vendor"));
                principal.setEmail(attributes.getFirst("mail"));
            }
            principal.setName(attributes.getFirst("userName"));
        }

        // Cache realm edOrg for security events.
        principal.setRealmEdOrg(targetEdOrg);

        principal.setRealm(realm.getEntityId());
        principal.setEdOrg(attributes.getFirst("edOrg"));
        principal.setAdminRealm(attributes.getFirst("edOrg"));

        if (SLIPrincipal.NULL_ENTITY_ID.equals(principal.getEntity().getEntityId()) && !(isAdminRealm || isDevRealm)) {
            // If we couldn't find an Entity for the user and this isn't an admin realm, then we have no valid user.
            throw new APIAccessDeniedException("Invalid user.", realm);
        }

        if (!(isAdminRealm || isDevRealm) && !realmHelper.isUserAllowedLoginToRealm(principal.getEntity(), realm)) {
            throw new APIAccessDeniedException("User is not associated with realm.", realm);
        }

        setPrincipalRoles(principal, attributes, realm, tenant, isAdminRealm, isDevRealm);

        if (samlTenant != null) {
            principal.setTenantId(samlTenant);
            TenantContext.setTenantId(samlTenant);
            TenantContext.setIsSystemCall(false);
            NeutralQuery idQuery = new NeutralQuery();
            idQuery.addCriteria(new NeutralCriteria("stateOrganizationId", NeutralCriteria.OPERATOR_EQUAL, principal.getEdOrg()));
            Entity edOrg = repo.findOne(EntityNames.EDUCATION_ORGANIZATION, idQuery);
            if (edOrg != null) {
                principal.setEdOrgId(edOrg.getEntityId());
            } else {
                debug("Failed to find edOrg with stateOrganizationID {} and tenantId {}", principal.getEdOrg(),
                        principal.getTenantId());
            }
        }

        if ((sandboxTenant != null) && isDevRealm) {
            principal.setSandboxTenant(sandboxTenant);
        }

        return principal;
    }

    private void setPrincipalRoles(SLIPrincipal principal, LinkedMultiValueMap<String, String> attributes, Entity realm, String tenant,
            boolean isAdminRealm, boolean isDevRealm) {
        List<String> roles = attributes.get("roles");
        if (roles == null || roles.isEmpty()) {
            error("Attempted login by a user that did not include any roles in the SAML Assertion.");
            throw new APIAccessDeniedException("Invalid user. No roles specified for user.", realm);
        }

        Set<Role> sliRoleSet = resolver.mapRoles(tenant, realm.getEntityId(), roles, isAdminRealm);
        List<String> sliRoleList = new ArrayList<String>();
        boolean isAdminUser = true;
        for (Role role : sliRoleSet) {
            sliRoleList.addAll(role.getName());
            if (!role.isAdmin()) {
                isAdminUser = false;
                break;
            }
        }

        if (!(isAdminRealm || isDevRealm) &&
                (principal.getUserType() == null || principal.getUserType().equals("") || principal.getUserType().equals(EntityNames.STAFF))) {
            Map<String, List<String>> sliEdOrgRoleMap = edOrgRoleBuilder.buildValidStaffRoles(realm.getEntityId(), principal.getEntity().getEntityId(), tenant, roles);
            principal.setEdOrgRoles(sliEdOrgRoleMap);
            Set<String> allRoles = new HashSet<String>();
            for (List<String> roleList : sliEdOrgRoleMap.values()) {
                allRoles.addAll(roleList);
            }
            principal.setRoles(new ArrayList<String>(allRoles));
        } else {
            principal.setRoles(sliRoleList);
            if (principal.getRoles().isEmpty()) {
                debug("Attempted login by a user that included no roles in the SAML Assertion that mapped to any of the SLI roles.");
                throw new APIAccessDeniedException(
                        "Invalid user.  No valid role mappings exist for the roles specified in the SAML Assertion.", realm);
            }
        }
        principal.setAdminUser(isAdminUser);
        principal.setAdminRealmAuthenticated(isAdminRealm || isDevRealm);
    }

    private String getUserNameFromEntity(Entity entity) {
        if (entity != null) {
            @SuppressWarnings("rawtypes")
            Map nameMap = (Map) entity.getBody().get("name");
            if (nameMap != null) {
                StringBuffer name = new StringBuffer();
                if (nameMap.containsKey("personalTitlePrefix")) {
                    name.append((String) nameMap.get("personalTitlePrefix"));
                    name.append(" ");
                }
                name.append((String) nameMap.get("firstName"));
                name.append(" ");
                name.append((String) nameMap.get("lastSurname"));
                if (nameMap.containsKey("generationCodeSuffix")) {
                    name.append(" ");
                    name.append((String) nameMap.get("generationCodeSuffix"));
                }
                return name.toString();
            }
        }
        return null;
    }

    private void auditSuccessfulLogin(SLIPrincipal principal, Entity session, URI requestUri, String authorizationCode, Entity realm, Map<String, Object> appSession, String tenant) {
        TenantContext.setIsSystemCall(true);

        ObjectMapper jsoner = new ObjectMapper();
        Map<String, Object> mapForm = jsoner.convertValue(principal, Map.class);
        mapForm.remove("entity");
        if (!mapForm.containsKey("userType")) {
            mapForm.put("userType", EntityNames.STAFF);
        }
        session.getBody().put("principal", mapForm);
        sessionManager.updateSession(session);

        SecurityEvent successfulLogin = securityEventBuilder.createSecurityEvent(this.getClass().getName(), requestUri, "", principal, null, realm, null, true);
        successfulLogin.setOrigin(httpServletRequest.getRemoteHost()+ ":" + httpServletRequest.getRemotePort());
        successfulLogin.setCredential(authorizationCode);
        successfulLogin.setUserOrigin(httpServletRequest.getRemoteHost()+ ":" + httpServletRequest.getRemotePort());
        successfulLogin.setLogLevel(LogLevelType.TYPE_INFO);
        successfulLogin.setRoles(principal.getRoles());

        String applicationDetails = null;
        if (appSession != null){
            String clientId = (String)appSession.get("clientId");
            if (clientId != null) {
                NeutralQuery appQuery = new NeutralQuery();
                appQuery.setOffset(0);
                appQuery.setLimit(1);
                appQuery.addCriteria(new NeutralCriteria("client_id", "=", clientId));
                Entity application = repo.findOne("application", appQuery);
                if (application != null) {
                    Map<String, Object> body = application.getBody();
                    if (body != null) {
                        String name                = (String) body.get("name");
                        String createdBy           = (String) body.get("created_by");
                        successfulLogin.setAppId(name+"," + clientId);
                        applicationDetails         = String.format("%s by %s", name, createdBy);
                    }
                }
            }
        }
        successfulLogin.setUser(principal.getExternalId());
        successfulLogin.setLogMessage(principal.getExternalId() + " from tenant " + tenant + " logged successfully into " + applicationDetails + ".");

        audit(successfulLogin);
    }

    private Response getLoginResponse(Map<String, Object> appSession, String authorizationCode, URI requestUri, String sessionId) {
        Object state = appSession.get("state");
        Boolean isInstalled = (Boolean) appSession.get("installed");
        if (isInstalled) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("authorization_code", authorizationCode);
            if (state != null) {
                resultMap.put("state", state);
            }
            info("Sending back authorization token for installed app: {}", authorizationCode);

            return Response.ok(resultMap).build();
        } else {
            String redirectUri = (String) appSession.get("redirectUri");
            UriBuilder builder = UriBuilder.fromUri(redirectUri);
            builder.queryParam("code", authorizationCode);
            if (state != null) {
                builder.queryParam("state", state);
            }

            boolean runningSsl = requestUri.getScheme().equals("https");
            URI redirect = builder.build();

            return Response.status(CustomStatus.FOUND)
                    .cookie(new NewCookie("_tla", sessionId, "/", apiCookieDomain, "", -1, runningSsl))
                    .location(redirect).build();
        }
    }

    /**
     * Get metadata describing saml federation.
     * This is an unsecured (public) resource.
     *
     * @return Response containing saml metadata
     */
    @GET
    @Path("metadata")
    @Produces({ "text/xml" })
    public Response getMetadata() {

        if (!metadata.isEmpty()) {
            return Response.ok(metadata).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Handles artifact binding request.
     * @param request
     * @param uriInfo
     * @return
     */
    @GET
    @Path("sso/artifact")
    public Response processArtifactBinding(@Context HttpServletRequest request, @Context UriInfo uriInfo) {
        String artifact = request.getParameter("SAMLart");
        String realmId = request.getParameter("RelayState");

        String artifactUrl = getArtifactUrl(realmId, artifact);

        if (artifact == null) {
            throw new APIAccessDeniedException("No artifact provided by the IdP");
        }

        ArtifactResolve artifactResolve = artifactBindingHelper.generateArtifactResolveRequest(artifact, pkEntry);
        Envelope soapEnvelope = artifactBindingHelper.generateSOAPEnvelope(artifactResolve);

        XMLObject response = soapHelper.sendSOAPCommunication(soapEnvelope, artifactUrl);

        ArtifactResponse artifactResponse = (ArtifactResponse)((EnvelopeImpl) response).getBody().getUnknownXMLObjects().get(0);
        org.opensaml.saml2.core.Response samlResponse = (org.opensaml.saml2.core.Response) artifactResponse.getMessage();

        return processResponse(uriInfo, samlResponse);
    }

    private String getArtifactUrl(String realmId, String artifact) {

        SAML2ArtifactBuilderFactory builder  = new SAML2ArtifactBuilderFactory();
        SAML2ArtifactType0004 art = (SAML2ArtifactType0004) builder.buildArtifact(artifact);
        byte[] sourceId = art.getSourceID();
        String hexSource = DatatypeConverter.printHexBinary(sourceId);

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);

        neutralQuery.addCriteria(new NeutralCriteria("idp.sourceId", "=", hexSource));
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", realmId));

        Entity realm = repo.findOne("realm", neutralQuery);
        if (realm == null) {
            raiseSamlValidationError("Invalid realm: " + realmId, null);
        }
        Map<String, Object> idp = (Map<String, Object>) realm.getBody().get("idp");
        if (idp == null) {
            raiseSamlValidationError("Idp information is not correctly set up for realm: " + realmId, null);
        }
        String artifactUrl = (String) idp.get("artifactResolutionEndpoint");
        if (artifactUrl == null || artifactUrl.isEmpty()) {
            raiseSamlValidationError("Artifact Resolution Endpoint is no configured for the realm: " + realmId, null);
        }
        return artifactUrl;
    }

    private Response processResponse(UriInfo uriInfo, org.opensaml.saml2.core.Response samlResponse) {
        samlHelper.validateCertificate(samlResponse);

        String inResponseTo = samlResponse.getInResponseTo();
        Entity session = sessionManager.getSessionForSamlId(inResponseTo);
        String requestedRealmId = (String) session.getBody().get("requestedRealmId");
        Entity realm = getRealm(samlResponse.getIssuer().getValue(), requestedRealmId);
        String targetEdOrg = getTargetEdOrg(realm);

        Assertion assertion = samlResponse.getAssertions().get(0);

        Conditions conditions = assertion.getConditions();
        if(conditions != null) {
            String notBefore = conditions.getNotBefore() == null ? null : conditions.getNotBefore().toString();
            String notOnOrAfter = conditions.getNotOnOrAfter() == null ? null : conditions.getNotOnOrAfter().toString();
            validateTimeRange("SAML Conditions failed.", notBefore, notOnOrAfter, targetEdOrg);
        }

        Subject subject = assertion.getSubject();
        if (subject != null) {
            SubjectConfirmationData subjectConfirmationData = subject.getSubjectConfirmations().get(0).getSubjectConfirmationData();
            String notBefore = subjectConfirmationData.getNotBefore() == null ? null : subjectConfirmationData.getNotBefore().toString();
            String notOnOrAfter = subjectConfirmationData.getNotOnOrAfter() == null ? null : subjectConfirmationData.getNotOnOrAfter().toString();

            validateSubject(uriInfo.getAbsolutePath(), subjectConfirmationData.getRecipient(), notBefore, notOnOrAfter, targetEdOrg);
        } else {
            raiseSamlValidationError("SAML response is missing Subject.", targetEdOrg);
        }

        LinkedMultiValueMap<String, String> attributes = samlHelper.extractAttributesFromResponse(assertion);

        return authenticateUser(attributes, realm, targetEdOrg, inResponseTo, session, uriInfo.getRequestUri());
    }



    private String fetchCertificateText() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();

        Base64 encoder = new Base64(64);
        String certificateText = new String(encoder.encode(certificate.getEncoded()));

        return certificateText;
    }


    /**
     * Check that the current time is within the specified range.
     *
     * @param notBefore
     *            - can be null to skip before check
     * @param notOnOrAfter
     *            - can be null to skip after check
     *
     * @return true if in range, false otherwise
     */
    private boolean isTimeInRange(String notBefore, String notOnOrAfter) {
        DateTime currentTime = new DateTime(DateTimeZone.UTC);

        if (notBefore != null) {
            DateTime calNotBefore = DateTime.parse(notBefore);
            if (currentTime.isBefore(calNotBefore)) {
                debug("{} is before {}.", currentTime, calNotBefore);
                return false;
            }
        }

        if (notOnOrAfter != null) {
            DateTime calNotOnOrAfter = DateTime.parse(notOnOrAfter);
            if (currentTime.isAfter(calNotOnOrAfter) || currentTime.isEqual(calNotOnOrAfter)) {
                debug("{} is on or after {}.", currentTime, calNotOnOrAfter);
                return false;
            }
        }
        return true;
    }

    private Template getTemplate() {
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine velocityEngine = new VelocityEngine(props);
        velocityEngine.init();

        return velocityEngine.getTemplate(METADATA_TEMPLATE);
    }

    Repository<Entity> getRepository() {
        return repo;
    }

    public void setSecurityEventBuilder(SecurityEventBuilder securityEventBuilder) {
        this.securityEventBuilder = securityEventBuilder;
    }

}
