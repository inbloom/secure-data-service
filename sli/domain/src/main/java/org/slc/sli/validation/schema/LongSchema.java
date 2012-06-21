package org.slc.sli.validation.schema;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;

/**
 *
 * SLI Long Schema which validates long entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class LongSchema extends PrimitiveNumericSchema<Long> {

    // Constructors
    public LongSchema() {
        super(NeutralSchemaType.LONG);
    }
    

    @Override
    public Object convert(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(value + " cannot be parsed to a long");
            }
        }
        
        throw new IllegalArgumentException(value + " is not a long");
    }

}
