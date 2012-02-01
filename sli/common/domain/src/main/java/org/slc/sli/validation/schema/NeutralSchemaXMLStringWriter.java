package org.slc.sli.validation.schema;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Transform a NeutralSchema into XML.
 * 
 * @author asaarela
 * 
 */
public class NeutralSchemaXMLStringWriter implements NeutralSchemaStringWriter {
    
    
    private NeutralSchema schema;
    private boolean enableHiearchy;
    
    public NeutralSchemaXMLStringWriter(boolean enableHiearchy) {
        this.enableHiearchy = enableHiearchy;
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
        return toXml(enableHiearchy);
    }
    
    /**
     * @return XML representation of this SLI schema
     */
    public String toXml(boolean enableHierarchy) {
        StringBuffer buffer = new StringBuffer();
        
        if (schema.isPrimitive()) {
            buffer.append("<primitive>" + escape(schema.getType()) + "</primitive>" + "\n");
        } else {
            buffer.append(getXmlHeader());
            
            if (!schema.isPrimitive()) {
                buffer.append(getXmlFields("fields", schema.getFields(), enableHierarchy));
                buffer.append(getXmlProperties("properties", schema.getProperties()));
            }
            
            buffer.append(getXmlFooter());
        }
        
        return buffer.toString();
    }
    
    private String getXmlHeader() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n");
        buffer.append("<" + escape(schema.getType()) + " version=\"" + escape(schema.getVersion())
                + "\" validatorClass=\"" + escape(schema.getValidatorClass()) + "\">" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlFields(String label, Map<String, Object> fields, boolean enableHierarchy) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<fields>" + "\n");
        
        for (String name : schema.getFields().keySet()) {
            Object object = schema.getFields().get(name);
            String fieldName = name;
            if (fieldName.startsWith("*")) {
                fieldName = fieldName.substring(1);
            }
            if (object instanceof NeutralSchema) {
                buffer.append("<" + escape(fieldName) + ">" + "\n");
                if (object instanceof ListSchema) {
                    
                    // Generate readable List element name
                    String listElementName = ((ListSchema) object).getType();
                    
                    List<NeutralSchema> schemaList = ((ListSchema) object).getList();
                    buffer.append("<" + escape(listElementName) + ">" + "\n");
                    for (NeutralSchema listItemSchema : schemaList) {
                        if (enableHierarchy) {
                            buffer.append(listItemSchema.toXml(enableHierarchy));
                        } else {
                            buffer.append("<" + escape(listItemSchema.getType()) + "/>" + "\n");
                        }
                    }
                    buffer.append("</" + escape(listElementName) + ">" + "\n");
                } else {
                    NeutralSchema schema = (NeutralSchema) object;
                    if (enableHierarchy) {
                        buffer.append(schema.toXml(enableHierarchy));
                    } else {
                        buffer.append("<" + escape(schema.getType()) + "/>" + "\n");
                    }
                }
                buffer.append("</" + escape(fieldName) + ">" + "\n");
            }
        }
        
        buffer.append("</fields>" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlProperties(String label, Map<String, Object> properties) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<properties>" + "\n");
        
        for (String name : schema.getProperties().keySet()) {
            Object object = schema.getProperties().get(name);
            
            String description = "";
            if (object instanceof List) {
                List<?> list = (List<?>) object;
                description = "[";
                String separator = "";
                for (Object listItem : list) {
                    description += separator + " '" + listItem.toString() + "'";
                    separator = ",";
                }
                description += "]";
            } else if (object instanceof String) {
                description = object.toString();
            }
            
            buffer.append("<property name=\"" + escape(name) + "\" value=\"" + escape(description) + "\"/>" + "\n");
        }
        
        buffer.append("</properties>" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlFooter() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n");
        buffer.append("</" + escape(schema.getType()) + ">" + "\n");
        
        return buffer.toString();
    }
    
    private String escape(String input) {
        return StringEscapeUtils.escapeXml(input);
    }
    
}
