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

    final private String typeAString = "entityA";
    final private String typeBString = "entityB";

    @Before
    public void setup() {
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

        Map<String, List<TranslationTask>> translationMap =
                new HashMap<String, List<TranslationTask>>();
        translationMap.put(typeAString, listA);
        translationMap.put(typeBString, listB);

        sifTranslationManager = new SifTranslationManager();

        sifTranslationManager.setTranslationMap(translationMap);

        List<SliEntity> sliXList = new ArrayList();
        sliXList.add(sliX);
        List<SliEntity> sliYList = new ArrayList();
        sliYList.add(sliY);
        List<SliEntity> sliZList = new ArrayList();
        sliZList.add(sliZ);

        //mock translator results
        Mockito.when(mockTranslationAtoX.translate(Mockito.eq(sifA))).thenReturn(sliXList);
        Mockito.when(mockTranslationAtoY.translate(Mockito.eq(sifA))).thenReturn(sliYList);
        Mockito.when(mockTranslationBtoZ.translate(Mockito.eq(sifB))).thenReturn(sliZList);
    }

    @Test
    public void shouldCreateOneToOneTranslatedEntities() {
        List<SliEntity> sliEntities = sifTranslationManager.translate(sifB);

        Assert.assertEquals("Should translate to one sli entity", 1, sliEntities.size());
        Assert.assertEquals("First translated entity not of correct", sliZ, sliEntities.get(0));
    }

    @Test
    public void shouldCreateOneToManyTranslatedEntities() {
        List<SliEntity> sliEntities = sifTranslationManager.translate(sifA);

        Assert.assertEquals("Should translate to two sli entities", 2, sliEntities.size());
        Assert.assertEquals("First translated entity not of correct", sliX, sliEntities.get(0));
        Assert.assertEquals("First translated entity not of correct", sliY, sliEntities.get(1));
    }
}
