package org.slc.sli.validation.schema;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.validation.NeutralSchemaType;

/**
 *
 * SLI Boolean Schema which validates boolean entities
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */
@Scope("prototype")
@Component
public class BooleanSchema extends PrimitiveSchema {

    public BooleanSchema() {
        super(NeutralSchemaType.BOOLEAN);
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String stringData = (String) value;
            stringData = stringData.toLowerCase();
            if (stringData.equals("true")) {
                return Boolean.TRUE;
            } else if (stringData.equals("false")) {
                return Boolean.FALSE;
            } else {
                throw new IllegalArgumentException(stringData + " cannot be parsed to a boolean");
            }
        }
        
        throw new IllegalArgumentException(value + " is not a boolean");
    }
    
}
