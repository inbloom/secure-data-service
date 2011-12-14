package org.slc.sli.validation;

import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * Validates entity bodies using an Avro schema.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Component
public class AvroEntityValidator implements EntityValidator {
    
    @Autowired
    private EntitySchemaRegistry entitySchemaRegistry;
    
    /**
     * Validates the given entity using its Avro schema.
     */
    public boolean validate(Entity entity) throws EntityValidationException {
        
        Schema schema = entitySchemaRegistry.findSchemaForType(entity);
        if (schema == null) {
            throw new EntityValidationException(EntityValidationException.NO_ASSOCIATED_SCHEMA,
                    "No schema associated for type: " + entity.getType());
        }
        
        Map<String, Object> body = entity.getBody();
        for (Field field : schema.getFields()) {
            
            Schema fieldSchema = field.schema();
            String name = field.name();
            Object val = body.get(name);
            
            switch (fieldSchema.getType()) {
            
            case UNION: {
                handleUnion(fieldSchema, name, val);
                break;
            }
            
            case ARRAY: {
                handleArray(fieldSchema, name, val);
                break;
            }
            
            case STRING:
            case BYTES:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
            
            {
                handlePrimitive(fieldSchema, name, val);
                break;
            }
            }
            
        }
        return true;
        
    }
    
    private void handleArray(Schema fieldSchema, String name, Object val) {
        
        // TODO
    }
    
    private void handleUnion(Schema fieldSchema, String name, Object val) {
        List<Schema> types = fieldSchema.getTypes();
        
        // A union with "null" means non-required
        boolean bFoundType = false;
        
        for (Schema type : types) {
            
            if (type.getType().equals(Type.NULL)) {
                
                if (val == null) {
                    bFoundType = true;
                    break;
                }
            } else {
                
                bFoundType = handlePrimitive(type, name, val);
                
            }
            
        }
        
        if (!bFoundType) {
            throw new RuntimeException("Invalid type for field: " + name);
        }
        System.out.println(fieldSchema);
    }
    
    private boolean handlePrimitive(Schema type, String name, Object val) {
        
        Class<?> valType = val.getClass();
        
        switch (type.getType()) {
        case STRING:
            return valType.equals(String.class);
            
        case BYTES:
            return valType.equals(Byte.class);
            
        case INT:
            return valType.equals(Integer.class);
            
        case LONG:
            return valType.equals(Long.class);
            
        case FLOAT:
            return valType.equals(Float.class);
        case DOUBLE:
            return valType.equals(Double.class);
        case BOOLEAN:
            return valType.equals(Boolean.class);
            
        }
        return false;
        
    }
    
    public void setSchemaRegistry(EntitySchemaRegistry schemaRegistry) {
        this.entitySchemaRegistry = schemaRegistry;
        
    }
    
}
