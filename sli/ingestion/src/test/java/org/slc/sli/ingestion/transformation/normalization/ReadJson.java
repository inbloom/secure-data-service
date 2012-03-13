package org.slc.sli.ingestion.transformation.normalization;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Json Configuration Unit Tests.
 *
 * @author npandey
 *
 */
public class ReadJson {

    @Test
    public void testTeacher() throws Exception {
        File jsonFile = new File("src/test/resources/Teacher.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref teacher = mapper.readValue(jsonFile , Ref.class);
        assertEquals("Teacher" , teacher.getCollectionName());
        for (List<Field> outerList : teacher.getChoiceOfFields()) {
            for (Field fields : outerList) {
                assertEquals("metadata.externalId" , fields.getPath());
                assertEquals("metadata.externalId" , fields.getValue().getValueSource());
            }
        }
    }

    @Test
    public void testSection() throws Exception {
        File jsonFile = new File("src/test/resources/Section.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref section = mapper.readValue(jsonFile , Ref.class);
        assertEquals("Section" , section.getCollectionName());
    }

    @Test
    public void testTeacherSectionAssociation() throws Exception {
        File jsonFile = new File("src/test/resources/TeacherSectionAssociation.json");
        ObjectMapper mapper = new ObjectMapper();
        Ref teacherSectionAssociation = mapper.readValue(jsonFile, Ref.class);
        assertEquals("teacherSectionAssociation" , teacherSectionAssociation.getCollectionName());
    }
}
