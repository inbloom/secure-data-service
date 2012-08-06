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
        Event mockEvent = Mockito.mock(Event.class);
        Zone mockZone = Mockito.mock(Zone.class);
        MessageInfo mockInfo = Mockito.mock(MessageInfo.class);
        DataObjectInputStream mockDataObjectInputStream = Mockito.mock(DataObjectInputStream.class);
        SIFDataObject mockSIFDataObject = Mockito.mock(SIFDataObject.class);

        Mockito.when(mockEvent.getActionString()).thenReturn("event");
        Mockito.when(mockZone.getZoneId()).thenReturn("zoneId");
        Mockito.when(mockInfo.getMessage()).thenReturn("message");
        Mockito.when(mockEvent.getData()).thenReturn(mockDataObjectInputStream);
        Mockito.when(mockDataObjectInputStream.readDataObject()).thenReturn(mockSIFDataObject);
        Mockito.when(mockSIFDataObject.toString()).thenReturn("dataObject");

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK sessionCheck call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).sessionCheck();

    }

    @Test
    public void testAddEntityOnEvent() throws ADKException {
        Event mockEvent = Mockito.mock(Event.class);
        Zone mockZone = Mockito.mock(Zone.class);
        MessageInfo mockInfo = Mockito.mock(MessageInfo.class);
        DataObjectInputStream mockDataObjectInputStream = Mockito.mock(DataObjectInputStream.class);
        SchoolInfo mockSchoolInfo = Mockito.mock(SchoolInfo.class);

        Mockito.when(mockEvent.getActionString()).thenReturn("Add");
        Mockito.when(mockEvent.getAction()).thenReturn(EventAction.ADD);
        Mockito.when(mockZone.getZoneId()).thenReturn("zoneId");
        Mockito.when(mockInfo.getMessage()).thenReturn("message");
        Mockito.when(mockEvent.getData()).thenReturn(mockDataObjectInputStream);
        Mockito.when(mockDataObjectInputStream.readDataObject()).thenReturn(mockSchoolInfo);
        Mockito.when(mockSchoolInfo.toString()).thenReturn("SchoolInfo");

        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        GenericEntity mockGenericEntity = Mockito.mock(GenericEntity.class);
        Mockito.when(mockSliEntity.getGenericEntity()).thenReturn(mockGenericEntity);

        Mockito.when(mockTranslationManager.translate(mockSchoolInfo)).thenReturn(mockSliEntities);
        Mockito.when(mockSchoolInfo.getRefId()).thenReturn("1234");
        Mockito.when(mockSlcInterface.create(Mockito.any(GenericEntity.class))).thenReturn("5678");

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK create call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(Mockito.any(GenericEntity.class));
    }

    @Test
    public void testChangeEntityOnEvent() throws ADKException {
        Event mockEvent = Mockito.mock(Event.class);
        Zone mockZone = Mockito.mock(Zone.class);
        MessageInfo mockInfo = Mockito.mock(MessageInfo.class);
        DataObjectInputStream mockDataObjectInputStream = Mockito.mock(DataObjectInputStream.class);
        SchoolInfo mockSchoolInfo = Mockito.mock(SchoolInfo.class);

        Mockito.when(mockEvent.getActionString()).thenReturn("Change");
        Mockito.when(mockEvent.getAction()).thenReturn(EventAction.CHANGE);
        Mockito.when(mockZone.getZoneId()).thenReturn("zoneId");
        Mockito.when(mockInfo.getMessage()).thenReturn("message");
        Mockito.when(mockEvent.getData()).thenReturn(mockDataObjectInputStream);
        Mockito.when(mockDataObjectInputStream.readDataObject()).thenReturn(mockSchoolInfo);
        Mockito.when(mockSchoolInfo.toString()).thenReturn("SchoolInfo");

        SliEntity mockSliEntity = Mockito.mock(SliEntity.class);
        List<SliEntity> mockSliEntities = new ArrayList<SliEntity>();
        mockSliEntities.add(mockSliEntity);
        Map<String, Object> mockBody = new HashMap<String, Object>();
        Mockito.when(mockSliEntity.body()).thenReturn(mockBody);

        Mockito.when(mockTranslationManager.translate(mockSchoolInfo)).thenReturn(mockSliEntities);
        Mockito.when(mockSchoolInfo.getRefId()).thenReturn("1234");
        GenericEntity mockEntity = new GenericEntity(Mockito.any(String.class), mockBody);
        Mockito.when(sifIdResolver.getSliEntity("1234")).thenReturn(mockEntity);

        subscriber.onEvent(mockEvent, mockZone, mockInfo);

        //verify that the SDK create call is made
        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(mockEntity);
    }

}