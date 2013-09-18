/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.bulk.extract.lea;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.bulk.extract.files.ExtractFile;


public class ExtractorFactoryTest {
    private ExtractorFactory factory;
    
    @Before
    public void setUp() {
        factory = new ExtractorFactory();
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testBuildEdorgExtractor() {
        Assert.assertTrue(factory.buildEdorgExtractor(null, null, null) != null);
        Assert.assertTrue(factory.buildEdorgExtractor(null, null, null).getClass() == EdorgExtractor.class);
    }
    
    @Test
    public void testBuildExtractFile() {
        Assert.assertTrue(factory.buildLEAExtractFile("bloop", "Bleep", "BLOO BLOO", null, null) != null);
        Assert.assertTrue(factory.buildLEAExtractFile("bloop", "Bleep", "BLOOB BLOO", null, null).getClass() == ExtractFile.class);
    }
    
    @Test
    public void testBuildAttendanceExtractor() {
        Assert.assertTrue(factory.buildAttendanceExtractor(null, null, null, null, null) != null);
        Assert.assertTrue(factory.buildAttendanceExtractor(null, null, null, null, null).getClass() == AttendanceExtractor.class);
    }
    
    @Test
    public void testBuildStudentExtractor() {
        Assert.assertTrue(factory.buildStudentExtractor(null, null, null, null) != null);
        Assert.assertTrue(factory.buildStudentExtractor(null, null, null, null).getClass() == StudentExtractor.class);
    }
    
    @Test
    public void testBuildStudentAssessmentExtractor() {
        Assert.assertTrue(factory.buildStudentAssessmentExtractor(null, null, null, null) != null);
        Assert.assertTrue(factory.buildStudentAssessmentExtractor(null, null, null, null).getClass() == StudentAssessmentExtractor.class);
    }
}
