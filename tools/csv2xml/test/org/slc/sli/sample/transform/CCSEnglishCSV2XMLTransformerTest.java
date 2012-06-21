package org.slc.sli.sample.transform;

import junit.framework.TestCase;

public class CCSEnglishCSV2XMLTransformerTest extends TestCase {
    public void testGradeLevelMapper() {
        CCSEnglishCSV2XMLTransformer.EnglishGradeLevelMapper englishGradeLevelMapper = new CCSEnglishCSV2XMLTransformer.EnglishGradeLevelMapper();
        assertEquals(9, englishGradeLevelMapper.getGradeLevel("9-10.blah"));
        assertEquals(0, englishGradeLevelMapper.getGradeLevel("K.something"));
    }
}
