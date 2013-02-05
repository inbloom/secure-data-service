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
package org.slc.sli.validation;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Extract from Xsd which fields are marked as self referencing
 * @author ablum
 */

@Component
public class SelfReferenceExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(NaturalKeyExtractor.class);

    @Autowired
    protected SchemaRepository entitySchemaRegistry;

    public String getSelfReferenceFields(Entity entity) {

        String selfReferenceField = null;

        NeutralSchema schema = entitySchemaRegistry.getSchema(entity.getType());
        if (schema != null) {

            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {

                selfReferenceField = getSelfReferenceFields(schema, "");

            }
        }

        return selfReferenceField;
    }

    /**
     * Recursive method to traverse down to the leaf nodes of a neutral schema and extract annotated
     * self referencing entities
     */
    private String getSelfReferenceFields(NeutralSchema schema, String baseXPath) {
        Map<String, NeutralSchema> fields = getSchemaFields(schema);

        for (Entry<String, NeutralSchema> fieldEntry : fields.entrySet()) {
            String fieldXPath = baseXPath + fieldEntry.getKey();

            NeutralSchema fieldSchema = fieldEntry.getValue();

            AppInfo fieldsAppInfo = fieldSchema.getAppInfo();
            if (fieldsAppInfo != null) {
                boolean isSelfReference = fieldsAppInfo.isSelfReference();
                if (isSelfReference) {
                    if (fieldSchema instanceof ComplexSchema) {
                        getSelfReferenceFields(fieldSchema, fieldXPath + ".");
                    } else {
                        return fieldXPath;
                    }
                } else {
                    String schemaClass = fieldSchema.getValidatorClass();
                    if (schemaClass.equals ("org.slc.sli.validation.schema.ChoiceSchema")) {
                        getSelfReferenceFields(fieldSchema, fieldXPath + ".");
                    }
                }
            }

        }
        return null;
    }

    /*
     * Created to allow for easier unit testing
     */
    public Map<String, NeutralSchema> getSchemaFields(NeutralSchema schema) {
        return schema.getFields();
    }
}
