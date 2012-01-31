package org.slc.sli.validation.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnits for TimeSchema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TimeSchemaTest {
    
    @Autowired
    TimeSchema schema;
    
    @Test
    public void testTimeStringValidation() throws IllegalArgumentException {
        String timeString = "12:00:00-05:00";
        assertTrue("Time entity validation failed", schema.validate(timeString));
    }
    
    @Test
    public void testTimeValidation() {
        Calendar calendar = Calendar.getInstance();
        String timeString = javax.xml.bind.DatatypeConverter.printTime(calendar);
        assertTrue("Time entity validation failed", schema.validate(timeString));
    }
    
    @Test
    public void testValidationOfStringFailure() {
        String stringEntity = "";
        assertFalse("Expected TimeSchema string validation failure did not succeed", schema.validate(stringEntity));
    }
    
}
