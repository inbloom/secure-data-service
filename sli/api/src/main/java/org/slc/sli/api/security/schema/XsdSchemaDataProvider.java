/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security.schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Extracts required data from the XsdSchema
 *
 * @author dkornishev
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
                    public Set<Right> getReadAuthorities() {
                        return new HashSet<Right>(Arrays.asList(Right.READ_GENERAL));
                    }

                    @Override
                    public Set<Right> getWriteAuthorities() {
                        return new HashSet<Right>(Arrays.asList(Right.WRITE_GENERAL));
                    }

                };
            }

            @Override
            public NeutralSchemaType getSchemaType() {
                throw new UnsupportedOperationException("This instance is for accessing security rights only");
            }

            @Override
            protected boolean validate(String fieldName, Object entity, List<ValidationError> errors, Repository<Entity> repo) {
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
    public Set<Right> getRequiredReadLevels(String entityType, String fieldPath) {
        Right auth = Right.READ_GENERAL;

        NeutralSchema schema = traverse(entityType, fieldPath);
        if (schema != null) {
            AppInfo info = schema.getAppInfo();
            if (info != null) {
                return info.getReadAuthorities();
            }
        }
        return new HashSet<Right>(Arrays.asList(auth));
    }

    @Override
    public Set<Right> getRequiredWriteLevels(String entityType, String fieldPath) {
        Right auth = Right.WRITE_GENERAL;
        NeutralSchema schema = traverse(entityType, fieldPath);
        if (schema != null) {
            AppInfo info = schema.getAppInfo();
            if (info != null) {
                return info.getWriteAuthorities();
            }
        }
        return new HashSet<Right>(Arrays.asList(auth));
    }

    @Override
    public String getReferencingEntity(String entityType, String fieldPath) {
        NeutralSchema schema = traverse(entityType, fieldPath);
        if (schema != null) {
            AppInfo info = null;
            if (schema instanceof ListSchema) {
                List<NeutralSchema> schemaList = ((ListSchema) schema).getList();
                if (!schemaList.isEmpty()) {
                    info = schemaList.get(0).getAppInfo();
                }
            } else {
                info = schema.getAppInfo();
            }

            if (info != null) {
                return info.getReferenceType();
            }
        }
        return null;
    }
    
    public Set<Right> getAllFieldRights(String entityType, boolean getReadRights) {
        Set<Right> neededRights = new HashSet<Right>();
        NeutralSchema schema = repo.getSchema(entityType);
        if (schema != null) {
            Map<String, NeutralSchema> fields = schema.getFields();
            for (String field : fields.keySet()) {
                NeutralSchema fieldSchema = fields.get(field);
                if (fieldSchema != null && fieldSchema.getAppInfo() != null) {
                    AppInfo info = fieldSchema.getAppInfo();
                    if (getReadRights) {
                        neededRights.addAll(info.getReadAuthorities());
                    } else {
                        neededRights.addAll(info.getWriteAuthorities());
                    }
                }
            }
        }
        
        return neededRights;
    }

    private NeutralSchema traverse(String entityType, String fieldPath) {
        NeutralSchema schema = repo.getSchema(entityType);

        if (schema != null) {
            String[] chunks = fieldPath.split("\\.");

            for (String chunk : chunks) {
                schema = schema.getFields().get(chunk);

                if (schema == null) {
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
