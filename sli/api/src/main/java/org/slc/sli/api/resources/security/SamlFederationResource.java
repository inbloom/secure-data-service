package org.slc.sli.api.resources.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.MongoAuthorizationCodeServices;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.api.util.OAuthTokenUtil;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(SamlFederationResource.class);
    private static final String IDP_SLO_POST_ENDPOINT = "idp.sloPostEndpoint";
    
    @Autowired
    private SamlHelper saml;
    
    @Autowired
    private Repository<Entity> repo;
    
    @Autowired
    private UserLocator users;
    
    @Autowired
    private SamlAttributeTransformer transformer;
    
    @Autowired
    private MongoAuthorizationCodeServices authCodeServices;
    
    @Autowired
    private OAuthTokenUtil oAuthTokenUtil;
    
    @Value("${sli.security.sp.issuerName}")
    private String metadataSpIssuerName;
    
    @Value("classpath:saml/samlMetadata.xml.template")
    private Resource metadataTemplateResource;
    
    private String metadata;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void processMetadata() throws IOException {
        InputStream is = metadataTemplateResource.getInputStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        is.close();
        
        metadata = writer.toString();
        metadata = metadata.replaceAll("\\$\\{sli\\.security\\.sp.issuerName\\}", metadataSpIssuerName);
    }
    
    @POST
    @Path("sso/post")
    @SuppressWarnings("unchecked")
    public Response consume(@FormParam("SAMLResponse") String postData,
            @Context HttpServletResponse response) throws Exception {
        
        LOG.info("Received a SAML post for SSO...");
        
        Document doc = saml.decodeSamlPost(postData);
        
        String inResponseTo = doc.getRootElement().getAttributeValue("InResponseTo");
        String samlMessageId = doc.getRootElement().getAttributeValue("ID");
        String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", issuer));
        Entity realm = fetchOne("realm", neutralQuery);
        
        if (realm == null) {
            throw new IllegalStateException("Failed to locate realm: " + issuer);
        }
        
        org.jdom.Element stmt = doc.getRootElement().getChild("Assertion", SamlHelper.SAML_NS).getChild("AttributeStatement", SamlHelper.SAML_NS);
        List<org.jdom.Element> attributeNodes = stmt.getChildren("Attribute", SamlHelper.SAML_NS);
        
        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();
        for (org.jdom.Element attributeNode : attributeNodes) {
            String samlAttributeName = attributeNode.getAttributeValue("Name");
            List<org.jdom.Element> valueNodes = attributeNode.getChildren("AttributeValue", SamlHelper.SAML_NS);
            for (org.jdom.Element valueNode : valueNodes) {
                attributes.add(samlAttributeName, valueNode.getText());
            }
        }
        
        // Apply transforms
        attributes = transformer.apply(realm, attributes);
        
        SLIPrincipal principal = users.locate((String) realm.getBody().get("tenantId"), attributes.getFirst("userId"));
        principal.setName(attributes.getFirst("userName"));
        principal.setRoles(attributes.get("roles"));
        principal.setRealm(realm.getEntityId());
        principal.setAdminRealm(attributes.getFirst("adminRealm"));
        
        // create sessionIndex
        String sessionIndex = samlMessageId;        
        String redirect = authCodeServices.createAuthorizationCodeForMessageId(inResponseTo, principal, sessionIndex);
        
        // create cookie here corresponding to session passed into authorization code above
        // TODO: make this into an http-only cookie
        Cookie cookie = new Cookie("_tla", sessionIndex);
        cookie.setMaxAge(60 * 60);
        cookie.setDomain(".slidev.org");
        cookie.setPath("/");
        response.addCookie(cookie);
        
        String edOrg = attributes.getFirst("edOrg");
        
        // TODO: This is a temporary hack because of how we're storing the edOrg in LDAP.
        // Once we have a dedicated edOrg attribute, we can strip out this part
        if (edOrg != null && edOrg.indexOf(' ') > -1) {
            edOrg = edOrg.substring(0, edOrg.indexOf(' '));
        }
        principal.setEdOrg(edOrg);
        
        return Response.temporaryRedirect(URI.create(redirect)).build();
    }
    
    /**
     * Location for IDP-initiated log out. Example of what is asserted:
     * <saml2p:LogoutRequest
     * xmlns:saml2p="urn:oasis:names:tc:SAML:2.0:protocol"
     * ID="21B78E9C6C8ECF16F01E4A0F15AB2D46"
     * IssueInstant="2010-04-28T21:36:11.230Z"
     * Version="2.0">
     * <saml2:Issuer
     * xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">https://dloomac.service-now.
     * com</saml2:Issuer>
     * <saml2:NameID xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion"
     * Format="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"
     * NameQualifier="http://idp.ssocircle.com"
     * SPNameQualifier="https://dloomac.service-now.com/navpage.do">
     * david.loo@service-now.com
     * </saml2:NameID>
     * <saml2p:SessionIndex>s211b2f811485b2a1d2cc4db2b271933c286771104</saml2p:SessionIndex>
     * </saml2p:LogoutRequest>
     */
    @POST
    @Path("slo/post")
    public Response processSingleLogoutPost(@FormParam(value = "SAMLRequest") String requestData) throws Exception {
        
        if (requestData == null) {
            return Response.noContent().build();
        }
        
        LOG.debug("Received a SAML Request post for SLO via slo/post...");
        Document doc = saml.decodeSamlPost(requestData);
        XMLOutputter outputter = new XMLOutputter();
        try {
            outputter.output(doc, System.out);
        } catch (IOException e) {
            System.err.println(e);
        }
        
        String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);
        String nameId = doc.getRootElement().getChildText("NameID", SamlHelper.SAML_NS);
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", issuer));
        Entity realm = fetchOne(EntityNames.REALM, neutralQuery);
        
        if (realm == null) {
            throw new IllegalStateException("Failed to locate realm: " + issuer);
        }
        
        String realmId = realm.getEntityId();
        
        Map<String, Object> logoutSuccess = new HashMap<String, Object>();
        if (oAuthTokenUtil.deleteTokensForUser(nameId, realmId)) {
            logoutSuccess.put("logout", true);
        } else {
            logoutSuccess.put("logout", false);
        }
        
        return Response.ok(logoutSuccess).build();
    }
    
    @GET
    @Path("slo/redirect")
    public void consumeSloRedirect(@QueryParam("SAMLResponse") String samlResponse, @QueryParam("SAMLRequest") String samlRequest) {
        LOG.debug("Received a SAMLResponse post for SLO via slo/redirect...");
        this.saml.decodeSamlRedirect(samlRequest);
    }
    
    public boolean logoutOfIdp() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication oAuth = ((OAuth2Authentication) auth).getUserAuthentication();
        SLIPrincipal principal = (SLIPrincipal) oAuth.getPrincipal();
        
        String postUrl = getSloEndpoint(principal.getRealm());
        String nameId = principal.getName();
        String sessionIndex = (String) oAuth.getDetails();
        
        LOG.debug("----------------------------------------------------------------------------------");
        LOG.debug("logging out principal: {}@{}", nameId, principal.getRealm());
        LOG.debug(" from idp: {}", postUrl);
        LOG.debug(" with session: {}", sessionIndex);
        
        String logoutRequest = saml.createSignedSamlLogoutRequest(postUrl, nameId, sessionIndex);
        LOG.debug(" and logout request:");
        LOG.debug(logoutRequest);
        LOG.debug("----------------------------------------------------------------------------------");
        
        try {
            Response response = Response.temporaryRedirect(new URI(postUrl + "?SAMLRequest=" + logoutRequest)).build();
            LOG.debug("response:");
            LOG.debug(" status:" + response.getStatus());
            LOG.debug(" entity: " + response.getEntity());
            LOG.debug(" metadata: " + response.getMetadata());
            return true;
        } catch (URISyntaxException e) {
            // TODO do better logging
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean logoutOfIdp(SLIPrincipal principal) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication oAuth = ((OAuth2Authentication) auth).getUserAuthentication();
        // SLIPrincipal principal = (SLIPrincipal) oAuth.getPrincipal();
        
        String postUrl = getSloEndpoint(principal.getRealm());
        String nameId = principal.getName();
        String sessionIndex = (String) oAuth.getDetails();
        String logoutRequest = saml.createSignedSamlLogoutRequest(postUrl, nameId, sessionIndex);
        
        LOG.debug("----------------------------------------------------------------------------------");
        LOG.debug("logging out principal: {}@{}", nameId, principal.getRealm());
        LOG.debug(" from idp: {}", postUrl);
        LOG.debug(" with session: {}", sessionIndex);
        LOG.debug(" and logout request:");
        LOG.debug(logoutRequest);
        LOG.debug("----------------------------------------------------------------------------------");
                
        try {
            Response response = Response.temporaryRedirect(new URI(postUrl + "?SAMLRequest=" + logoutRequest)).build();
            LOG.debug("response:");
            LOG.debug(" status:" + response.getStatus());
            LOG.debug(" entity: " + response.getEntity());
            LOG.debug(" metadata: " + response.getMetadata());
            return true;
        } catch (URISyntaxException e) {
            LOG.error("failed to create uri to send logout request");
        }
        
//        String encodedLogoutRequest = saml.createSamlLogoutRequest(principal.getRealm(), principal.getName());
//        String sloEndpoint = getSloEndpoint(principal.getRealm());
//        String url = sloEndpoint + "?SAMLRequest=" + encodedLogoutRequest;
//        
//        RestTemplate restTemplate = new RestTemplate();
//        
//        @SuppressWarnings("unused")
//        HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        
        // TODO response parse
        // TODO return response.isSuccess();
        
        return false;
    }
    
    private String getSloEndpoint(String realmId) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", realmId));
        Entity realm = fetchOne(EntityNames.REALM, neutralQuery);
        
        if (realm == null) {
            throw new IllegalStateException("Failed to locate realm: " + realmId);
        }
        
        return (String) realm.getBody().get(IDP_SLO_POST_ENDPOINT);
    }
    
    @POST
    @Path("slo/response")
    public void processSingleLogoutResponsePost(@FormParam(value = "SAMLResponse") String responseData)
            throws Exception {
        LOG.debug("Received a SAML Response post for SLO via slo/response...");
        Document doc = saml.decodeSamlPost(responseData);
        XMLOutputter outputter = new XMLOutputter();
        try {
            outputter.output(doc, System.out);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    private Entity fetchOne(String collection, NeutralQuery neutralQuery) {
        Iterable<Entity> results = repo.findAll(collection, neutralQuery);
        
        if (!results.iterator().hasNext()) {
            throw new RuntimeException("Not found");
        }
        
        return results.iterator().next();
    }
    
    /**
     * Get metadata describing saml federation.
     * This is an unsecured (public) resource.
     * 
     * @return Response containing saml metadata
     */
    @GET
    @Path("metadata")
    public Response getMetadata() {
        
        if (!metadata.isEmpty()) {
            return Response.ok(metadata).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
        
    }
}
