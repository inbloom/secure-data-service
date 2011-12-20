package org.slc.sli.validation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.commons.io.FileUtils;
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
    
    @PostConstruct
    public void init() {
        try {
        File schemaDir = ResourceUtils.getFile(baseDir);
        
        Iterator<File> it = FileUtils.iterateFiles(schemaDir, new String[] { "avpr" }, false);
        while (it.hasNext()) {
            registerSchema(it.next());
        }
        } catch (IOException e) {
        }
    }
    
    @Override
    public Schema findSchemaForType(Entity entity) {
        return mapSchema.get(entity.getType());
    }
    
    private void registerSchema(File file) throws IOException {
        Parser parser = new Schema.Parser();
        Schema schema = parser.parse(FileUtils.openInputStream(file));
        mapSchema.put(file.getName().split("_")[0], schema);
        
    }
    
}
