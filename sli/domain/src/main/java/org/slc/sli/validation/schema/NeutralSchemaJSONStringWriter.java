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


package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transform a NeutralSchema to JSON.
 * 
 * @author asaarela
 * 
 */
public class NeutralSchemaJSONStringWriter implements NeutralSchemaStringWriter {
    
    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(NeutralSchemaJSONStringWriter.class);
    
    private NeutralSchema schema;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.slc.sli.validation.schema.NeutralSchemaStringWriter#transform(org.slc.sli.validation.
     * schema.NeutralSchema)
     */
    @Override
    public String transform(NeutralSchema schema) {
        this.schema = schema;
        return toJson();
    }
    
    /**
     * @return JSON representation of this SLI schema
     */
    protected String toJson() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(getJsonHeader());
        
        if (!schema.isPrimitive()) {
            buffer.append(getJsonFields("fields", schema.getFields()));
        }
        
        buffer.append(getJsonProperties("properties", schema.getProperties()));
        buffer.append(getJsonFooter());
        
        return buffer.toString();
    }
    
    private String getJsonHeader() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n{\n   \"type\":\"").append(schema.getType()).append("\"");
        buffer.append(  ",\n   \"validatorClass\":\"").append(schema.getValidatorClass()).append("\"");
        buffer.append(  ",\n   \"version\":\"").append(schema.getVersion()).append("\"");
        getAnnotations(schema, buffer);
        
        return buffer.toString();
    }
    
    private void getAnnotations(NeutralSchema schema, StringBuffer buffer) {
        
        StringBuilder annotations = new StringBuilder();
        annotations.append(", \"annotations\": {");
        AppInfo appInfo = schema.getAppInfo();
        if (appInfo != null) {
            annotations.append(appInfo.toString());
            buffer.append(annotations).append("} ");
        }
    }
    
    private String getJsonFields(String label, Map<String, NeutralSchema> fields) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(",\n    ");
        buffer.append("\"").append(label).append("\":{");
        
        if ((schema.getFields() != null) && (schema.getFields().size() > 0)) {
            String separator = "";
            
            for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
                
                String name = entry.getKey();
                NeutralSchema object = entry.getValue();
                
                StringBuilder description = new StringBuilder();
                
                if (object instanceof ListSchema) {
                    List<NeutralSchema> schemaList = ((ListSchema) object).getList();
                    List<String> list = new ArrayList<String>();
                    for (NeutralSchema schema : schemaList) {
                        list.add(schema.getType());
                    }
                    try {
                        description.append(MAPPER.writeValueAsString(list));
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                    
                } else {
                    description.append("\"").append(object.getType()).append("\"");
                }
                
                buffer.append(separator);
                buffer.append("\n    ");
                buffer.append("{\"").append(name).append("\":").append(description);

                getAnnotations(object, buffer);
                
                separator = "},";
            }
        }
        
        buffer.append("}");
        
        return buffer.toString();
    }
    
    private String getJsonProperties(String label, Map<String, Object> properties) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(",\n   \"").append(label).append("\":{");

        if ((properties != null) && (properties.size() > 0)) {
            
            String separator = "";
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                
                String name = entry.getKey();
                Object object = entry.getValue();
                
                String description = "";
                if (object instanceof List) {
                    try {
                        description = MAPPER.writeValueAsString(object);
                    } catch (Exception exception) {
                        LOG.error("Error wring description for " + object, exception);
                    }
                } else {
                    if (object instanceof String) {
                        StringBuilder builder  = new StringBuilder();
                        builder.append("\"").append(object).append("\"");
                        description = builder.toString();
                    }
                }

                buffer.append(separator);
                buffer.append("\n      \"").append(name).append("\":").append(description);

                separator = ",";
            }
        }
        
        buffer.append("}");
        
        return buffer.toString();
    }
    
    private String getJsonFooter() {
        return "\n}\n";
    }
    
}
