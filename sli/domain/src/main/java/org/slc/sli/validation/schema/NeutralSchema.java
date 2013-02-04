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

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;
import org.slc.sli.validation.schema.Annotation.AnnotationType;

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
public abstract class NeutralSchema {

    // Attributes
    private String type = "";
    private String version = "1.0";
    private Map<String, Object> properties = null;
    private Map<String, NeutralSchema> fields = null;

    // Future Attributes
    private Properties docProperties = new Properties();
    private Properties customProperties = new Properties();
    private Map<String, Object> customFields = null;
    private String readConverter = null;
    private String writeConverter = null;

    private Map<Annotation.AnnotationType, Annotation> annotations = new LinkedHashMap<Annotation.AnnotationType, Annotation>();

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
    public abstract NeutralSchemaType getSchemaType();

    @JsonIgnore
    public String getValidatorClass() {
        return this.getClass().getName();
    }

    @JsonIgnore
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // Default value for subclasses.  Could be refactored, but not worth the added complexity.
    public boolean isPrimitive() {
        return true;
    }

    @JsonIgnore
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // Default value for subclasses.  Could be refactored, but not worth the added complexity.
    public boolean isSimple() {
        return true;
    }

    public Object convert(Object value) {
        return value;
    }

    public boolean isPii() {
        return this.getAppInfo() != null && this.getAppInfo().isPersonallyIdentifiableInfo();

    }

    public boolean isRestrictedForLogging() {
        return this.getAppInfo() != null && this.getAppInfo().isRestrictedFieldForLogging();

    }

    public boolean isRelaxedBlacklisted() {
        AppInfo myInfo = (AppInfo) annotations.get(AnnotationType.APPINFO);
        if (myInfo == null) {
            myInfo = new AppInfo(null);
            addAnnotation(myInfo);
        }

        return myInfo.isRelaxedBlacklisted();
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

    public void setFields(Map<String, NeutralSchema> fields) {
        this.fields = fields;
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
        AppInfo info = (AppInfo) annotations.get(AnnotationType.APPINFO);
        schema.inheritAnnotations(info);

        fields.put(name, schema);

    }

    protected void inheritAnnotations(AppInfo parentInfo) {
        // Nothing to inherit.
        if (parentInfo == null || parentInfo.getValues().isEmpty()) {
            return;
        }

        AppInfo myInfo = (AppInfo) annotations.get(AnnotationType.APPINFO);
        if (myInfo == null) {
            myInfo = new AppInfo(null);
            addAnnotation(myInfo);
        }
        myInfo.inherit(parentInfo);
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
        boolean isValid = this.validate("", entity, errors, null);
        return isValid && errors.size() <= 0;
    }

    /**
     * Validates the given entity
     * Returns true if the validation was successful or a ValidationException if the validation was
     * unsuccessful.
     *
     * @param entity
     *            being validated using this SLI Schema
     * @param errors
     *            list of current errors
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    public boolean validatePresent(Object entity, List<ValidationError> errors,
            Repository<Entity> repo) throws EntityValidationException {
        throw new EntityValidationException("", "", new ArrayList<ValidationError>());
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
     * @param repo
     *            reference to the entity repository
     * @return true if valid
     */
    protected abstract boolean validate(String fieldName, Object entity, List<ValidationError> errors,
            Repository<Entity> repo);

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
        if (!isValid && errors != null) {
            errors.add(new ValidationError(errorType, fieldName, fieldValue, new String[] { expectedType }));
        }
        return isValid;
    }

    protected boolean addError(String fieldName, Object fieldValue, String expectedType,
            ErrorType errorType, List<ValidationError> errors) {

        return this.addError(false, fieldName, fieldValue, expectedType, errorType, errors);
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
        if (!isValid && errors != null) {
            errors.add(new ValidationError(errorType, fieldName, fieldValue, expectedTypes));
        }
        return isValid;
    }

    public void addAnnotation(Annotation d) {
        annotations.put(d.getType(), d);
    }

    /**
     * Helper functions for annotations.
     */
    public Documentation getDocumentation() {
        return (Documentation) getAnnotation(AnnotationType.DOCUMENTATION);
    }

    public AppInfo getAppInfo() {
        return (AppInfo) getAnnotation(AnnotationType.APPINFO);
    }

    protected Annotation getAnnotation(Annotation.AnnotationType type) {
        if (annotations == null) {
            return null;
        }
        return annotations.get(type);
    }

}
