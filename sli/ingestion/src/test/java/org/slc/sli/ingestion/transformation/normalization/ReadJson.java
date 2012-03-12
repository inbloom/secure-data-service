package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.fail;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class ReadJson {

    @Test
    public void test() throws Exception {
        File jsonFile = new File("src/test/resources/Teacher.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref teacher = mapper.readValue(jsonFile , Ref.class);
        assertEquals("Teacher" , teacher.getCollectionName());
    }

}
