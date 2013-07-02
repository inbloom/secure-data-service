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

package org.slc.sli.common.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

/**
 *
 * TODO: add class javadoc
 *
 */
public class EmbeddedDocumentRelationsTest {

    @Test
    public void studentSectionAssociationTest() {
        assertTrue(EmbeddedDocumentRelations.getSubDocuments().contains("studentSectionAssociation"));
        assertEquals(EmbeddedDocumentRelations.getParentEntityType("studentSectionAssociation"), "section");
        assertEquals(EmbeddedDocumentRelations.getParentFieldReference("studentSectionAssociation"), "sectionId");
    }

    @Test
    public void nonSubDocTest() {
        assertTrue(EmbeddedDocumentRelations.getParentEntityType("does not exist") == null);
        assertTrue(EmbeddedDocumentRelations.getParentFieldReference("does not exist") == null);
    }

    @Test
    public void testDenormalization() {
        String denormalizedEntityType = "studentSchoolAssociation";
        assertTrue(EmbeddedDocumentRelations.getDenormalizedDocuments().contains(denormalizedEntityType));
        assertFalse(EmbeddedDocumentRelations.getDenormalizedDocuments().contains("foobar"));
        assertEquals("student", EmbeddedDocumentRelations.getDenormalizeToEntity(denormalizedEntityType));
        assertEquals("schoolId", EmbeddedDocumentRelations.getDenormalizedIdKey(denormalizedEntityType));
        assertEquals(Arrays.asList("entryDate", "entryGradeLevel", "exitWithdrawDate"),
                EmbeddedDocumentRelations.getDenormalizedBodyFields(denormalizedEntityType));
        assertEquals("schools", EmbeddedDocumentRelations.getDenormalizedToField(denormalizedEntityType));
        Map<String, String> referenceKeys = EmbeddedDocumentRelations.getReferenceKeys(denormalizedEntityType);
        assertEquals(1, referenceKeys.keySet().size());
        assertTrue(referenceKeys.containsKey("studentId"));
        assertEquals("_id", referenceKeys.get("studentId"));
    }

}
