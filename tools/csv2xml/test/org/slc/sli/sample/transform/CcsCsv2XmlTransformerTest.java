/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
