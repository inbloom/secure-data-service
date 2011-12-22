package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests sample Avro schema for Students.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AvroSchemaTest {
    
    @Autowired
    private EntitySchemaRegistry schemaReg;

    @SuppressWarnings("unchecked")
    @Test
    public void testValidStudent() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/student_fixture.json"));
        String student;
        while ((student = reader.readLine()) != null) {
            ObjectMapper oRead = new ObjectMapper();
            Map<String, Object> obj = oRead.readValue(student, Map.class);
            mapValidation((Map<String, Object>) obj.get("body"), "student");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testValidSchool() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/school_fixture.json"));
        String school;
        while ((school = reader.readLine()) != null) {
            ObjectMapper oRead = new ObjectMapper();
            Map<String, Object> obj = oRead.readValue(school, Map.class);
            mapValidation((Map<String, Object>) obj.get("body"), "school");
        }
    }

    private void mapValidation(Map<String, Object> obj, String schemaName) {
        AvroEntityValidator validator = new AvroEntityValidator();
        validator.setSchemaRegistry(schemaReg);
        
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        when(e.getType()).thenReturn(schemaName);
        
        try {
            assertTrue(validator.validate(e));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.out.println(err);
            }
            fail();
        }
    }
    
    /**
     * This is left here are PoC code - but I don't think that we want to validate JSON files,
     * rather, we want to do map validation.
     * 
     * @param obj
     * @param schema
     * @throws Exception
     */
    private void validate(Object obj, Schema schema) throws Exception {
        
        GenericDatumReader<GenericRecord> r = new GenericDatumReader<GenericRecord>(schema);
        r.setExpected(schema);
        File file = new File("src/test/resources/school_fixture2.json");
        DecoderFactory factory = DecoderFactory.get();
        Decoder decoder = factory.resolvingDecoder(schema, schema,
                factory.jsonDecoder(schema, new FileInputStream(file)));
        GenericRecord rec = (GenericRecord) r.read(null, decoder);
        if (schema.equals(rec.getSchema())) {
            System.out.println(rec);
        }
    }
}
