package org.slc.sli.validation.schema;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;

/**
 *
 * SLI Double Schema which validates double-precision floating point entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class DoubleSchema extends PrimitiveNumericSchema<Double> {

    // Constructors
    public DoubleSchema() {
        super(NeutralSchemaType.DOUBLE);
    }
    

    @Override
    public Object convert(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(value + " cannot be parsed to a double");
            }
        }
        
        throw new IllegalArgumentException(value + " is not a double");
    }

}
