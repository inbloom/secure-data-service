package org.slc.sli.api.resources.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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
import org.slc.sli.api.security.resolve.ClientRoleResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.security.saml.SamlAttributeTransformer;
import org.slc.sli.api.security.saml.SamlHelper;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
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

    @Autowired
    private ClientRoleResolver roleResolver;

    @Value("${sli.security.sp.issuerName}")
    private String metadataSpIssuerName;

    @Value("classpath:saml/samlMetadata.xml.template")
    private Resource metadataTemplateResource;

    @Value("${sli.api.cookieDomain}")
    private String apiCookieDomain;

    @Context
    private HttpServletRequest httpServletRequest;

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

        Document doc = null;

        try {
            doc = saml.decodeSamlPost(postData);
        } catch (Exception e) {
            SecurityEvent event = new SecurityEvent();

            event.setClassName(this.getClass().toString());
            event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
            event.setTimeStamp(new Date());

            try {
                event.setExecutedOn(InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException ue) {
                LOG.info("Could not find hostname for security event logging!");
            }

            if (this.httpServletRequest != null) {
                event.setUserOrigin(httpServletRequest.getRemoteHost());
                event.setAppId(httpServletRequest.getHeader("User-Agent"));
                event.setActionUri(httpServletRequest.getRequestURI());
                event.setUser(httpServletRequest.getRemoteUser());

                // the origin header contains the uri info of the idp server that sends the SAML data
                event.setLogMessage("SAML message received from " + httpServletRequest.getHeader("Origin")
                        + " is invalid!");
                event.setLogLevel(LogLevelType.TYPE_WARN);
            } else {
                event.setLogMessage("HttpServletRequest is missing, and this should never happen!!");
                event.setLogLevel(LogLevelType.TYPE_ERROR);
            }

            audit(event);

            throw e;
        }

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

        org.jdom.Element stmt = doc.getRootElement().getChild("Assertion", SamlHelper.SAML_NS)
                .getChild("AttributeStatement", SamlHelper.SAML_NS);
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

        SLIPrincipal principal;
        String tenant = (String) realm.getBody().get("tenantId");
        if (tenant == null || tenant.length() < 1) {
            // accept the tenantId from the IDP if and only if the realm's tenantId is null
            tenant = attributes.getFirst("tenant");
            if (tenant == null) {
                LOG.error("No tenant found in either the realm or SAMLResponse. issuer: {}, inResponseTo: {}",
                        issuer, inResponseTo);
                throw new IllegalArgumentException("No tenant found in either the realm or SAMLResponse. issuer: "
                        + issuer + ", inResponseTo: ");
            }
        }
        principal = users.locate(tenant, attributes.getFirst("userId"));
        String userName = getUserNameFromEntity(principal.getEntity());
        if (userName != null) {
            principal.setName(userName);
        } else {
            principal.setName(attributes.getFirst("userName"));
        }

        principal.setRoles(attributes.get("roles"));
        principal.setRealm(realm.getEntityId());
        principal.setEdOrg(attributes.getFirst("edOrg"));
        principal.setAdminRealm(attributes.getFirst("edOrg"));
        principal.setSliRoles(roleResolver.resolveRoles(principal.getRealm(), principal.getRoles()));


        if ("-133".equals(principal.getEntity().getEntityId()) && !(Boolean) realm.getBody().get("admin")) {
            //if we couldn't find an Entity for the user and this isn't an admin realm, then we have no valid user
            throw new RuntimeException("Invalid user");
        }
        
        // {sessionId,redirectURI}
        Pair<String, URI> tuple = this.sessionManager.composeRedirect(inResponseTo, principal);

        return Response.temporaryRedirect(tuple.getRight())
                .cookie(new NewCookie("_tla", tuple.getLeft(), "/", apiCookieDomain, "", 300, false)).build();
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
