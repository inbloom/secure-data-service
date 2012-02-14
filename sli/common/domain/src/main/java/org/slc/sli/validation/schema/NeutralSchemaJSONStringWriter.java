package org.slc.sli.validation.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        StringBuffer buffer = new StringBuffer();
        
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
        
        buffer.append("\n");
        buffer.append("{");
        buffer.append("" + "\n" + "   \"type\":\"" + schema.getType() + "\"");
        buffer.append("," + "\n" + "   \"validatorClass\":\"" + schema.getValidatorClass() + "\"");
        buffer.append("," + "\n" + "   \"version\":\"" + schema.getVersion() + "\"");
        getAnnotations(schema, buffer);
        
        return buffer.toString();
    }
    
    private void getAnnotations(NeutralSchema schema, StringBuffer buffer) {
        
        StringBuffer annotations = new StringBuffer();
        annotations.append(", \"annotations\": {");
        AppInfo appInfo = schema.getAppInfo();
        if (appInfo != null) {
            annotations.append(appInfo.toString());
            buffer.append(annotations + "} ");
        }
    }
    
    private String getJsonFields(String label, Map<String, NeutralSchema> fields) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(", ");
        buffer.append("\n    ");
        
        buffer.append("\"" + label + "\":{");
        
        if ((schema.getFields() != null) && (schema.getFields().size() > 0)) {
            String separator = "";
            
            for (Map.Entry<String, NeutralSchema> entry : fields.entrySet()) {
                
                String name = entry.getKey();
                NeutralSchema object = entry.getValue();
                
                StringBuffer description = new StringBuffer();
                
                if (object instanceof ListSchema) {
                    List<NeutralSchema> schemaList = ((ListSchema) object).getList();
                    List<String> list = new ArrayList<String>();
                    for (NeutralSchema schema : schemaList) {
                        list.add(schema.getType());
                    }
                    try {
                        description.append(NeutralSchema.MAPPER.writeValueAsString(list));
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                    
                } else {
                    description.append("\"" + object.getType() + "\"");
                }
                
                buffer.append(separator);
                
                buffer.append("\n");
                buffer.append("    ");
                
                buffer.append("{\"" + name + "\":" + description);
                
                getAnnotations(object, buffer);
                
                separator = "},";
            }
        }
        
        buffer.append("}");
        
        return buffer.toString();
    }
    
    private String getJsonProperties(String label, Map<String, Object> properties) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("," + "\n" + "   \"" + label + "\":{");
        
        if ((properties != null) && (properties.size() > 0)) {
            
            String separator = "";
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                
                String name = entry.getKey();
                Object object = entry.getValue();
                
                String description = "";
                if (object instanceof List) {
                    try {
                        description = NeutralSchema.MAPPER.writeValueAsString(object);
                    } catch (Exception exception) {
                        LOG.error("Error wring description for " + object, exception);
                    }
                } else if (object instanceof String) {
                    description = "\"" + (String) object + "\"";
                }
                
                buffer.append(separator + "\n" + "      \"" + name + "\":" + description);
                
                separator = ",";
            }
        }
        
        buffer.append("}");
        
        return buffer.toString();
    }
    
    private String getJsonFooter() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n");
        buffer.append("}" + "\n");
        
        return buffer.toString();
    }
    
}
