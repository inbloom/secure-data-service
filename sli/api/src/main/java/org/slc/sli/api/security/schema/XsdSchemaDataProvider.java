package org.slc.sli.api.security.schema;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Extracts required data from the XsdSchema
 * 
 * @author dkornishev
 * 
 */
@Component
public class XsdSchemaDataProvider implements SchemaDataProvider {
    
    @Autowired
    private SchemaRepository repo;
    
    /**
     * Bogus Schema used as fallback in case field path search turns up nothing
     */
    private NeutralSchema defaultSchema;
    
    @PostConstruct
    public void init() {
        defaultSchema = new NeutralSchema("") {
            
            @Override
            public AppInfo getAppInfo() {
                return new AppInfo(null) {
                    
                    @Override
                    public Right getReadAuthority() {
                        return Right.READ_GENERAL;
                    }
                    
                    @Override
                    public Right getWriteAuthority() {
                        return Right.WRITE_GENERAL;
                    }
                    
                };
            }
            
            @Override
            public NeutralSchemaType getSchemaType() {
                throw new UnsupportedOperationException("This instance is for accessing security rights only");
            }
            
            @Override
            protected boolean validate(String fieldName, Object entity, List<ValidationError> errors,
                    Repository<Entity> repo) {
                throw new UnsupportedOperationException("This instance is for accessing security rights only");
            }
        };
    }
    
    @Override
    public String getDataSphere(String entityType) {
        String sphere = "CEM";
        if (repo.getSchema(entityType) != null && repo.getSchema(entityType).getAppInfo() != null) {
            sphere = repo.getSchema(entityType).getAppInfo().getSecuritySphere();
        }
        
        return sphere;
    }
    
    @Override
    public Right getRequiredReadLevel(String entityType, String fieldPath) {
        Right auth = Right.READ_GENERAL;
        
        NeutralSchema schema = traverse(entityType, fieldPath);
        if (schema != null) {
            AppInfo info = schema.getAppInfo();
            if (info != null) {
                auth = info.getReadAuthority();
            }
        }
        return auth;
    }
    
    @Override
    public Right getRequiredWriteLevel(String entityType, String fieldPath) {
        Right auth = Right.WRITE_GENERAL;
        NeutralSchema schema = traverse(entityType, fieldPath);
        if (schema != null) {
            AppInfo info = schema.getAppInfo();
            if (info != null) {
                auth = info.getWriteAuthority();
            }
        }
        return auth;
    }
    
    private NeutralSchema traverse(String entityType, String fieldPath) {
        NeutralSchema schema = repo.getSchema(entityType);
        
        if (schema != null) {
            String[] chunks = fieldPath.split("\\.");
            
            for (String chunk : chunks) {
                schema = schema.getFields().get(chunk);
                
                if (schema == null) {
                    warn("Reached end of the line for type {} and path {}", entityType, fieldPath);
                    schema = defaultSchema;
                    break;
                }
            }
        } else {
            schema = defaultSchema;
        }
        
        return schema;
    }
}
