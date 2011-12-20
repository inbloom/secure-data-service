package org.slc.sli.validation;

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
    private final String baseDir = "classpath:avroSchema";
    
    // TODO need to figure out better way to map schema file name to entity type
    private String[] schemaTypes = new String[] { "school", "student", "studentSchoolAssociation" };
    
    @PostConstruct
    public void init() throws IOException {
        for (String schemaType : schemaTypes) {
            registerSchema(schemaType);
        }
    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return mapSchema.get(entity.getType());
    }
    
    private void registerSchema(String schemaType) throws IOException {
        Parser parser = new Schema.Parser();
        URL url = ResourceUtils.getURL(baseDir + "/" + schemaType + "_body.avpr");
        Schema schema = parser.parse(url.openStream());
        mapSchema.put(schemaType, schema);
        
    }
    
}
