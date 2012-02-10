package org.slc.sli.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;

/**
 * JUnit for validating section
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionValidation {

    @Autowired
    private EntityValidator validator;
    
    @Before
    public void init() {
    }
    
    private Entity goodSection() {
        final Map<String, Object> goodSection = new HashMap<String, Object>();
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
        goodSection.put("schoolId", "42");
        goodSection.put("sessionId", "MySessionId");
        return new Entity() {
            
            @Override
            public String getType() {
                return "section";
            }
            
            @Override
            public String getEntityId() {
                return "id";
            }
            
            @Override
            public Map<String, Object> getBody() {
                return goodSection;
            }
        };
    }
    
    @Test
    public void testSectionValidation() {
        Entity goodSection = goodSection();
        assertTrue(validator.validate(goodSection));
    }
    
    @Test
    public void testMinimumSection() {
        Entity minSection = goodSection();
        minSection.getBody().remove("educationalEnvironment");
        minSection.getBody().remove("mediumOfInstruction");
        minSection.getBody().remove("populationServed");
        minSection.getBody().remove("availableCredit");
        assertTrue(validator.validate(minSection));
    }
    
    @Test
    public void testMissingRequiredFields() {
        Entity missingSectionCode = goodSection();
        missingSectionCode.getBody().remove("uniqueSectionCode");
        assertFalse(validator.validate(missingSectionCode));
        
    }
}
