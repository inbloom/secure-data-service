package org.slc.sli.validation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
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
    
    private Map<String, Schema> mapSchema = new HashMap<String, Schema>();
    private final String baseDir = "classpath:avroSchema";
    
    @PostConstruct
    public void init() {
        try {
            URL baseURL = ResourceUtils.getURL(baseDir);
            LOG.debug("base schema url is {}", baseURL.toString());
            String protocol = baseURL.getProtocol();
            LOG.debug("base schema url protocol is {}", protocol);

            // check if the schema files are archived in jar
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
                        registerSchema(schemaName);
                    }
                }
            }
            // check if the schema files are in file system
            else if (protocol.equals("file")) {
                
                File schemaDir = FileUtils.toFile(baseURL);
                LOG.debug("base schema directory is {}", schemaDir);
                Iterator<File> it = FileUtils.iterateFiles(schemaDir, new String[] { "avpr" }, false);
                while (it.hasNext()) {
                    registerSchema(it.next().getName());
                }
            } else
                LOG.debug("can not load the avro schema files at {}", baseDir);
        } catch (IOException e) {
            LOG.debug("can not load the avro schema files at {}", baseDir);
        }
    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return mapSchema.get(entity.getType());
    }
    
    private void registerSchema(String schemaFileName) throws IOException {
        Parser parser = new Schema.Parser();
        URL fileURL = ResourceUtils.getURL(baseDir + "/" + schemaFileName);
        Schema schema = parser.parse(fileURL.openStream());
        mapSchema.put(schemaFileName.split("_")[0], schema);
        LOG.debug("added the avro schema file {} into registry", schemaFileName);
    }
}
