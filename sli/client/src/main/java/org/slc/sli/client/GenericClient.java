package org.slc.sli.client;

import java.net.URI;
import java.util.ArrayList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.client.config.ClientJacksonContextResolver;
import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Default implementation of a generic SLIClient
 * 
 * @author nbrown
 * 
 */
@Component
public class GenericClient implements SliClient {
    /*
     * TODO
     * At this point, supporting a new entity type requires the following:
     * --The PathEntry.getRootPath has to be updated to show the new api paths
     * ----Eventually this should be able to 'discover' the paths through the root resource
     * --The getID method has to be updated to handle the new resource type
     * ----This is going to have to be adjusted eventually as well
     */
    
    private static final Logger LOG = LoggerFactory.getLogger(GenericClient.class);
    
    private PathEntry root = PathEntry.getRootPath();
    private final Client client;
    private final String sliServer;
    
    @Autowired
    private SliParser sliParser;
    
    private Format defaultFormat = Format.JSON;
    
    public GenericClient(String sliServer, String userName, String password) {
        ClientConfig cc = new DefaultClientConfig();
        ClientFilter filter = new HTTPBasicAuthFilter(userName, password);
        cc.getClasses().add(ClientJacksonContextResolver.class);
        client = Client.create(cc);
        client.addFilter(filter);
        this.sliServer = sliServer;
    }
    
    public boolean resourceTypeSupported(Class<?> resourceType) {
        return root.getChildren().keySet().contains(resourceType);
    }
    
    @Override
    public <T> Iterable<T> list(Class<T> resourceType) {
        if (resourceTypeSupported(resourceType)) {
            WebResource resource = getListResourcePath(root, resourceType);
            Format format = getDefaultFormat();
            String mediaType = format.getMediaType();
            String response = resource.accept(mediaType).get(String.class);
            LOG.debug("Response to list is {}", response);
            return sliParser.parseList(response, format, resourceType);
        } else {
            LOG.warn("Resources of type " + resourceType + " are not supported");
            return new ArrayList<T>();
        }
    }
    
    private <T> String getListPath(PathEntry from, Class<T> resourceType) {
        return sliServer + from.getPath() + from.getChildren().get(resourceType).getPath();
    }
    
    private <T> String getPath(PathEntry from, Class<T> resourceType, String id) {
        return getListPath(from, resourceType) + "/" + id;
    }
    
    private <T> WebResource getListResourcePath(PathEntry from, Class<T> resourceType) {
        return client.resource(getListPath(from, resourceType));
    }
    
    private <T> WebResource getListResourcePath(Class<T> resourceType) {
        return getListResourcePath(root, resourceType);
    }
    
    private <T> WebResource getResourcePath(PathEntry from, Class<T> resourceType, String id) {
        return client.resource(getPath(from, resourceType, id));
    }
    
    private <T> WebResource getResourcePath(Class<T> resourceType, String id) {
        return getResourcePath(root, resourceType, id);
    }
    
    private <T> WebResource getRelationPath(Class<?> resourceType, String id, Class<T> relativeType) {
        PathEntry from = root.getChildren().get(resourceType);
        return client.resource(getPath(root, resourceType, id) + "/" + from.getChildren().get(relativeType).getPath());
    }
    
    private WebResource getRelationshipPath(Object resource, Object relative) {
        String pathString = getRelationshipPathString(resource, relative);
        return client.resource(pathString);
    }
    
    private WebResource getRelationPath(Object resource, Object relative, Object relation) {
        return client.resource(getRelationshipPathString(resource, relative) + "/" + getID(relation));
    }
    
    private String getRelationshipPathString(Object resource, Object relative) {
        Class<?> resourceType = resource.getClass();
        String id = getID(resource);
        String relativeID = getID(relative);
        Class<?> relativeType = relative.getClass();
        PathEntry from = root.getChildren().get(resourceType);
        String pathString = getPath(root, resourceType, id) + "/" + from.getChildren().get(relativeType).getPath()
                + "/" + relativeID;
        return pathString;
    }
    
    private String getID(Object resource) {
        // TODO make this smarter
        if (resource instanceof Student) {
            return Integer.toString(((Student) resource).getStudentId());
        } else if (resource instanceof School) {
            return Integer.toString(((School) resource).getSchoolId());
        } else if (resource instanceof StudentSchoolAssociation) {
            return Integer.toString(((StudentSchoolAssociation) resource).getAssociationId());
        } else {
            return resource.toString();
        }
    }
    
    @Override
    public <T> boolean deleteResource(Class<T> resourceType, String id) {
        WebResource webResource = getResourcePath(resourceType, id);
        return deleteResource(webResource);
    }
    
    @Override
    public <T> boolean deleteResource(T resource) {
        return deleteResource(resource.getClass(), getID(resource));
    }
    
