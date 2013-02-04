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

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.schema.BooleanSchema;
import org.slc.sli.validation.schema.ChoiceSchema;
import org.slc.sli.validation.schema.ComplexSchema;
import org.slc.sli.validation.schema.DateSchema;
import org.slc.sli.validation.schema.DateTimeSchema;
import org.slc.sli.validation.schema.DoubleSchema;
import org.slc.sli.validation.schema.DurationSchema;
import org.slc.sli.validation.schema.IntegerSchema;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.LongSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.ReferenceSchema;
import org.slc.sli.validation.schema.StringSchema;
import org.slc.sli.validation.schema.TimeSchema;
import org.slc.sli.validation.schema.TokenSchema;
import org.slc.sli.validation.strategy.AbstractBlacklistStrategy;

/**
 *
 * SLI Schema Factory which creates Schema instances based upon Ed-Fi type.
 * File persistence methods are also provided.
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Component
public class NeutralSchemaFactory implements SchemaFactory {

    @Resource(name = "validationStrategyList")
    private List<AbstractBlacklistStrategy> validationStrategyList;

    @Resource(name = "relaxedValidationStrategyList")
    private List<AbstractBlacklistStrategy> relaxedValidationStrategyList;

    @Autowired
    private SchemaRepositoryProvider schemaRepositoryProvider;

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.validation.SchemaFactory#createSchema(javax.xml.namespace.QName)
     */
    @Override
    public NeutralSchema createSchema(QName qName) {
        return createSchema(qName.getLocalPart());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.slc.sli.validation.SchemaFactory#copySchema(org.slc.sli.validation.schema.NeutralSchema)
     */
    @Override
    public NeutralSchema copySchema(NeutralSchema toCopy) {
        NeutralSchema copy = createSchema(toCopy.getSchemaType());
        copy.setProperties(new HashMap<String, Object>(toCopy.getProperties()));
        copy.setType(toCopy.getType());
        copy.setFields(toCopy.getFields());
        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.validation.SchemaFactory#createSchema(java.lang.String)
     */
    @Override
    public NeutralSchema createSchema(String xsd) {
        NeutralSchemaType schemaType = NeutralSchemaType.findByName(xsd);
        if (schemaType != null) {
            return createSchema(schemaType);
        } else {
            return new ComplexSchema(xsd);
        }
    }

    private NeutralSchema createSchema(NeutralSchemaType schemaType) {
        switch (schemaType) {
            case BOOLEAN:
                return new BooleanSchema();
            case INT:
                return new IntegerSchema();
            case INTEGER:
                return new IntegerSchema();
            case LONG:
                return new LongSchema();
            case DOUBLE:
                return new DoubleSchema();
            case DATE:
                return new DateSchema(schemaType.getName());
            case TIME:
                return new TimeSchema(schemaType.getName());
            case DATETIME:
                return new DateTimeSchema(schemaType.getName());
            case DURATION:
                return new DurationSchema(schemaType.getName());
            case STRING:
                return new StringSchema(schemaType.getName(), validationStrategyList, relaxedValidationStrategyList);
            case ID:
                return new StringSchema(schemaType.getName(), validationStrategyList, relaxedValidationStrategyList);
            case IDREF:
                return new StringSchema(schemaType.getName(), validationStrategyList, relaxedValidationStrategyList);
            case TOKEN:
                return new TokenSchema(schemaType.getName());
            case LIST:
                return new ListSchema(schemaType.getName());
            case COMPLEX:
                return new ComplexSchema(schemaType.getName());
            case REFERENCE:
                return new ReferenceSchema(schemaType.getName(),
                        schemaRepositoryProvider != null ? schemaRepositoryProvider.getSchemaRepository() : null);
            case CHOICE:
                return new ChoiceSchema(schemaType.getName());
            default:
                return null;
        }
    }


}
