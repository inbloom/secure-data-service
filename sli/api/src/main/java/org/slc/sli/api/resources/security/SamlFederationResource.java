package org.slc.sli.api.resources.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.MongoAuthorizationCodeServices;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
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
@Path("/saml")
public class SamlFederationResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(SamlFederationResource.class);
    
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
    public Response consume(@FormParam("SAMLResponse") String postData) throws Exception {
        
        LOG.info("Received a SAML post for SSO...");
        
        Document doc = saml.decodeSamlPost(postData);
        
        String inResponseTo = doc.getRootElement().getAttributeValue("InResponseTo");
        String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);
        
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(1);
        neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", issuer));
        Entity realm = fetchOne("realm", neutralQuery);
        
        if (realm == null) {
            throw new IllegalStateException("Failed to locate realm: " + issuer);
        }
        
        Element stmt = doc.getRootElement().getChild("Assertion", SamlHelper.SAML_NS)
                .getChild("AttributeStatement", SamlHelper.SAML_NS);
        List<Element> attributeNodes = stmt.getChildren("Attribute", SamlHelper.SAML_NS);
        
        LinkedMultiValueMap<String, String> attributes = new LinkedMultiValueMap<String, String>();
        for (Element attributeNode : attributeNodes) {
            String samlAttributeName = attributeNode.getAttributeValue("Name");
            List<Element> valueNodes = attributeNode.getChildren("AttributeValue", SamlHelper.SAML_NS);
            for (Element valueNode : valueNodes) {
                attributes.add(samlAttributeName, valueNode.getText());
            }
        }
        
        // Apply transforms
        attributes = transformer.apply(realm, attributes);
        
        SLIPrincipal principal = users.locate((String) realm.getBody().get("regionId"), attributes.getFirst("userId"));
        principal.setName(attributes.getFirst("userName"));
        principal.setRoles(attributes.get("roles"));
        principal.setRealm(realm.getEntityId());
        principal.setAdminRealm(attributes.getFirst("adminRealm"));
        String redirect = authCodeServices.createAuthorizationCodeForMessageId(inResponseTo, principal);
        
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
    public Response processSingleLogoutPost(@FormParam(value = "SAMLRequest") String requestData,
            @FormParam(value = "SAMLResponse") String responseData) throws Exception {
        if (responseData != null) {
            LOG.debug("Received a SAML Response post for SLO via slo/post...");
            Document doc = saml.decodeSamlPost(responseData);
            XMLOutputter outputter = new XMLOutputter();
            try {
                outputter.output(doc, System.out);
            } catch (IOException e) {
                System.err.println(e);
            }
            
            // this will change
            return Response.ok().build();
        } else if (requestData != null) {
            LOG.debug("Received a SAML Request post for SLO via slo/post...");
            Document doc = saml.decodeSamlPost(requestData);
            XMLOutputter outputter = new XMLOutputter();
            try {
                outputter.output(doc, System.out);
            } catch (IOException e) {
                System.err.println(e);
            }
            
            // String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);
            // String nameId = doc.getRootElement().getChildText("NameID", SamlHelper.SAML_NS);
            
            // perform look up by issuer and nameId
            // NeutralQuery neutralQuery = new NeutralQuery();
            // neutralQuery.setOffset(0);
            // neutralQuery.setLimit(1);
            // neutralQuery.addCriteria(new NeutralCriteria("idp.id", "=", issuer));
            // Entity realm = fetchOne("realm", neutralQuery);
            
            // if (realm == null) {
            // throw new IllegalStateException("Failed to locate realm: " + issuer);
            // }
            
            // add logic based on
            // SLIPrincipal principal = users.locate((String) realm.getBody().get("regionId"),
            // nameId);
            
            // if (principal.getEntity() == null) {
            // throw new IllegalStateException("Failed to locate user: " + nameId);
            // }
            
            // remove all OAuth Access Tokens associated with user (for each application)
            
            // send success LogoutResponse via post back to idp
            
            // this will change
            return Response.ok().build();
        } else {
            // send a Logout Request to IDP of current user
            // get user
            // get user idp
            SLIPrincipal principal = new SLIPrincipal();
            String idpEndpoint = "";
            String samlLogoutMessage = saml.createSamlLogoutRequest(idpEndpoint);
            String sloRedirect = idpEndpoint + "?SAMLRequest=" + samlLogoutMessage;
            return Response.temporaryRedirect(URI.create(sloRedirect)).build();
        }
    }
    
    @POST
    @Path("slo/response")
    public void processSingleLogoutResponsePost(@FormParam(value = "SAMLResponse") String responseData)
    // @RequestParam(value = "SAMLResponse", required = false) String responseData)
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
