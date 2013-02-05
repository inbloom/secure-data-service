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
import java.util.List;
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
    public void testGetDenormalizedDocuments() {
        assertTrue(EmbeddedDocumentRelations.getDenormalizedDocuments().contains("studentProgramAssociation"));
        assertFalse(EmbeddedDocumentRelations.getDenormalizedDocuments().contains("foobar"));
    }

    @Test
    public void testGetDenormalizedToEntity() {
        String entityType = "studentProgramAssociation";
        assertEquals("student", EmbeddedDocumentRelations.getDenormalizeToEntity(entityType));
    }

    @Test
    public void testGetReferenceKeys() {
        String entityType = "studentProgramAssociation";
        Map<String, String> actual = EmbeddedDocumentRelations.getReferenceKeys(entityType);
        assertEquals(1, actual.keySet().size());
        assertTrue(actual.containsKey("studentId"));
        assertEquals("_id", actual.get("studentId"));
    }

    @Test
    public void testGetDenormalizedIdKey() {
        String entityType = "studentProgramAssociation";
        String expected = "programId";
        String actual = EmbeddedDocumentRelations.getDenormalizedIdKey(entityType);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDenormalizedFields() {
        String entityType = "studentProgramAssociation";
        List<String> expected = Arrays.asList("endDate");
        List<String> actual = EmbeddedDocumentRelations.getDenormalizedBodyFields(entityType);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDenormalizedToField() {
        String entityType = "studentProgramAssociation";
        String expected = "program";
        String actual = EmbeddedDocumentRelations.getDenormalizedToField(entityType);
        assertEquals(expected, actual);
    }
}
