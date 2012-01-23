package org.slc.sli.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Schema base class which provides common attributes/behavior for all SLI schema classes
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Scope("prototype")
@Component
public class NeutralSchema implements Serializable {
    
    // Logging
    static final Log LOG = LogFactory.getLog(NeutralSchema.class);
    
    // Constants
    public static final String JSON = "json";
    public static final String XML = "xml";
    
    // Jackson Mapper
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // Attributes
    private String type = "";
    private NeutralSchemaType schemaType;
    private String version = "1.0";
    private Map<String, Object> properties = null;
    private Map<String, Object> fields = null;
    
    // Future Attributes
    private Properties docProperties = new Properties();
    private Properties customProperties = new Properties();
    private Map<String, Object> customFields = null;
    private String readConverter = null;
    private String writeConverter = null;
    
    // Constructors
    public NeutralSchema() {
    }
    
    public NeutralSchema(String type) {
        this.type = type;
    }
    
    // Methods
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    @JsonIgnore
    public void setSchemaType(NeutralSchemaType schemaType) {
        this.schemaType = schemaType;
    }
    
    @JsonIgnore
    public NeutralSchemaType getSchemaType() {
        return schemaType;
    }
    
    @JsonIgnore
    public String getValidatorClass() {
        return this.getClass().getName();
    }
    
    @JsonIgnore
    public boolean isPrimitive() {
        return true;
    }
    
