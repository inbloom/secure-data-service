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

import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Transform a NeutralSchema into XML.
 *
 * @author asaarela
 *
 */
public class NeutralSchemaXMLStringWriter implements NeutralSchemaStringWriter {
    
    
    private NeutralSchema schema;
    private boolean enableHierarchy;
    
    public NeutralSchemaXMLStringWriter(boolean enableHierarchy) {
        this.enableHierarchy = enableHierarchy;
    }
    
    
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
        return toXml();
    }
    
    /**
     * @return XML representation of this SLI schema
     */
    public String toXml() {
        StringBuilder buffer = new StringBuilder();
        
        if (schema.isPrimitive()) {
            buffer.append("<primitive>").append(escape(schema.getType())).append("</primitive>").append("\n");
        } else {
            buffer.append(getXmlHeader());
            
            if (!schema.isPrimitive()) {
                buffer.append(getXmlFields());
                buffer.append(getXmlProperties());
            }
            
            buffer.append(getXmlFooter());
        }
        
        return buffer.toString();
    }
    
    private String getXmlHeader() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n<").append(escape(schema.getType())).append(" version=\"")
                .append(escape(schema.getVersion())).append("\" validatorClass=\"")
                .append(escape(schema.getValidatorClass())).append("\">").append("\n");
        
        return buffer.toString();
    }
    
    private String getXmlFields() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<fields>" + "\n");
        
        for (String name : schema.getFields().keySet()) {
            Object object = schema.getFields().get(name);
            String fieldName = name;
            if (fieldName.startsWith("*")) {
                fieldName = fieldName.substring(1);
            }
            if (object instanceof NeutralSchema) {
                buffer.append("<").append(escape(fieldName)).append(">").append("\n");
                if (object instanceof ListSchema) {
                    
                    // Generate readable List element name
                    String listElementName = ((ListSchema) object).getType();
                    
                    List<NeutralSchema> schemaList = ((ListSchema) object).getList();
                    buffer.append("<").append(escape(listElementName)).append(">").append("\n");
                    for (NeutralSchema listItemSchema : schemaList) {
                        if (enableHierarchy) {
                            buffer.append(transform(listItemSchema));
                        } else {
                            buffer.append("<").append(escape(listItemSchema.getType())).append("/>").append("\n");
                        }
                    }
                    buffer.append("</").append(escape(listElementName)).append(">").append("\n");
                } else {
                    NeutralSchema schema = (NeutralSchema) object;
                    if (enableHierarchy) {
                        buffer.append(transform(schema));
                    } else {
                        buffer.append("<").append(escape(schema.getType())).append("/>").append("\n");
                    }
                }
                buffer.append("</").append(escape(fieldName)).append(">").append("\n");
            }
        }
        
        buffer.append("</fields>" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlProperties() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("<properties>" + "\n");
        
        for (String name : schema.getProperties().keySet()) {
            Object object = schema.getProperties().get(name);

            StringBuilder description = new StringBuilder();
            if (object instanceof List) {
                List<?> list = (List<?>) object;
                description.append("[");
                String separator = "";
                for (Object listItem : list) {
                    description.append(separator).append(" '").append(listItem.toString()).append("'");
                    separator = ",";
                }
                description.append("]");
            } else if (object instanceof String) {
                description.append(object.toString());
            }
            
            buffer.append("<property name=\"").append(escape(name)).append("\" value=\"")
                    .append(escape(description.toString())).append("\"/>").append("\n");
        }
        
        buffer.append("</properties>" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlFooter() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n</").append(escape(schema.getType())).append(">").append("\n");
        return buffer.toString();
    }
    
    private String escape(String input) {
        return StringEscapeUtils.escapeXml(input);
    }
    
}
