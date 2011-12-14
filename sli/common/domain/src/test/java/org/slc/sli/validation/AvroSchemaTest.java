package org.slc.sli.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.slc.sli.domain.Entity;

/**
 * Tests sample Avro schema for Students.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public class AvroSchemaTest {
    
    @Test
    public void testValidSchool() throws Exception {
        Parser parser = new Schema.Parser();
        Schema schema = parser.parse(new File("src/main/resources/avroSchema/school_body.avpr"));
        
        String school = new BufferedReader(new FileReader("src/test/resources/school_fixture.json")).readLine();
        ObjectMapper oRead = new ObjectMapper();
        Map obj = oRead.readValue(school, Map.class);
        mapValidation(obj, schema);
        
    }
    
    private void mapValidation(Map obj, Schema schema) {
        AvroEntityValidator validator = new AvroEntityValidator();
        EntitySchemaRegistry mockSchemaRegistry = mock(EntitySchemaRegistry.class);
        
        validator.setSchemaRegistry(mockSchemaRegistry);
        Entity e = mock(Entity.class);
        when(e.getBody()).thenReturn(obj);
        
        when(mockSchemaRegistry.findSchemaForType(e)).thenReturn(schema);
        validator.validate(e);
        
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