    @JsonIgnore
    public boolean isSimple() {
        return (!(this instanceof ComplexSchema || this instanceof ListSchema));
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<String, Object>();
        }
        return properties;
    }
    
    public Map<String, Object> getFields() {
        if (fields == null) {
            fields = new LinkedHashMap<String, Object>();
        }
        return fields;
    }
    
    // Future Methods
    @JsonIgnore
    public void setDocProperties(Properties properties) {
        this.docProperties = properties;
    }
    
    @JsonIgnore
    public Properties getDocProperties() {
        return docProperties;
    }
    
    @JsonIgnore
    public void setCustomProperties(Properties properties) {
        this.customProperties = properties;
    }
    
    @JsonIgnore
    public Properties getCustomProperties() {
        return customProperties;
    }
    
    @JsonIgnore
    public Map<String, Object> getCustomFields() {
        if (customFields == null) {
            customFields = new LinkedHashMap<String, Object>();
        }
        return customFields;
    }
    
    @JsonIgnore
    public void setReadConverter(String readConverter) {
        this.readConverter = readConverter;
    }
    
    @JsonIgnore
    public String getReadConverter() {
        return readConverter;
    }
    
    @JsonIgnore
    public void setWriteConverter(String writeConverter) {
        this.writeConverter = writeConverter;
    }
    
    @JsonIgnore
    public String getWriteConverter() {
        return writeConverter;
    }
    
    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     * 
     * @param entity
     *            being validated using this SLI Schema
     * @return true if valid
     */
    public boolean validate(Object entity) throws EntityValidationException {
        List<ValidationError> errors = new LinkedList<ValidationError>();
        boolean isValid = this.validate("", entity, errors);
        return (isValid && (errors.size() <= 0));
    }
    
    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     * 
     * @param fieldName
     *            name of entity field being validated
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @return true if valid
     */
    protected boolean validate(String fieldName, Object entity, List<ValidationError> errors) {
        boolean isValid = true;
        
        return isValid;
    }
    
    /**
     * @param isValid
     *            if false constructs an appropriate ValidationError and adds it to the current list
     *            of errors
     * @param fieldName
     *            name of entity field being validated
     * @param fieldValue
     *            value of entity field being validated
     * @param expectedType
     *            expected type of entity field being validated
     * @param errors
     *            list of current errors
     * @return isMatch
     */
    protected boolean addError(boolean isValid, String fieldName, Object fieldValue, String expectedType,
            ErrorType errorType, List<ValidationError> errors) {
        if (!isValid && (errors != null)) {
            errors.add(new ValidationError(errorType, fieldName, fieldValue, new String[] { expectedType }));
        }
        return isValid;
    }
    
    /**
     * @param isValid
     *            if false constructs an appropriate ValidationError and adds it to the current list
     *            of errors
     * @param fieldName
     *            name of entity field being validated
     * @param fieldValue
     *            value of entity field being validated
     * @param expectedTypes
     *            array of symbols restricting entity field being validated
     * @param errors
     *            list of current errors
     * @return isMatch
     */
    protected boolean addError(boolean isValid, String fieldName, Object fieldValue, String[] expectedTypes,
            ErrorType errorType, List<ValidationError> errors) {
        if (!isValid && (errors != null)) {
            errors.add(new ValidationError(errorType, fieldName, fieldValue, expectedTypes));
        }
        return isValid;
    }
    
    /**
     * @return JSON representation of this SLI schema
     */
    public String toJson() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(this.getJsonHeader());
        
        if (!this.isPrimitive()) {
            buffer.append(this.getJsonFields("fields", this.getFields()));
            buffer.append(this.getJsonProperties("properties", this.getProperties()));
        }
        
        buffer.append(this.getJsonFooter());
        
        return buffer.toString();
    }
    
    private String getJsonHeader() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n");
        buffer.append("{");
        buffer.append("" + "\n" + "   \"type\":\"" + this.getType() + "\"");
        buffer.append("," + "\n" + "   \"validatorClass\":\"" + this.getValidatorClass() + "\"");
        buffer.append("," + "\n" + "   \"version\":\"" + this.getVersion() + "\"");
        
        return buffer.toString();
    }
    
    private String getJsonFields(String label, Map<String, Object> fields) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("," + "\n" + "   \"" + label + "\":{");
        
        if ((this.fields != null) && (this.getFields().size() > 0)) {
            String separator = "";
            for (String name : fields.keySet()) {
                Object object = fields.get(name);
                
                String description = "";
                if (object instanceof String) {
                    description = "\"" + object + "\"";
                } else if (object instanceof List) {
                    try {
                        description = MAPPER.writeValueAsString(object);
                    } catch (Exception exception) {
                    }
                } else if (object instanceof NeutralSchema) {
                    if (object instanceof ListSchema) {
                        List<NeutralSchema> schemaList = ((ListSchema) object).getList();
                        List list = new ArrayList();
                        for (NeutralSchema schema : schemaList) {
                            list.add(schema.getType());
                        }
                        try {
                            description = MAPPER.writeValueAsString(list);
                        } catch (Exception exception) {
                        }
                    } else {
                        description = "\"" + ((NeutralSchema) object).getType() + "\"";
                    }
                }
                
                buffer.append(separator + "\n" + "      \"" + name + "\":" + description);
                
                separator = ",";
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
            for (String name : properties.keySet()) {
                Object object = properties.get(name);
                
                String description = "";
                if (object instanceof List) {
                    try {
                        description = MAPPER.writeValueAsString(object);
                    } catch (Exception exception) {
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
    
    /**
     * @return XML representation of this SLI schema
     */
    public String toXml(boolean enableHierarchy) {
        StringBuffer buffer = new StringBuffer();
        
        if (this.isPrimitive()) {
            buffer.append("<primitive>" + escape(this.getType()) + "</primitive>" + "\n");
        } else {
            buffer.append(this.getXmlHeader());
            
            if (!this.isPrimitive()) {
                buffer.append(this.getXmlFields("fields", this.getFields(), enableHierarchy));
                buffer.append(this.getXmlProperties("properties", this.getProperties()));
            }
            
            buffer.append(this.getXmlFooter());
        }
        
        return buffer.toString();
    }
    
    private String getXmlHeader() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("\n");
        buffer.append("<" + escape(this.getType()) + " version=\"" + escape(this.getVersion()) + "\" validatorClass=\""
                + escape(this.getValidatorClass()) + "\">" + "\n");
        
        return buffer.toString();
    }
    
    private String getXmlFields(String label, Map<String, Object> fields, boolean enableHierarchy) {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<fields>" + "\n");
        
        for (String name : this.getFields().keySet()) {
            Object object = this.getFields().get(name);
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
        
        for (String name : this.getProperties().keySet()) {
            Object object = this.getProperties().get(name);
            
            String description = "";
            if (object instanceof List) {
                List list = (List) object;
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
        buffer.append("</" + escape(this.getType()) + ">" + "\n");
        
        return buffer.toString();
    }
    
    private String escape(String input) {
        return StringEscapeUtils.escapeXml(input);
    }
    
    public String toString() {
        return this.toJson();
    }
    
}
