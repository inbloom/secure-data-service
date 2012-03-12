package org.slc.sli.api.resources.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.oauth.MongoAuthorizationCodeServices;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

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
    private EntityRepository repo;
    
    @Autowired
    private UserLocator users;
    
    @Autowired
    private SamlAttributeTransformer transformer;
    
    @Value("${sli.security.sp.issuerName}")
    private String metadataSpIssuerName;
    
    @Autowired
    private MongoAuthorizationCodeServices authCodeServices;
    
    @Value("classpath:saml/samlMetadata.xml.template")
    private Resource metadataTemplateResource;
    
    private String metadata;
    
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
        
        LOG.info("Received a SAML post...");
        
        Document doc = saml.decodeSamlPost(postData);
        
        // String msgId = doc.getRootElement().getAttributeValue("ID");
        String inResponseTo = doc.getRootElement().getAttributeValue("InResponseTo");
        String issuer = doc.getRootElement().getChildText("Issuer", SamlHelper.SAML_NS);
        
        Entity realm = fetchOne("realm", new Query(Criteria.where("body.idp.id").is(issuer)));
        
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
        
        // TODO change everything authRealm to use issuer instead of authRealm
        
        final SLIPrincipal principal = users.locate((String) realm.getBody().get("regionId"), attributes.getFirst("userId"));
        principal.setName(attributes.getFirst("userName"));
        principal.setRoles(attributes.get("roles"));
        principal.setRealm(realm.getEntityId());
        String redirect = authCodeServices.createAuthorizationCodeForMessageId(inResponseTo, principal);
        
        return Response.temporaryRedirect(URI.create(redirect)).build();
    }
    
    @POST
    @Path("slo/post")
    public void processSingleLogoutPost() {
        // TODO slo will post something here, what? Need those arguments
    }
    
    private Entity fetchOne(String collection, Query query) {
        Iterable<Entity> results = repo.findByQuery(collection, query, 0, 1);
        
        if (!results.iterator().hasNext()) {
            throw new EntityNotFoundException("Not found");
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
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getMetadata() {
        
        if (!metadata.isEmpty()) {
            return Response.ok(metadata).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
        
    }
}
