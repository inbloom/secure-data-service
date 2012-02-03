package org.slc.sli.validation.schema;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.ValidationError.ErrorType;

/**
 * JUnit for choice schemas
 * 
 * @author nbrown
 * 
 */
public class ChoiceSchemaTest {
    
    @Test
    public void test() {
        NeutralSchema stringSchema = new StringSchema();
        NeutralSchema longSchema = new LongSchema();
        Map<String, NeutralSchema> optionMap = new LinkedHashMap<String, NeutralSchema>();
        optionMap.put("stringOption", stringSchema);
        optionMap.put("longOption", longSchema);
        NeutralSchema choice = new ChoiceSchema(optionMap);
        List<ValidationError> errors = new ArrayList<ValidationError>();
        assertTrue(choice.validate("stringOption", "TestString", errors));
        assertTrue(errors.isEmpty());
        assertTrue(choice.validate("longOption", 5L, errors));
        assertTrue(errors.isEmpty());
        choice.validate("stringOption", 1L, errors);
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals(ErrorType.INVALID_DATATYPE, error.getType());
        assertArrayEquals(new String[] { "string" }, error.getExpectedTypes());
        choice.validate("noOption", 1L, errors);
        assertEquals(2, errors.size());
        ValidationError noOptionError = errors.get(1);
        assertEquals(ErrorType.UNKNOWN_FIELD, noOptionError.getType());
        assertArrayEquals(new String[] { "stringOption", "longOption" }, noOptionError.getExpectedTypes());
    }
    
}
