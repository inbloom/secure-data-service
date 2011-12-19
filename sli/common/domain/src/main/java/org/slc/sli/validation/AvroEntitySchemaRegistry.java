package org.slc.sli.validation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.slc.sli.domain.Entity;
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
    
    private Map<String, Schema> mapSchema = new HashMap<String, Schema>();
    
    @PostConstruct
    public void init() throws IOException {
        
        File root = ResourceUtils.getFile("classpath:avroSchema");
        File[] schemaFiles = root.listFiles();
        
        for (File file : schemaFiles) {
            URL url = ResourceUtils.getURL("classpath:avroSchema/" + file.getName());
        Parser parser = new Schema.Parser();
        Schema schema = parser.parse(url.openStream());
            mapSchema.put(file.getName().split("_")[0], schema);
        }

    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return mapSchema.get(entity.getType());
    }
    
}
