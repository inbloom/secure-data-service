package org.slc.sli.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import org.slc.sli.domain.Entity;

/**
 * 
 * SLI Schema Registry which parses and builds SLI Schema object graph for use by the SLI Validation
 * Framework.
 * The SLI Validation Framework will subsequently invoke findSchemaForType() to obtain SLI schema
 * objects
 * used for validating associated SLI entities.
 * 
 * @author Dong Liu <dliu@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Component
public class NeutralSchemaRegistry {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(NeutralSchemaRegistry.class);
    
    // Constants
    public static final String SCHEMA_PATH = "classpath:neutral-schemas";
    public static final String JSON = "json";
    public static final String XML = "xml";
    static final int BUFFER_SIZE = 2048;
    
    // Attributes
    
    @Autowired
    NeutralSchemaFactory schemaFactory;
    
    private String schemaPath = SCHEMA_PATH;
    private String schemaRepresentation = JSON;
    private Map<String, NeutralSchema> schemaMap = new HashMap<String, NeutralSchema>();
    
    // Methods
    
    @PostConstruct
    public void init() {
        initialize(SCHEMA_PATH, JSON);
    }
    
    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }
    
    public String getSchemaPath() {
        return this.schemaPath;
    }
    
    public void setSchemaRepresentation(String schemaRepresentation) {
        this.schemaRepresentation = schemaRepresentation;
    }
    
    public String getSchemaRepresentation() {
        return this.schemaRepresentation;
    }
    
    public void setSchemaFactory(NeutralSchemaFactory schemaFactory) {
        this.schemaFactory = schemaFactory;
    }
    
    public NeutralSchemaFactory getSchemaFactory() {
        return this.schemaFactory;
    }
    
    public NeutralSchema findSchemaForType(Entity entity) {
        return schemaMap.get(entity.getType());
    }
    
    public long size() {
        return this.schemaMap.size();
    }
    
    protected Map<String, NeutralSchema> getSchemaMap() {
        return this.schemaMap;
    }
    
    protected void initialize(String schemaResourcesPath, String schemaRepresentation) {
        
        // Set Schema Resource Path and Representation
        this.setSchemaPath(schemaResourcesPath);
        this.setSchemaRepresentation(schemaRepresentation);
        
        try {
            URL schemaResourcesUrl = ResourceUtils.getURL(schemaResourcesPath);
            String protocol = schemaResourcesUrl.getProtocol();
            
            // Process schema files which are either archived in a jar or placed directly on the
            // file system
            if (schemaResourcesUrl.toString().endsWith("jar")) {
                String jarPath = schemaResourcesUrl.getPath().substring(5, schemaResourcesUrl.getPath().indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                this.registerSchemaEntries(jar, schemaRepresentation);
            } else if (protocol.equals("file")) {
                File schemaResourcesDir = FileUtils.toFile(schemaResourcesUrl);
                List<File> schemaFiles = new ArrayList<File>(FileUtils.listFiles(schemaResourcesDir,
                        new String[] { schemaRepresentation }, true));
                this.registerSchemaFiles(schemaFiles);
            } else {
                LOG.error("Unsupported schema registry protocol: " + protocol);
            }
            
            // Resolve Schema Dependencies
            this.resolveSchemaDependencies();
            
            LOG.info("Total Registered Schemas: " + this.size());
            
        } catch (IOException ioException) {
            LOG.error("Unable to parse schema resources: " + schemaResourcesPath + ": " + ioException.getMessage(), ioException);
        }
    }
    
    protected void registerSchemaFiles(List<File> schemaFiles) {
        
        for (File schemaFile : schemaFiles) {
            
            // Create Neutral Schema from JSON File
            NeutralSchema schema = schemaFactory.fromFile(schemaFile, NeutralSchema.class);
            NeutralSchema specificSchema = null;
            if (schema != null) {
                specificSchema = schemaFactory.createSpecificSchema(schema);
            }
            
            // Update Schema Registry Map
            if (specificSchema != null) {
                
                LOG.info("Registering Schema File: " + schemaFile.getName());
                
                schemaMap.put(specificSchema.getType(), specificSchema);
            }
        }
    }
    
    protected void registerSchemaEntries(JarFile jar, String schemaRepresentation) {
        
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                StringBuilder schemaEntry = new StringBuilder();
                if (entry.getName().startsWith(SCHEMA_PATH.split(":")[1])
                        && entry.getName().endsWith(schemaRepresentation)) {
                    String schemaName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    byte[] data = new byte[BUFFER_SIZE];
                    InputStream entryInputStream = ResourceUtils.getURL(schemaName).openStream();
                    while (entryInputStream.read(data, 0, BUFFER_SIZE) != -1) {
                        schemaEntry.append(data);
                    }
                    
                    // Create Neutral Schema from JSON String
                    NeutralSchema schema = schemaFactory.fromStringBuffer(schemaEntry, NeutralSchema.class);
                    NeutralSchema specificSchema = null;
                    if (schema != null) {
                        specificSchema = schemaFactory.createSpecificSchema(schema);
                    }
                    
                    // Update Schema Registry Map
                    if (specificSchema != null) {
                        
                        LOG.info("Registering Schema Entry: " + specificSchema.getType());
                        
                        schemaMap.put(specificSchema.getType(), specificSchema);
                    }
                }
            }
        } catch (IOException ioException) {
            LOG.error("Unable to parse schema resources archive: " + jar.getName() + ": " + ioException.getMessage(), ioException);
        }
    }
    
    protected void resolveSchemaDependencies() {
        
        // Populated each Schema's references/dependencies
        Map<String, NeutralSchema> pendingMap = new HashMap<String, NeutralSchema>();
        for (NeutralSchema schema : schemaMap.values()) {
            pendingMap.clear();
            for (String schemaFieldName : schema.getFields().keySet()) {
                Object schemaFieldObject = schema.getFields().get(schemaFieldName);
                NeutralSchema fieldSchema = null;
                if (schemaFieldObject instanceof String) {
                    String schemaFieldType = (String) schemaFieldObject;
                    
                    // Schema references
                    fieldSchema = schemaMap.get(schemaFieldType);
                    
                } else if (schemaFieldObject instanceof List) {
                    List<?> list = (List<?>) schemaFieldObject;
                    
                    // List references
                    ListSchema listSchema = (ListSchema) schemaFactory.createSchema("list");
                    
                    for (Object listItem : list) {
                        String listItemSchemaType = listItem.toString();
                        NeutralSchema listItemSchema = schemaMap.get(listItemSchemaType);
                        listSchema.getList().add(listItemSchema);
                    }
                    
                    fieldSchema = listSchema;
                    
                } else {
                    LOG.error("Detected invalid object type during schema parsing: "
                            + schemaFieldObject.getClass().getName());
                }
                
                // Save Schema instances for later
                if (fieldSchema != null) {
                    pendingMap.put(schemaFieldName, fieldSchema);
                }
            }
            
            // Update Schema fields with referenced Schema instances
            for (String schemaFieldName : pendingMap.keySet()) {
                NeutralSchema fieldSchema = pendingMap.get(schemaFieldName);
                schema.getFields().put(schemaFieldName, fieldSchema);
                
            }
        }
    }
    
    protected void toFile(String directory, String fileName, String representation) {
        List<NeutralSchema> list = new ArrayList<NeutralSchema>(this.getSchemaMap().values());
        schemaFactory.toFile(directory, fileName + "." + representation, list, representation, true);
    }
    
    public static void main(String[] args) {
        
        LOG.info("Starting Schema Registration...");
        
        NeutralSchemaRegistry registry = new NeutralSchemaRegistry();
        
        registry.setSchemaFactory(new NeutralSchemaFactory());
        
        registry.init();
        
        registry.toFile("neutral-schemas/", "registry", XML);
        
        LOG.info("Finished.");
    }
    
}
