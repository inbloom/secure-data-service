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
        
     // TODO
        // By convention, load all <entity>_body schema files.
        
        Parser parser = new Schema.Parser();
        
        // FilenameUtils.
        URL url = ResourceUtils.getURL("classpath:avroSchema/school_body.avpr");
        Schema schema = parser.parse(url.openStream());
        mapSchema.put("school", schema);
    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return mapSchema.get(entity.getType());
    }
    
}
