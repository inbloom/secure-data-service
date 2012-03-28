package org.slc.sli.ingestion.transformation.normalization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Json Configuration Unit Tests.
 *
 * @author npandey
 *
 */
public class ReadJson {
    @Test
    public void testTeacherSectionAssociation() throws Exception {
        Resource jsonFile = new ClassPathResource("TeacherSectionAssociation.json");
        EntityConfig teacherSectionAssociation = EntityConfig.parse(jsonFile.getInputStream());

        assertEquals("metadata.externalId", teacherSectionAssociation.getReferences().get(0).getRef().getChoiceOfFields().get(0).get(0).getValues().get(0).getValueSource());
        assertEquals("Section" , teacherSectionAssociation.getReferences().get(1).getRef().getCollectionName());

        assertEquals("metaData.externalId", teacherSectionAssociation.getKeyFields().get(0));
        assertEquals("metaData.localId", teacherSectionAssociation.getKeyFields().get(1));
        assertEquals("body.studentId", teacherSectionAssociation.getKeyFields().get(2));
    }
}
