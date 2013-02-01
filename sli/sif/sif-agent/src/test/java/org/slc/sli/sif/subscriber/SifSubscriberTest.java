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


package org.slc.sli.sif.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.ADKException;
import openadk.library.DataObjectInputStream;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;
import openadk.library.student.SchoolInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SlcInterface;
import org.slc.sli.sif.translation.SifTranslationManager;

/**
 * JUnit tests for SifSubscriber
 *
 * @author jtully
 *
 */
public class SifSubscriberTest {

    @InjectMocks
    SifSubscriber subscriber;

    @Mock
    private SlcInterface mockSlcInterface;

    @Mock
    private SifTranslationManager mockTranslationManager;

    @Mock
    private SifIdResolver sifIdResolver;

    private static final String SIF_ID = "sif_id";
    private static final String OTHER_SIF_ID = "other_sif_id";
    private static final String CREATOR_SIF_ID = "creator_sif_id";
    private static final String SLI_ID = "sli_id";
    private static final String SIF_TYPE = "sif_type";
    private static final String SLI_TYPE = "sli_type";
    private static final String ZONE_ID = "zone_id";

    @Before
    public void setup() {
        subscriber = new SifSubscriber();

        MockitoAnnotations.initMocks(this);

        Mockito.when(mockSlcInterface.sessionCheck()).thenReturn("token");
    }

    @Test
    public void shouldExtendSubscriber() {
        Assert.assertTrue("SifSubscriber should extend Subscriber", subscriber instanceof Subscriber);
    }

