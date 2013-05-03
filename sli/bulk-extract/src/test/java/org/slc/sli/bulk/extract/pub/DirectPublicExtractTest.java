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

package org.slc.sli.bulk.extract.pub;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;

/**
 * User: ablum
 */
public class DirectPublicExtractTest {

    private EntityExtractor extractor;
    private ExtractFile file;
    private DirectPublicDataExtract edorg;

    @Before
    public void setUp() throws Exception {
        extractor = Mockito.mock(EntityExtractor.class);
        file = Mockito.mock(ExtractFile.class);
        edorg = new DirectPublicDataExtract(extractor);
    }

    @Test
    public void testExtractEducationOrganization() {
        edorg.extract("SEA", file);
        Mockito.verify(extractor, Mockito.times(1)).extractEntities(file, "educationOrganization");
    }

    @Test
    public void testExtractCourse() {
        edorg.extract("SEA", file);
        Mockito.verify(extractor, Mockito.times(1)).extractEntities(file, "course");
    }

    @Test
    public void testExtractCourseOffering() {
        edorg.extract("SEA", file);
        Mockito.verify(extractor, Mockito.times(1)).extractEntities(file, "courseOffering");
    }

    @Test
    public void testExtractSession() {
        edorg.extract("SEA", file);
        Mockito.verify(extractor, Mockito.times(1)).extractEntities(file, "session");
    }

    @Test
    public void testExtractGraduationPlan() {
        edorg.extract("SEA", file);
        Mockito.verify(extractor, Mockito.times(1)).extractEntities(file, "graduationPlan");
    }
}
