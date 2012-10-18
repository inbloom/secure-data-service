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

package org.slc.sli.common.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(EmbeddedDocumentRelations.getParents("studentSectionAssociation").get(0).getParentEntityType(), "section");
        assertEquals(EmbeddedDocumentRelations.getParents("studentSectionAssociation").get(0).getParentReferenceFieldName(), "sectionId");
    }

    @Test
    public void nonSubDocTest() {
        assertTrue(EmbeddedDocumentRelations.getParents("does not exist") == null);
        assertTrue(EmbeddedDocumentRelations.getParents("does not exist") == null);
    }
}
