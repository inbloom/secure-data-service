package org.slc.sli.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.SchemaParseException;
import org.apache.commons.io.FileUtils;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * Provides a registry for retrieving Avro schema
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Component
public class AvroEntitySchemaRegistry implements EntitySchemaRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(AvroEntitySchemaRegistry.class);
    
    private Map<String, Schema> entityTypeToSchemaMap = new HashMap<String, Schema>();
    private final String baseDir = "classpath:avroSchema";
    private final String enumBaseDir = baseDir + "/enum";
    
    @PostConstruct
    public void init() {
        List<String> enumTypes = getResourceNames(enumBaseDir);
        List<String> recordTypes = getResourceNames(baseDir);
        loadSchemas(enumTypes, recordTypes);
    }
    
    private static List<String> getResourceNames(String baseDir) {
        List<String> list = new LinkedList<String>();
        try {
            URL baseURL = ResourceUtils.getURL(baseDir);
            LOG.debug("base schema url is {}", baseURL.toString());
            String protocol = baseURL.getProtocol();
            LOG.debug("base schema url protocol is {}", protocol);
            
            // check if the schema files are archived in jar or in file system
            if (protocol.equals("jar")) {
                String jarPath = baseURL.getPath().substring(5, baseURL.getPath().indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
                LOG.debug("base schema jar is {}", jar.getName());
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(baseDir.split(":")[1]) && name.endsWith("avpr")) {
                        String schemaName = name.substring(name.lastIndexOf("/") + 1);
                        LOG.debug("schema file name is {}", schemaName);
                        list.add(schemaName);
                    }
                }
            } else if (protocol.equals("file")) {
                File schemaDir = FileUtils.toFile(baseURL);
                LOG.debug("base schema directory is {}", schemaDir);
                Iterator<File> it = FileUtils.iterateFiles(schemaDir, new String[] { "avpr" }, false);
                while (it.hasNext()) {
                    list.add(it.next().getName());
                }
            } else {
                throw new RuntimeException("Unable to load Avro Schema file.  Unhandled protocol: " + baseURL);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load Avro Schemas from: " + baseDir, e);
        }
        return list;
    }
    
    private void loadSchemas(List<String> enumTypes, List<String> recordTypes) {
        try {
            // Parser remembers types as they are loaded.
            Parser mainParser = new Schema.Parser();
            mainParser.setValidate(true);
            
            for (String enumType : enumTypes) {
                String file = this.enumBaseDir + "/" + enumType;
                InputStream openStream = null;
                try {
                    openStream = ResourceUtils.getURL(file).openStream();
                    mainParser.parse(openStream);
                } finally {
                    if (openStream != null) {
                        openStream.close();
                    }
                }
            }
            
            /*
             * The following code does try-fail style dependency resolution.
             * For each entity, parse entity:
             * --- If any exception, add it back into the load queue to try again later.
             * 
             * In theory this could blow up horribly, but on all sorted base Ed-Fi entities it only
             * takes 3 iterations.
             */
            Collections.sort(recordTypes);
            Deque<String> loadQue = new ArrayDeque<String>(recordTypes);
            
            int lastQueSize = -1;
            while (loadQue.size() > 0) {
                if (lastQueSize == loadQue.size()) {
                    throw new RuntimeException(
                            "Schema loader making no progress.  Perhaps due to missing or circular dependencies.");
                }
                lastQueSize = loadQue.size();
                Queue<String> currentQue = new ArrayDeque<String>(loadQue);
                loadQue = new ArrayDeque<String>();
                for (String schemaName : currentQue) {
                    String file = this.baseDir + "/" + schemaName;
                    InputStream openStream = null;
                    try {
                        boolean success = false;
                        openStream = ResourceUtils.getURL(file).openStream();
                        
                        try {
                            Schema.Parser tmp = new Schema.Parser();
                            tmp.addTypes(mainParser.getTypes());
                            tmp.parse(openStream);
                            success = true;
                        } catch (SchemaParseException e) {
                            if (e.getMessage().startsWith("Can't redefine:")) {
                                // TODO: REMOVE ME WHEN NO MORE OLD-STYLE SELF CONTAINED SCHEMAS
                                // EXIST
                                // THIS ONLY WORKS WITH THE CURRENT SCHEMAS (AS OF 12/23/2011). ANY
                                // CHANGES MAY BREAK.
                                openStream.close();
                                openStream = ResourceUtils.getURL(file).openStream();
                                Schema.Parser tmp = new Schema.Parser();
                                Schema schema = tmp.parse(openStream);
                                this.entityTypeToSchemaMap.put(schemaName.split("_")[0], schema);
                                success = false; // skip parsing with the main parser
                                // END TODO
                            } else {
                                loadQue.add(file);
                            }
                        }
                        if (success) {
                            openStream.close();
                            openStream = ResourceUtils.getURL(file).openStream();
                            Schema schema = mainParser.parse(openStream);
                            this.entityTypeToSchemaMap.put(schemaName.split("_")[0], schema);
                            LOG.debug("added the avro schema file {} into registry", schemaName);
                        }
                        
                    } finally {
                        if (openStream != null) {
                            openStream.close();
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return entityTypeToSchemaMap.get(entity.getType());
    }
}
