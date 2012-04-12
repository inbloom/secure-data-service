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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.security.OauthSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
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
@Path("saml")
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
    private OauthSessionManager sessionManager;
    
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
        principal.setEdOrg(attributes.getFirst("edOrg"));
        
        Pair<String, URI> tuple = this.sessionManager.composeRedirect(inResponseTo, principal);
        
        return Response.temporaryRedirect(tuple.getRight()).cookie(new NewCookie("_tla", tuple.getLeft(), "/", ".slidev.org", "", 300, false)).build();
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