    @Test
    public void shouldCallSessionCheckOnEvent() throws ADKException {
        SIFDataObject mockSifDataObject = createMockSifDataObject();
        Event mockEvent = createMockSifEvent(false, mockSifDataObject);
        Zone mockZone = createMockZone();
        MessageInfo mockInfo = createMockInfo();

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK sessionCheck call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).sessionCheck();

    }

    @Test
    public void testAddEntityOnEvent() throws ADKException {
        SIFDataObject mockSifDataObject = createMockSifDataObject();
        Event mockEvent = createMockSifEvent(false, mockSifDataObject);
        Zone mockZone = createMockZone();
        MessageInfo mockInfo = createMockInfo();

        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        GenericEntity mockGenericEntity = Mockito.mock(GenericEntity.class);
        Mockito.when(mockSliEntity.createGenericEntity()).thenReturn(mockGenericEntity);
        Mockito.when(mockSliEntity.entityType()).thenReturn(SLI_TYPE);
        Mockito.when(mockSliEntity.getOtherSifRefId()).thenReturn(OTHER_SIF_ID);
        Mockito.when(mockSliEntity.hasOtherSifRefId()).thenReturn(true);
        Mockito.when(mockSliEntity.isCreatedByOthers()).thenReturn(false);
        Mockito.when(sifIdResolver.getSliEntityFromOtherSifId(OTHER_SIF_ID,
                SLI_TYPE, ZONE_ID)).thenReturn(null);

        Mockito.when(mockTranslationManager.translate(mockSifDataObject, ZONE_ID)).thenReturn(mockSliEntities);
        Mockito.when(mockSlcInterface.create(Mockito.any(GenericEntity.class))).thenReturn(SLI_ID);

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK create call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(mockGenericEntity);

        Mockito.verify(sifIdResolver, Mockito.times(1)).putSliGuid(SIF_ID, SLI_TYPE, SLI_ID, ZONE_ID);

        Mockito.verify(sifIdResolver, Mockito.times(1)).putSliGuidForOtherSifId(OTHER_SIF_ID, SLI_TYPE, SLI_ID, ZONE_ID);
    }

    @Test
    public void testChangeEntityOnEvent() throws ADKException {
        SIFDataObject mockSifDataObject = createMockSifDataObject();
        Event mockEvent = createMockSifEvent(true, mockSifDataObject);
        Zone mockZone = createMockZone();
        MessageInfo mockInfo = createMockInfo();


        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        Map<String, Object> mockBody = new HashMap<String, Object>();
        Mockito.when(mockSliEntity.createBody()).thenReturn(mockBody);
        Mockito.when(mockSliEntity.entityType()).thenReturn(SLI_TYPE);

        Mockito.when(mockTranslationManager.translate(mockSifDataObject, ZONE_ID)).thenReturn(mockSliEntities);

        GenericEntity mockEntity = new GenericEntity(SLI_TYPE, mockBody);
        List<Entity> mockEntityList = new ArrayList<Entity>();
        mockEntityList.add(mockEntity);
        Mockito.when(sifIdResolver.getSliEntityList(SIF_ID, ZONE_ID)).thenReturn(mockEntityList);

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK create call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(mockEntity);

        //change events should not add to the custom data maps
        Mockito.verify(sifIdResolver, Mockito.never()).putSliGuid(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
        Mockito.verify(sifIdResolver, Mockito.never()).putSliGuidForOtherSifId(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAddWithCommonCreatorCombine() throws ADKException {
        SIFDataObject mockSifDataObject = createMockSifDataObject();
        Event mockEvent = createMockSifEvent(false, mockSifDataObject);
        Zone mockZone = createMockZone();
        MessageInfo mockInfo = createMockInfo();

        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        GenericEntity mockGenericEntity = Mockito.mock(GenericEntity.class);
        Mockito.when(mockSliEntity.createGenericEntity()).thenReturn(mockGenericEntity);
        Mockito.when(mockSliEntity.entityType()).thenReturn(SLI_TYPE);
        Mockito.when(mockSliEntity.hasOtherSifRefId()).thenReturn(false);
        Mockito.when(mockSliEntity.isCreatedByOthers()).thenReturn(true);
        Mockito.when(mockSliEntity.getCreatorRefId()).thenReturn(CREATOR_SIF_ID);

        //mock the extraction of the combining entity
        Entity combiningEntity = Mockito.mock(Entity.class);
        Mockito.when(combiningEntity.getId()).thenReturn(SLI_ID);
        Mockito.when(combiningEntity.getEntityType()).thenReturn(SLI_TYPE);
        Mockito.when(sifIdResolver.getSliEntity(CREATOR_SIF_ID, ZONE_ID)).thenReturn(combiningEntity);


        Mockito.when(mockTranslationManager.translate(mockSifDataObject, ZONE_ID)).thenReturn(mockSliEntities);

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK update call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(combiningEntity);

        //verify sif custom data map updates
        Mockito.verify(sifIdResolver, Mockito.times(1)).putSliGuid(SIF_ID, SLI_TYPE, SLI_ID, ZONE_ID);
        Mockito.verify(sifIdResolver, Mockito.never()).putSliGuidForOtherSifId(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAddWithCommonOtherIdCombine() throws ADKException {
        SIFDataObject mockSifDataObject = createMockSifDataObject();
        Event mockEvent = createMockSifEvent(false, mockSifDataObject);
        Zone mockZone = createMockZone();
        MessageInfo mockInfo = createMockInfo();

        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        GenericEntity mockGenericEntity = Mockito.mock(GenericEntity.class);
        Mockito.when(mockSliEntity.createGenericEntity()).thenReturn(mockGenericEntity);
        Mockito.when(mockSliEntity.entityType()).thenReturn(SLI_TYPE);
        Mockito.when(mockSliEntity.hasOtherSifRefId()).thenReturn(true);
        Mockito.when(mockSliEntity.isCreatedByOthers()).thenReturn(false);
        Mockito.when(mockSliEntity.getOtherSifRefId()).thenReturn(OTHER_SIF_ID);

        //mock the extraction of the combining entity
        Entity combiningEntity = Mockito.mock(Entity.class);
        Mockito.when(combiningEntity.getId()).thenReturn(SLI_ID);
        Mockito.when(combiningEntity.getEntityType()).thenReturn(SLI_TYPE);
        Mockito.when(sifIdResolver.getSliEntityFromOtherSifId(OTHER_SIF_ID,
                SLI_TYPE, ZONE_ID)).thenReturn(combiningEntity);

        Mockito.when(mockTranslationManager.translate(mockSifDataObject, ZONE_ID)).thenReturn(mockSliEntities);

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK update call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(combiningEntity);

        //verify sif custom data map updates
        Mockito.verify(sifIdResolver, Mockito.times(1)).putSliGuid(SIF_ID, SLI_TYPE, SLI_ID, ZONE_ID);
        Mockito.verify(sifIdResolver, Mockito.never()).putSliGuidForOtherSifId(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString());
    }

    private Event createMockSifEvent(boolean change, SIFDataObject dataObject) throws ADKException {
        Event mockEvent = Mockito.mock(Event.class);
        DataObjectInputStream mockDataObjectInputStream = Mockito.mock(DataObjectInputStream.class);
        if (change) {
            Mockito.when(mockEvent.getActionString()).thenReturn("Change");
            Mockito.when(mockEvent.getAction()).thenReturn(EventAction.CHANGE);
        } else {
            Mockito.when(mockEvent.getActionString()).thenReturn("Add");
            Mockito.when(mockEvent.getAction()).thenReturn(EventAction.ADD);
        }
        Mockito.when(mockEvent.getData()).thenReturn(mockDataObjectInputStream);
        Mockito.when(mockDataObjectInputStream.readDataObject()).thenReturn(dataObject);
        return mockEvent;
    }

    private MessageInfo createMockInfo() throws ADKException {
        MessageInfo mockInfo = Mockito.mock(MessageInfo.class);
        Mockito.when(mockInfo.getMessage()).thenReturn("message");
        return mockInfo;
    }

    private Zone createMockZone() throws ADKException {
        Zone mockZone = Mockito.mock(Zone.class);
        Mockito.when(mockZone.getZoneId()).thenReturn(ZONE_ID);
        return mockZone;
    }

    private SIFDataObject createMockSifDataObject() {
        SchoolInfo mockSchoolInfo = Mockito.mock(SchoolInfo.class);
        Mockito.when(mockSchoolInfo.toString()).thenReturn(SIF_TYPE);
        Mockito.when(mockSchoolInfo.getRefId()).thenReturn(SIF_ID);
        return mockSchoolInfo;
    }

}
