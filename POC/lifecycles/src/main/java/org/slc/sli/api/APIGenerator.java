package org.slc.sli.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slc.sli.model.ModelEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API call generator
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 */
public class APIGenerator {
    
    // Logging
    private static Logger log = LoggerFactory.getLogger(APIGenerator.class);
    
    
    // Constants
    public static final String DEFAULT_API_CSV_FILE = "api.txt";
    public static final String API_TOKEN_SEPARATOR = "|\t";
    public static final String DEFAULT_OPERATION = "read";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String DEFAULT_CONTENT_TYPE = CONTENT_TYPE_JSON;
    
    
    // Static Attributes
    private static DatastoreClient datastoreClient = new DatastoreClient();
    
    
    // Entry Point
    public static void main(String[] args) {
        
        log.info("Starting APIGenerator..."); 
        
        read(DEFAULT_API_CSV_FILE);
    }
    
    private static void read(String filePath) {
        
        try {
            
            log.info("Reading API file: " + filePath + "...");
            
            FileReader fileReader = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(fileReader);
            
            String line = reader.readLine();            
            while (line != null) {
                String url = null;
                String operation = DEFAULT_OPERATION;
                String contentType = DEFAULT_CONTENT_TYPE;
                String content = "";

                // UnEscape CSV DoubleQuotes
                Pattern pattern = Pattern.compile("\"\"");   
                Matcher matcher = pattern.matcher(line);  
                line = matcher.replaceAll("\"");   
                matcher.reset();
                
                StringTokenizer tokenizer = new StringTokenizer(line, API_TOKEN_SEPARATOR);
                if (tokenizer.hasMoreTokens()) {
                    url = tokenizer.nextToken().trim();                    
                }
                if (tokenizer.hasMoreTokens()) {
                    operation = tokenizer.nextToken().trim();                    
                }
                if (tokenizer.hasMoreTokens()) {
                    contentType = tokenizer.nextToken().trim();                    
                }
                if (tokenizer.hasMoreTokens()) {
                    content = tokenizer.nextToken().trim(); 
                    
                    // UnEscape CSV Quoted String
                    if (content.startsWith("\"") && content.endsWith("\"")) {
                        content = content.substring(1, content.length() - 1).trim();                    
                    }
                }
                
                invokeAPI(url, operation, contentType, content);
                
                line = reader.readLine();
            }
      
            reader.close();
            
            log.info("Finished API file: " + filePath);
            
        } catch (Exception exception) {
            exception.printStackTrace();
        }        
    }
    
    private static void invokeAPI(String urlPath, String operation, String contentType, String content) {
        
        if (urlPath != null) {
            
            List<Map> responseList = null;
            ModelEntity responseEntity = null;
            String response = null;
            DatastoreOperationType operationType = ModelEntity.getEnumFromString(DatastoreOperationType.class, operation);
            ModelEntity entity = null;
            if ((contentType != null) && ((content != null) && (content.length() > 0))) {
                if (contentType.equals(CONTENT_TYPE_JSON)) {
                    entity = ModelEntity.fromJSON(content);
                 } else {
                     log.error("Content-Type: " + contentType + " not supported yet!");
                 }
            }
            
            // Log Request Details
            String message = operationType.value();
            message += " URL: " + urlPath;
            if (entity != null) {
                message += " CONTENT: " + entity.toString();
            }
            log.info(message);
            
            switch (operationType) {
            
            case CREATE:
                if (entity != null) {
                    response = datastoreClient.create(urlPath, entity);
                }
                break;
            
            case READ:
                if (entity != null) {
                    responseEntity = datastoreClient.read(urlPath, entity);
                } else {
                    responseEntity = datastoreClient.read(urlPath);
                }
                break;
            
            case UPDATE:
                if (entity != null) {
                    response = datastoreClient.update(urlPath, entity);
                }
                break;
            
            case DELETE:
                datastoreClient.delete(urlPath, entity);
                break;
                
            case READ_LIST:
                responseList = datastoreClient.readList(urlPath);
                break;
            
            }
            
            // Log Response Details
            if (response != null) {
                log.info("   Response: " + response);
            }
            if ((responseList != null) && (responseList.size() > 0)) {
               for (Map itemMap : responseList) {
                   log.info("   Response: " + itemMap.toString());
                   log.info("   Lifecycle: " + getLifecycleInfo(itemMap));
                }
            } else if (operationType == DatastoreOperationType.READ_LIST) {
                log.info("   Response: Empty");
            }
            if (responseEntity != null) {
                log.info("   Response: " + responseEntity.toString());
                log.info("   Lifecycle: " + getLifecycleInfo(responseEntity));
            } else if (operationType == DatastoreOperationType.READ) {
                log.info("   Response: Not Found");
            }
            
        }
        
    }
    
    private static String getLifecycleInfo(Map entityMap) {
        String description = "";
        
        Map lifecycleMap = (Map)entityMap.get("lifecycle");
        
        description += lifecycleMap.get("state");
        String currentPart = (String)lifecycleMap.get("currentPart");
        List parts = (List)lifecycleMap.get("parts");
        List dependencies = (List)lifecycleMap.get("dependencies");
        if ((currentPart != null) && (currentPart.length() > 0)) {
            description += " Current Part: " + currentPart;
        }
        if ((parts != null) && (parts.size() > 0)) {
            description += " Parts: " + parts.toString();
        }
        if ((dependencies != null) && (dependencies.size() > 0)) {
            description += " Dependencies: " + dependencies.toString();
        }
        
        return description;
    }
    
    private static String getLifecycleInfo(ModelEntity entity) {
        String description = "";
        
        description += entity.getLifecycle().getState().toString();
        String currentPart = entity.getLifecycle().getCurrentPart();
        String[] parts = entity.getLifecycle().getParts();
        String[] dependencies = entity.getLifecycle().getDependencies();
        if ((currentPart != null) && (currentPart.length() > 0)) {
            description += " Current Part: " + currentPart;
        }
        if ((parts != null) && (parts.length > 0)) {
            description += " Parts: " + parts.toString();
        }
        if ((dependencies != null) && (dependencies.length > 0)) {
            description += " Dependencies: " + dependencies.toString();
        }
        
        return description;
    }
    
}
