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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.ElementDef;
import openadk.library.SIFDataObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.sif.domain.slientity.SliEntity;

/**
 *
 * SifTranslationManager unit tests
 *
 */
@SuppressWarnings("rawtypes")
public class SifTranslationManagerTest {
    SifTranslationManager sifTranslationManager;

    TranslationTask mockTranslationAtoX;
    TranslationTask mockTranslationAtoY;
    TranslationTask mockTranslationBtoZ;

    SliEntity sliX;
    SliEntity sliY;
    SliEntity sliZ;

    SIFDataObject sifA;
    SIFDataObject sifB;

    ElementDef typeA;
    ElementDef typeB;

    private final String typeAString = "entityA";
    private final String typeBString = "entityB";

    @Before
    public void setup() throws SifTranslationException {
        typeA = Mockito.mock(ElementDef.class);
        typeB = Mockito.mock(ElementDef.class);
        Mockito.when(typeA.toString()).thenReturn(typeAString);
        Mockito.when(typeB.toString()).thenReturn(typeBString);

        mockTranslationAtoX = Mockito.mock(TranslationTask.class);
        mockTranslationAtoY = Mockito.mock(TranslationTask.class);
        mockTranslationBtoZ = Mockito.mock(TranslationTask.class);

        sliX = Mockito.mock(SliEntity.class);
        sliY = Mockito.mock(SliEntity.class);
        sliZ = Mockito.mock(SliEntity.class);

        sifA = Mockito.mock(SIFDataObject.class);
        sifB = Mockito.mock(SIFDataObject.class);

        Mockito.when(sifA.getObjectType()).thenReturn(typeA);
        Mockito.when(sifB.getObjectType()).thenReturn(typeB);

        List<TranslationTask> listA = new ArrayList<TranslationTask>();
        listA.add(mockTranslationAtoX);
        listA.add(mockTranslationAtoY);

        List<TranslationTask> listB = new ArrayList<TranslationTask>();
        listB.add(mockTranslationBtoZ);

        Map<String, List<TranslationTask>> translationMap = new HashMap<String, List<TranslationTask>>();
        translationMap.put(typeAString, listA);
        translationMap.put(typeBString, listB);

        sifTranslationManager = new SifTranslationManager();

        sifTranslationManager.setTranslationMap(translationMap);

        List<SliEntity> sliXList = new ArrayList<SliEntity>();
        sliXList.add(sliX);
        List<SliEntity> sliYList = new ArrayList<SliEntity>();
        sliYList.add(sliY);
        List<SliEntity> sliZList = new ArrayList<SliEntity>();
        sliZList.add(sliZ);

        // mock translator results
        Mockito.when(mockTranslationAtoX.translate(Mockito.eq(sifA), Mockito.anyString())).thenReturn(sliXList);
        Mockito.when(mockTranslationAtoY.translate(Mockito.eq(sifA), Mockito.anyString())).thenReturn(sliYList);
        Mockito.when(mockTranslationBtoZ.translate(Mockito.eq(sifB), Mockito.anyString())).thenReturn(sliZList);
    }

    @Test
    public void shouldCreateOneToOneTranslatedEntities() {
        List<SliEntity> sliEntities = sifTranslationManager.translate(sifB, "zoneId");

        Assert.assertEquals("Should translate to one sli entity", 1, sliEntities.size());
        Assert.assertEquals("First translated entity not of correct", sliZ, sliEntities.get(0));
    }

    @Test
    public void shouldCreateOneToManyTranslatedEntities() {
        List<SliEntity> sliEntities = sifTranslationManager.translate(sifA, "zoneId");

        Assert.assertEquals("Should translate to two sli entities", 2, sliEntities.size());
        Assert.assertEquals("First translated entity not of correct", sliX, sliEntities.get(0));
        Assert.assertEquals("First translated entity not of correct", sliY, sliEntities.get(1));
    }

    @Test
    public void shouldHandleSifTranslationExceptions() throws SifTranslationException {
        Mockito.when(mockTranslationBtoZ.translate(Mockito.eq(sifB), Mockito.anyString())).thenThrow(
                new SifTranslationException("test throw"));
        sifTranslationManager.translate(sifA, "zoneId");
    }
}
