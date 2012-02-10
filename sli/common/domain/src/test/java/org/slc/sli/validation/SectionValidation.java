package org.slc.sli.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.validation.schema.NeutralSchema;

/**
 * JUnit for validating section
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SectionValidation.class);
    
    @Autowired
    private SchemaRepository repo;
    
    @Test
    public void testSectionValidation() {
        NeutralSchema schema = repo.getSchema("section");
        assertNotNull(schema);
        Map<String, Object> goodSection = new HashMap<String, Object>();
        goodSection.put("uniqueSectionCode", "Math101");
        goodSection.put("sequenceOfCourse", 1);
        goodSection.put("educationalEnvironment", "Classroom");
        goodSection.put("mediumOfInstruction", "Face-to-face instruction");
        goodSection.put("populationServed", "Regular Students");
        Map<String, Object> credit = new HashMap<String, Object>();
        credit.put("credit", 1.5);
        credit.put("creditType", "Semester hour credit");
        credit.put("creditConversion", 2.5);
        goodSection.put("credits", credit);
        schema.validate(goodSection);
    }
}
