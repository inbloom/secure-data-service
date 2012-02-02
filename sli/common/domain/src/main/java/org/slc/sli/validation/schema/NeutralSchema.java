package org.slc.sli.validation.schema;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

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
public class NeutralSchema {
    
    // Constants
    public static final String JSON = "json";
    public static final String XML = "xml";
    
    // Jackson Mapper
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    
    // Attributes
    private String type = "";
    private NeutralSchemaType schemaType;
    private String version = "1.0";
    private Map<String, Object> properties = null;
    private Map<String, NeutralSchema> fields = null;
    
    // Future Attributes
    private Properties docProperties = new Properties();
    private Properties customProperties = new Properties();
    private Map<String, Object> customFields = null;
    private String readConverter = null;
    private String writeConverter = null;
    
    private boolean isPersonallyIdentifiableInfo = false;
    
    private Right readAuthority = Right.READ_GENERAL;
    private Right writeAuthority = Right.WRITE_GENERAL;
    private String documentation = null;
    
    // data sphere, used by security to identify whether the data is 'core', 'admin', etc.
    private String securitySphere = null;
    
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
    
    public final Map<String, NeutralSchema> getFields() {
        if (fields == null) {
            fields = new LinkedHashMap<String, NeutralSchema>();
        }
        return fields;
    }
    
    public void clearFields() {
        if (fields != null) {
            fields.clear();
        }
    }
    
    public void addField(String name, NeutralSchema schema) {
        if (fields == null) {
            fields = new LinkedHashMap<String, NeutralSchema>();
        }
        
        // Inherit only if the parent is more restrictive.
        if (isPersonallyIdentifiableInfo) {
            schema.isPersonallyIdentifiableInfo(isPersonallyIdentifiableInfo);
        }
        
        switch (readAuthority) {
            case FULL_ACCESS:
                schema.setReadAuthority(Right.FULL_ACCESS);
                break;
            case ADMIN_ACCESS:
                if (schema.getReadAuthority() != Right.FULL_ACCESS) {
                    schema.setReadAuthority(Right.ADMIN_ACCESS);
                }
                break;
            case READ_RESTRICTED:
                if (schema.getReadAuthority() != Right.FULL_ACCESS && schema.getReadAuthority() != Right.ADMIN_ACCESS) {
                    schema.setReadAuthority(Right.READ_RESTRICTED);
                }
            case READ_GENERAL:
                if (schema.getReadAuthority() == Right.ANONYMOUS_ACCESS) {
                    schema.setReadAuthority(Right.READ_GENERAL);
                }
        }
        
        switch (writeAuthority) {
            case FULL_ACCESS:
                schema.setWriteAuthority(Right.FULL_ACCESS);
                break;
            case ADMIN_ACCESS:
                if (schema.getWriteAuthority() != Right.FULL_ACCESS) {
                    schema.setWriteAuthority(Right.ADMIN_ACCESS);
                }
                break;
            case WRITE_RESTRICTED:
                if (schema.getWriteAuthority() != Right.FULL_ACCESS && schema.getWriteAuthority() != Right.ADMIN_ACCESS) {
                    schema.setWriteAuthority(Right.WRITE_RESTRICTED);
                }
                break;
            case WRITE_GENERAL:
                if (schema.getWriteAuthority() == Right.ANONYMOUS_ACCESS) {
                    schema.setWriteAuthority(Right.WRITE_GENERAL);
                }
                break;
        }
        
        if (schema.getSecuritySphere() == null) {
            schema.setSecuritySphere(securitySphere);
        }
        
        fields.put(name, schema);
    }
    
    public Boolean isPersonallyIdentifiableInfo() {
        return isPersonallyIdentifiableInfo;
    }
    
    public void isPersonallyIdentifiableInfo(boolean pii) {
        isPersonallyIdentifiableInfo = new Boolean(pii);
    }
    
    public Right getReadAuthority() {
        return readAuthority;
    }
    
    public void setReadAuthority(Right readAuthority) {
        this.readAuthority = readAuthority;
    }
    
    public Right getWriteAuthority() {
        return writeAuthority;
    }
    
    public void setWriteAuthority(Right writeAuthority) {
        this.writeAuthority = writeAuthority;
    }
    
    public String getDocumentation() {
        return documentation;
    }
    
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    public String getSecuritySphere() {
        return securitySphere;
    }
    
    public void setSecuritySphere(String securitySphere) {
        this.securitySphere = securitySphere;
    }
    
    // Future Methods
    @JsonIgnore
    public void setDocProperties(Properties properties) {
        docProperties = properties;
    }
    
    @JsonIgnore
    public Properties getDocProperties() {
        return docProperties;
    }
    
    @JsonIgnore
    public void setCustomProperties(Properties properties) {
        customProperties = properties;
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
    
    public String toJson() {
        NeutralSchemaStringWriter t = new NeutralSchemaJSONStringWriter();
        return t.transform(this);
    }
    
    public String toXml(boolean enableHierarchy) {
        NeutralSchemaStringWriter t = new NeutralSchemaXMLStringWriter(enableHierarchy);
        return t.transform(this);
    }
    
    @Override
    public String toString() {
        return toJson();
    }
    
}
