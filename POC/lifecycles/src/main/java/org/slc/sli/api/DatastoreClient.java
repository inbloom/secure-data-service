package org.slc.sli.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slc.sli.model.DeterministicUUID;
import org.slc.sli.model.ModelEntity;
import org.slc.sli.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastoreClient {
    
    // Logging
    private static final Logger log = LoggerFactory.getLogger(DatastoreClient.class);
    
    
    // Constants
    public static final String URL_PREFIX = "http://localhost:9999/lifecycles/services";
    public static final String URL_PATH_SEPARATOR = "/";
    public static MediaType[] DEFAULT_CONTENT_TYPES = {MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE};
    public static Class DEFAULT_ENTITY_CLASS = ModelEntity.class;
    
    
    // Attributes
    ClientConfig clientConfig;
    Client client;
    
    
    // Constructors
    public DatastoreClient() {       
        this.init();
    }
    
    
    // Methods
    public void init() {
        clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        client = Client.create(clientConfig);
    }
    
    public String create(String urlPath, ModelEntity entity) {
        return this.create(urlPath, DEFAULT_CONTENT_TYPES, entity);
    }
    
    public String createList(String urlPath, List<ModelEntity> entityList) {
        ModelEntity[] entityArray = new ModelEntity[entityList.size()];
        entityArray = entityList.toArray(entityArray);
        return this.createList(urlPath, entityArray);
    }
    
    public String createList(String urlPath, ModelEntity[] entityArray) {
        return this.create(urlPath, DEFAULT_CONTENT_TYPES, entityArray);
    }
    
    protected String create(String urlPath, MediaType[] contentTypes, Object entity) {
        URI uri = UriBuilder.fromUri(urlPath).build();
        
        MediaType[] mediaTypes = new MediaType[contentTypes.length];
        for (int i=0; i < contentTypes.length; i++) {
            mediaTypes[i] = contentTypes[i];
        }
        
        WebResource service = client.resource(uri);
        Class responseClass = String.class;
        Object responseObject = service.accept(mediaTypes).post(responseClass, entity);

        String response = responseObject.toString();
        
        return response;
    }
    
    public ModelEntity read(String urlPath) {
        return (ModelEntity)this.read(urlPath, DEFAULT_CONTENT_TYPES, DEFAULT_ENTITY_CLASS);
    }
    
    public ModelEntity read(String urlPath, ModelEntity entity) {
        String id = this.generateId(entity);
        if (id != null) {
            urlPath += URL_PATH_SEPARATOR + DatastoreResource.PATH_PARAMETER_ID + DatastoreResource.PATH_PARAMETER_VALUE_SEPARATOR + id;
        }
        return (ModelEntity)this.read(urlPath, DEFAULT_CONTENT_TYPES, DEFAULT_ENTITY_CLASS);
    }
    
    public List<Map> readList(String urlPath) {
        return (List<Map>)this.read(urlPath, DEFAULT_CONTENT_TYPES, List.class);
    }
    
    protected Object read(String urlPath, MediaType[] contentTypes, Class entityClass) {
        URI uri = UriBuilder.fromUri(urlPath).build();
        
        MediaType[] mediaTypes = new MediaType[contentTypes.length];
        for (int i=0; i < contentTypes.length; i++) {
            mediaTypes[i] = contentTypes[i];
        }
        
        WebResource service = client.resource(uri);
        Object responseObject = service.accept(mediaTypes).get(entityClass);
        
        return responseObject;
    }
    
    public String update(String urlPath, ModelEntity entity) {
        return this.update(urlPath, DEFAULT_CONTENT_TYPES, entity);
    }
    
    public String updateList(String urlPath, List<ModelEntity> entityList) {
        ModelEntity[] entityArray = new ModelEntity[entityList.size()];
        entityArray = entityList.toArray(entityArray);
        return this.updateList(urlPath, entityArray);
    }
    
    public String updateList(String urlPath, ModelEntity[] entityArray) {
        return this.update(urlPath, DEFAULT_CONTENT_TYPES, entityArray);
    }
    
    protected String update(String urlPath, MediaType[] contentTypes, Object entity) {
        URI uri = UriBuilder.fromUri(urlPath).build();
        
        MediaType[] mediaTypes = new MediaType[contentTypes.length];
        for (int i=0; i < contentTypes.length; i++) {
            mediaTypes[i] = contentTypes[i];
        }
        
        WebResource service = client.resource(uri);
        Class responseClass = String.class;
        Object responseObject = service.accept(mediaTypes).put(responseClass, entity);

        String response = responseObject.toString();
        
        return response;
    }
    
    public void delete(String urlPath) {
        this.delete(urlPath, DEFAULT_CONTENT_TYPES);
    }
    
    public void delete(String urlPath, ModelEntity entity) {
        String id = this.generateId(entity);
        if (id != null) {
            urlPath += URL_PATH_SEPARATOR + DatastoreResource.PATH_PARAMETER_ID + DatastoreResource.PATH_PARAMETER_VALUE_SEPARATOR + id;
        }
        this.delete(urlPath, DEFAULT_CONTENT_TYPES);
    }
    
    public void deleteList(String urlPath) {
        this.delete(urlPath, DEFAULT_CONTENT_TYPES);
    }
    
    protected void delete(String urlPath, MediaType[] contentTypes) {
        URI uri = UriBuilder.fromUri(urlPath).build();
        
        MediaType[] mediaTypes = new MediaType[contentTypes.length];
        for (int i=0; i < contentTypes.length; i++) {
            mediaTypes[i] = contentTypes[i];
        }
        
        WebResource service = client.resource(uri);
        service.accept(mediaTypes).delete();

    }
    
    private String generateId(ModelEntity entity) {
        String id = null;
        
        try {
            id = DeterministicUUID.getUUID(entity, ModelEntity.ID_ATTRIBUTE).toString(); 
        } catch (ModelException modelException) {
            log.error("Deterministic ID generation failed!");
        }
        
        return id;
    }

    // Entry Point
    public static void main( String[] args ) {
        
        log.info("Starting Datastore Client [REST Url Prefix: " + DatastoreClient.URL_PREFIX + "]...");
        
        try {
            
            DatastoreClient datastoreClient = new DatastoreClient();
            List<Map> responseList;
            ModelEntity responseEntity;
            String response = "";
            String[] keys = {"usi"};
            UUID deterministicUUID;
            String type = "ModelEntity";
            int total = 100;
            boolean listEnabled = false;
            
            List<ModelEntity> entityList = new ArrayList<ModelEntity>();
            for (int index = 1; index <= total; index++) {
                ModelEntity entity = new ModelEntity(type, null, new HashMap<String, Object>(), new HashMap<String, Object>(), null, null);
                entity.setKeys(ModelEntity.ID_ATTRIBUTE, keys);
                entity.getBody().put("usi", "" + index);
                entityList.add(entity);
            }
            
            if (listEnabled) {
                
                log.info("Performing Entity List [" + total + "] Operations...");

                responseList = datastoreClient.readList(URL_PREFIX + "/list");        
                log.info("Initial Entities: ");
                log.info(responseList.toString());

                datastoreClient.createList(URL_PREFIX + "/list", entityList);
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Create: ");
                log.info(responseList.toString());

                for (ModelEntity entity : entityList) {
                    entity.getBody().put("hello", "world");
                }
                datastoreClient.updateList(URL_PREFIX + "/list", entityList);
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Update: ");
                log.info(responseList.toString());

                String deleteUrl = URL_PREFIX + "/list";
                deleteUrl += URL_PATH_SEPARATOR + "id:" + "*";
                datastoreClient.delete(deleteUrl);
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Delete: ");
                log.info(responseList.toString());
                
            } else {
                
                log.info("Performing Entity Operations...");

                responseList = datastoreClient.readList(URL_PREFIX + "/list");        
                log.info("Initial Entities: ");
                log.info(responseList.toString());

                datastoreClient.create(URL_PREFIX + "/entity", entityList.get(0));
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Create: ");
                log.info(responseList.toString());
                
                String findUrl = URL_PREFIX + "/entity";
                deterministicUUID = DeterministicUUID.getUUID(entityList.get(0), ModelEntity.ID_ATTRIBUTE);        
                String findId = deterministicUUID.toString();
                findUrl += URL_PATH_SEPARATOR + "id:" + findId;
                responseEntity = datastoreClient.read(findUrl);
                log.info("Find: ");
                log.info(responseEntity.toString());
                
                entityList.get(0).getBody().put("hello", "world");
                datastoreClient.update(URL_PREFIX + "/entity", entityList.get(0));
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Update: ");
                log.info(responseList.toString());

                String deleteUrl = URL_PREFIX + "/entity";
                deterministicUUID = DeterministicUUID.getUUID(entityList.get(0), ModelEntity.ID_ATTRIBUTE);        
                String deleteId = deterministicUUID.toString();
                deleteUrl += URL_PATH_SEPARATOR + "id:" + deleteId;
                datastoreClient.delete(deleteUrl);
                
                responseList = datastoreClient.readList(URL_PREFIX + "/list");
                log.info("After Delete: ");
                log.info(responseList.toString());
                
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
    
}
