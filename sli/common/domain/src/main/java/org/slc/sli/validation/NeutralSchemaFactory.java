package org.slc.sli.validation;

import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

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
    
    /* (non-Javadoc)
     * @see org.slc.sli.validation.SchemaFactory#createSchema(javax.xml.namespace.QName)
     */
    @Override
    public NeutralSchema createSchema(QName qName) {
        return createSchema(qName.getLocalPart());
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.validation.SchemaFactory#createSchema(java.lang.String)
     */
    @Override
    public NeutralSchema createSchema(String xsd) {
        NeutralSchemaType schemaType = NeutralSchemaType.findByName(xsd);
        if (schemaType != null) {
            switch (schemaType) {
                case BOOLEAN:
                    return new BooleanSchema(schemaType.getName());
                case INT:
                    return new IntegerSchema(schemaType.getName());
                case INTEGER:
                    return new IntegerSchema(schemaType.getName());
                case LONG:
                    return new LongSchema(schemaType.getName());
                case FLOAT:
                    return new FloatSchema(schemaType.getName());
                case DOUBLE:
                    return new DoubleSchema(schemaType.getName());
                case DECIMAL:
                    return new DecimalSchema(schemaType.getName());
                case DATE:
                    return new DateSchema(schemaType.getName());
                case TIME:
                    return new TimeSchema(schemaType.getName());
                case DATETIME:
                    return new DateTimeSchema(schemaType.getName());
                case DURATION:
                    return new DurationSchema(schemaType.getName());
                case STRING:
                    return new StringSchema(schemaType.getName());
                case ID:
                    return new StringSchema(schemaType.getName());
                case IDREF:
                    return new StringSchema(schemaType.getName());
                case RESTRICTED:
                    return new RestrictedSchema(schemaType.getName());
                case TOKEN:
                    return new TokenSchema(schemaType.getName());
                case LIST:
                    return new ListSchema(schemaType.getName());
                case COMPLEX:
                    return new ComplexSchema(schemaType.getName());
                default:
                    return null;
            }
        } else {
            return new ComplexSchema(xsd);
        }
    }
    
}
