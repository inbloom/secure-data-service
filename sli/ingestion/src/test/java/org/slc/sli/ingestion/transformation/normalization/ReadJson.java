package org.slc.sli.ingestion.transformation.normalization;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class ReadJson {

    @Test
    public void testTeacher() throws Exception {
        File jsonFile = new File("src/test/resources/Teacher.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref teacher = mapper.readValue(jsonFile , Ref.class);
        assertEquals("Teacher" , teacher.getCollectionName());
    }

    /*@Test
    public void testSection() throws Exception {
        File jsonFile = new File("src/test/resources/Section.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref teacher = mapper.readValue(jsonFile , Ref.class);
        assertEquals("Section" , teacher.getCollectionName());
    }*/

}
