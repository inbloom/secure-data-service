package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
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
        RefDef teacher = mapper.readValue(jsonFile , RefDef.class);
        assertEquals("Teacher" , teacher.getRef().getCollectionName());
        for (List<Field> outerList : teacher.getRef().getChoiceOfFields()) {
            for (Field fields : outerList) {
                assertEquals("metadata.externalId" , fields.getPath());
                assertEquals("metadata.externalId" , fields.getValues().get(0).getValueSource());
            }
        }
    }

    @Test
    public void testSection() throws Exception {
        File jsonFile = new File("src/test/resources/Section.json");
        ObjectMapper mapper = new ObjectMapper();
        RefDef section = mapper.readValue(jsonFile , RefDef.class);
        assertEquals("Section" , section.getRef().getCollectionName());
    }

    @Test
    public void testTeacherSectionAssociation() throws Exception {
        File jsonFile = new File("src/test/resources/TeacherSectionAssociation.json");
        ObjectMapper mapper = new ObjectMapper();
        RefDef teacherSectionAssociation = mapper.readValue(jsonFile, RefDef.class);
        assertEquals("Teacher" , teacherSectionAssociation.getRef().getCollectionName());
    }
}
