package org.slc.sli.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.validation.schema.NeutralSchema;

/**
 * JUnit for validating section
 * 
 * @author nbrown
 * 
 */
public class SectionValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SectionValidation.class);
    
    @Autowired
    private SchemaRepository repo;
    
    @Test
    public void testSectionValidation() {
        NeutralSchema schema = repo.getSchema("section");
        Map<String, Object> goodSection = new HashMap<String, Object>();
        schema.validate(goodSection);
    }
}
