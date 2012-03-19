package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Json Configuration Unit Tests.
 *
 * @author npandey
 *
 */
public class ReadJson {
    @Test
    public void testTeacherSectionAssociation() throws Exception {
        File jsonFile = new File("src/test/resources/TeacherSectionAssociation.json");
        EntityConfig teacherSectionAssociation = EntityConfig.parse(jsonFile);

        assertEquals("metadata.externalId", teacherSectionAssociation.getReferences().get(0).getRef().getChoiceOfFields().get(0).get(0).getValues().get(0).getValueSource());
        assertEquals("Section" , teacherSectionAssociation.getReferences().get(1).getRef().getCollectionName());

        assertEquals("metaData.externalId", teacherSectionAssociation.getKeyFields().get(0));
        assertEquals("metaData.localId", teacherSectionAssociation.getKeyFields().get(1));
        assertEquals("body.studentId", teacherSectionAssociation.getKeyFields().get(2));
    }
}
