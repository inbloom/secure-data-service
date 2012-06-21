package org.slc.sli.validation.schema;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;

/**
 *
 * SLI Integer Schema which validates integer entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class IntegerSchema extends PrimitiveNumericSchema<Integer> {

    // Constructors
    public IntegerSchema() {
        super(NeutralSchemaType.INTEGER);
    }
    
    @Override
    public Object convert(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(value + " cannot be parsed to an integer");
            }
        }
        
        throw new IllegalArgumentException(value + " is not an integer");
    }
}
