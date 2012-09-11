package org.slc.sli.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slc.sli.datastore.mongo.SimpleMongoRepository;
import org.slc.sli.model.ModelEntity;
import org.slc.sli.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/")
@Component
@Scope("request")
public class DatastoreResource {
    
    // Logging
    private static Logger log = LoggerFactory.getLogger(DatastoreResource.class);
    
    
    // Constants
    public final static String PATH_PARAMETER_SEPARATOR = "/";
    public final static String PATH_PARAMETER_VALUE_SEPARATOR = ":";
    public final static String PATH_PARAMETER_VALUE_LIST_SEPARATOR = ",";
    public static final String PATH_PARAMETER_ID = "id";
    public final static String WILDCARD_INDICATOR = "*";
    public final static String DEFAULT_MODEL_COLLECTION = "ModelEntity";
    
    
    // Attributes
    @Context
    private ServletContext context;    
    @Context
    private HttpServletRequest request;  
    @Autowired
    private SimpleMongoRepository mongoRepository;
    private String principal = "rbloh";
    
    
    // Constructors
    public DatastoreResource() {
        super();
        this.init();
    }
    
    
    // Methods
    public void init() {
    }
    
    @Path("/entity")
    @POST
    @Produces("application/json")
    public String createEntity(ModelEntity entity) {
        return this.createEntity(entity, "");
    }

    @Path("/entity/{path:.*}")
    @POST
    @Produces("application/json")
    public String createEntity(ModelEntity entity, @PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Create Entity:");
            
            if (entity.getLifecycle() != null) {
                entity = mongoRepository.save(principal, DEFAULT_MODEL_COLLECTION, entity);    
            } else {
                entity = mongoRepository.create(principal, DEFAULT_MODEL_COLLECTION, entity);    
            }
            
            log.info(entity.toString());
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }
        
