package org.slc.sli.sample.transform;
import org.slc.sli.sample.entities.GradeLevelType;

import junit.framework.TestCase;


public class CcsCsv2XmlTransformerTest extends TestCase {
    
    private CcsCsv2XmlTransformer ccsCsv2XmlTransformer = new CcsCsv2XmlTransformer();
    
    public void testGetGradeLevel() {
        assertEquals(GradeLevelType.KINDERGARTEN, ccsCsv2XmlTransformer.getGradeLevel(0));
        assertEquals(GradeLevelType.ELEVENTH_GRADE, ccsCsv2XmlTransformer.getGradeLevel(11));
        try {
            ccsCsv2XmlTransformer.getGradeLevel(13);
            fail();
        } catch(Exception e) {
            assertTrue(true);
        }
    }
}