    private boolean deleteResource(WebResource webResource) {
        try {
            webResource.delete();
        } catch (UniformInterfaceException e) {
            LOG.error("Exception while doing delete, got response" + e.getResponse());
            return false;
        }
        return true;
    }
    
    @Override
    public <T> T addNewResource(T resource) {
        @SuppressWarnings("unchecked")
        Class<? extends T> resourceClass = (Class<? extends T>) resource.getClass();
        WebResource webResource = getListResourcePath(resourceClass);
        String mediaType = getDefaultFormat().getMediaType();
        try {
            ClientResponse response = webResource.accept(mediaType).type(mediaType)
                    .post(ClientResponse.class, resource);
            LOG.debug("Got response {}", response);
            URI newLocation = response.getLocation();
            WebResource newWebResource = client.resource(newLocation);
            T newResource = doGet(resourceClass, newWebResource);
            return newResource;
        } catch (UniformInterfaceException e) {
            LOG.error("Exception while doing post, got response" + e.getResponse());
            return null;
        }
    }
    
    private boolean updateResource(Object resource, String id) {
        WebResource webResource = getResourcePath(resource.getClass(), id);
        return putResource(resource, webResource);
    }
    
    @Override
    public boolean updateResource(Object resource) {
        return updateResource(resource, getID(resource));
    }
    
    @Override
    public <T> T getResource(Class<T> resourceType, String id) {
        WebResource resource = getResourcePath(resourceType, id);
        return doGet(resourceType, resource);
    }
    
    private <T> T doGet(Class<T> resourceType, WebResource resource) {
        Format format = getDefaultFormat();
        String mediaType = format.getMediaType();
        String response = resource.accept(mediaType).get(String.class);
        LOG.debug("Response to get is {}", response);
        return sliParser.parse(response, format, resourceType);
    }
    
    @Override
    public <T> Iterable<T> getAssociated(Object resource, Class<T> relativeType) {
        Format format = getDefaultFormat();
        WebResource webresource = getRelationPath(resource.getClass(), getID(resource), relativeType);
        String mediaType = format.getMediaType();
        ClientResponse response = webresource.accept(mediaType).head();
        LOG.debug("Response to getAssociated is {}", response);
        if (response.getStatus() == 204) {
            return new LinkIterable<T>(response, "*", relativeType, client, sliParser);
        } else {
            return sliParser.parseList(webresource.get(String.class), format, relativeType);
        }
    }
    
    @Override
    public <T> Iterable<T> getAssociations(Object resource, Object relative, Class<T> assocationType) {
        Format format = getDefaultFormat();
        WebResource webResource = getRelationshipPath(resource, relative);
        String mediaType = format.getMediaType();
        ClientResponse response = webResource.accept(mediaType).head();
        LOG.debug("Response to getAssociations is {}", response);
        if (response.getStatus() == 204) {
            return new LinkIterable<T>(response, assocationType.getSimpleName(), assocationType, client, sliParser);
        } else {
            return sliParser.parseList(webResource.get(String.class), format, assocationType);
        }
    }
    
    @Override
    public <T> T associate(Object resource, Object relative, T relation) {
        Format format = getDefaultFormat();
        WebResource webResource = getRelationshipPath(resource, relative);
        String mediaType = format.getMediaType();
        ClientResponse resp = webResource.type(mediaType).post(ClientResponse.class, relation);
        LOG.debug("Response to associate is {}", resp);
        if (resp.getStatus() < 300) {
            URI newLocation = resp.getLocation();
            WebResource newWebResource = client.resource(newLocation);
            @SuppressWarnings("unchecked")
            Class<? extends T> relationClass = (Class<? extends T>) relation.getClass();
            T newResource = doGet(relationClass, newWebResource);
            return newResource;
        }
        return null;
    }
    
    @Override
    public boolean disassociate(Object resource, Object relative, Object relation) {
        return deleteResource(getRelationPath(resource, relative, relation));
    }
    
    @Override
    public boolean reassociate(Object resource, Object relative, Object relation) {
        WebResource webResource = getRelationPath(resource, relative, relation);
        return putResource(relation, webResource);
    }
    
    @Override
    public Format getDefaultFormat() {
        return defaultFormat;
    }
    
    @Override
    public void setDefaultFormat(Format format) {
        this.defaultFormat = format;
        
    }
    
    private boolean putResource(Object resourceObject, WebResource resourcePath) {
        String mediaType = getDefaultFormat().getMediaType();
        try {
            ClientResponse response = resourcePath.accept(mediaType).type(mediaType)
                    .put(ClientResponse.class, resourceObject);
            LOG.debug("Got response {}", response);
        } catch (UniformInterfaceException e) {
            LOG.error("Exception while doing delete, got response" + e.getResponse());
            return false;
        }
        return true;
    }
}