        return status;
    }

    @Path("/list")
    @POST
    @Produces("application/json")
    public String createEntityList(List<ModelEntity> list) {
        return this.createEntityList(list, "");
    }

    @Path("/list/{path:.*}")
    @POST
    @Produces("application/json")
    public String createEntityList(List<ModelEntity> list, @PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Create List:");
            
            for (ModelEntity entity : list) {
                if (entity.getLifecycle() != null) {
                    entity = mongoRepository.save(principal, DEFAULT_MODEL_COLLECTION, entity);    
                } else {
                    entity = mongoRepository.create(principal, DEFAULT_MODEL_COLLECTION, entity);    
                }
                
                log.info(entity.toString());
                
            }            
        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }
        
        return status;
    }

    @Path("/entity")
    @GET
    @Produces("application/json")
    public ModelEntity getEntity() {
        return this.getEntity("");
    }

    @Path("/entity/{path:.*}")
    @GET
    @Produces("application/json")
    public ModelEntity getEntity(@PathParam("path") String path) {
        ModelEntity entity = null;
        
        try {
            log.info("Find Entity:");
            
            Map<String, String> pathParameters = this.getPathParameters(path);
            String id = pathParameters.get(PATH_PARAMETER_ID);
            if (id == null) {
                throw new ModelException("URL path does not contain a valid id.");
            }
            
            entity = mongoRepository.findById(DEFAULT_MODEL_COLLECTION, id);
            
            log.info(entity.toString());
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }        
        
        return entity;
    }

    @Path("/list")
    @GET
    @Produces("application/json")
    public List<ModelEntity> getEntityList() {
        return this.getEntityList("");
    }

    @Path("/list/{path:.*}")
    @GET
    @Produces("application/json")
    public List<ModelEntity> getEntityList(@PathParam("path") String path) {
        List<ModelEntity> list = new ArrayList<ModelEntity>();
        
        try {
            log.info("Find List:");
            
            list = mongoRepository.findAll(DEFAULT_MODEL_COLLECTION);
            
            log.info(list.toString());
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }        
        
        return list;
    }

    @Path("/entity")
    @PUT
    @Produces("application/json")
    public String updateEntity(ModelEntity entity) {
        return this.updateEntity(entity, "");
    }

    @Path("/entity/{path:.*}")
    @PUT
    @Produces("application/json")
    public String updateEntity(ModelEntity entity, @PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Update Entity:");
            
            if (entity.getLifecycle() != null) {
                entity = mongoRepository.save(principal, DEFAULT_MODEL_COLLECTION, entity);    
            } else {
                entity = mongoRepository.update(principal, DEFAULT_MODEL_COLLECTION, entity);    
            }
            
            log.info(entity.toString());
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }
        
        return status;
    }

    @Path("/list")
    @PUT
    @Produces("application/json")
    public String updateEntityList(List<ModelEntity> list) {
        return this.updateEntityList(list, "");
    }

    @Path("/list/{path:.*}")
    @PUT
    @Produces("application/json")
    public String updateEntityList(List<ModelEntity> list, @PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Update List:");
            
            for (ModelEntity entity : list) {
                if (entity.getLifecycle() != null) {
                    entity = mongoRepository.save(principal, DEFAULT_MODEL_COLLECTION, entity);    
                } else {
                    entity = mongoRepository.update(principal, DEFAULT_MODEL_COLLECTION, entity);    
                }
                
                log.info(entity.toString());
                
            }            
        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }
        
        return status;
    }

    @Path("/entity")
    @DELETE
    @Produces("application/json")
    public void deleteEntity() {
        this.deleteEntity("");
    }

    @Path("/entity/{path:.*}")
    @DELETE
    @Produces("application/json")
    public void deleteEntity(@PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Delete Entity:");
            
            Map<String, String> pathParameters = this.getPathParameters(path);
            String id = pathParameters.get(PATH_PARAMETER_ID);
            if (id == null) {
                throw new ModelException("URL path does not contain a valid id.");
            }
            
            mongoRepository.delete(principal, DEFAULT_MODEL_COLLECTION, id);    
            
            log.info("ID: " + id);
            
        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }        
    }

    @Path("/list")
    @DELETE
    @Produces("application/json")
    public void deleteEntityList() {
        this.deleteEntityList("");
    }

    @Path("/list/{path:.*}")
    @DELETE
    @Produces("application/json")
    public void deleteEntityList(@PathParam("path") String path) {
        String status = "{\"status\" : \"success\"}";
        
        try {
            log.info("Purge:");
            
            Map<String, String> pathParameters = this.getPathParameters(path);
            String id = pathParameters.get("id");
            if (id == null) {
                throw new ModelException("URL path does not contain a valid id.");
            } else if (!(id.equals(WILDCARD_INDICATOR))) {
                throw new ModelException("URL path does not contain a valid wildcard indicator.");
            }
            
            mongoRepository.purge(principal, DEFAULT_MODEL_COLLECTION);    

        } catch (Exception exception) {
            log.error(exception.getMessage());
            status = "{\"status\" : \"exception\", \"message\" : \"" + exception.getMessage() + "\"}";
        }        
    }

    protected Map<String, String> getPathParameters(String path) {
        Map<String, String> pathParameters = new HashMap<String, String>();
        
        StringTokenizer tokenizer = new StringTokenizer(path, PATH_PARAMETER_SEPARATOR);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            
            int separatorIndex = token.indexOf(PATH_PARAMETER_VALUE_SEPARATOR);
            if (separatorIndex > 0) {
                String parameterName = token.substring(0, separatorIndex);
                String parameterValue = token.substring(separatorIndex + 1);
                pathParameters.put(parameterName, parameterValue);
            }
        }
        
        return pathParameters;
    }

    protected Map<String, String> getRequestParameters() {
        Map<String, String> requestParameters = new HashMap<String, String>();
        
        Map<String, String[]> parameterMap  = request.getParameterMap();
        if (parameterMap.size() > 0) {
            for (String parameterName : parameterMap.keySet()) {
                String[] parameterArray = parameterMap.get(parameterName);
                String parameterValue = "";
                for (int i=0; i < parameterArray.length; i++) {
                    if (i != 0) {
                        parameterValue += PATH_PARAMETER_VALUE_LIST_SEPARATOR;
                    }
                    parameterValue += parameterArray[i];
                }
                requestParameters.put(parameterName, parameterValue);
            }
        }
        
        return requestParameters;
    }

    protected List<String> getParameterList(String parameterValue) {
        List<String> parameterList = new ArrayList<String>();
        
        StringTokenizer tokenizer = new StringTokenizer(parameterValue, PATH_PARAMETER_VALUE_LIST_SEPARATOR);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            parameterList.add(token);
        }
        
        return parameterList;
    }

    protected void displayMap(String title, Map<String, String> map) {        
        if (map.size() > 0) {
            log.info(title);
            for (String parameterName : map.keySet()) {
                String parameterValue = map.get(parameterName);
                log.info("   " + parameterName + "[" + parameterValue + "]");
            }
        }
    }

}