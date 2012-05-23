package org.slc.sli.validation;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class NeutralSchemaFactory implements SchemaFactory, ApplicationContextAware {

    @Resource(name = "validationStrategyList")
    private List<AbstractBlacklistStrategy> validationStrategyList;

    @Resource(name = "relaxedValidationStrategyList")
    private List<AbstractBlacklistStrategy> relaxedValidationStrategyList;

    private ApplicationContext applicationContext;

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
                return new BooleanSchema(schemaType.getName());
            case INT:
                return new IntegerSchema(schemaType.getName());
            case INTEGER:
                return new IntegerSchema(schemaType.getName());
            case LONG:
                return new LongSchema(schemaType.getName());
            case DOUBLE:
                return new DoubleSchema(schemaType.getName());
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
                return getReferenceSchema(schemaType);
            case CHOICE:
                return new ChoiceSchema(schemaType.getName());
            default:
                return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private NeutralSchema getReferenceSchema(NeutralSchemaType schemaType) {
        if (applicationContext != null) {
            return new ReferenceSchema(schemaType.getName(),
                    applicationContext.getBean(SchemaRepository.class));
        } else {
            return new ReferenceSchema(schemaType.getName());
        }
    }



}
