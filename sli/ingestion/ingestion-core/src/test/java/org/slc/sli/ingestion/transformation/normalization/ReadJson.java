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
        assertEquals("Section" , teacherSectionAssociation.getReferences().get(1).getRef().getEntityType());

        assertEquals("metaData.externalId", teacherSectionAssociation.getKeyFields().get(0));
        assertEquals("metaData.localId", teacherSectionAssociation.getKeyFields().get(1));
        assertEquals("body.studentId", teacherSectionAssociation.getKeyFields().get(2));
    }
}
