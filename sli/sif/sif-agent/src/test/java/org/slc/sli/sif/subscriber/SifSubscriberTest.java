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
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.MessageInfo;
import openadk.library.Zone;
import openadk.library.student.SchoolInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.slcinterface.SifIdResolver;
import org.slc.sli.sif.slcinterface.SlcInterface;
import org.slc.sli.sif.translation.SifTranslationManager;

/**
 * JUnit tests for SifSubscriber
 *
 */
public class SifSubscriberTest extends AdkTest {

    @InjectMocks
    private SifSubscriber subscriber = new SifSubscriber();

    @Mock
    private SifTranslationManager translationManager;

    @Mock
    private SlcInterface mockSlcInterface;

    @Mock
    private SifIdResolver mockSifIdResolver;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldTranslateEventEntities() throws ADKException {

        SchoolInfo sifData = new SchoolInfo();
        Event event = new Event(sifData, EventAction.ADD);
        MessageInfo info = Mockito.mock(MessageInfo.class);
        Zone zone = Mockito.mock(Zone.class);
        Mockito.when(zone.getZoneId()).thenReturn("zoneId");

        List<GenericEntity> translatedEntities = new ArrayList<GenericEntity>();

        Mockito.when(translationManager.translate(sifData, "zoneId")).thenReturn(translatedEntities);

        subscriber.onEvent(event, zone, info);

        Mockito.verify(translationManager, Mockito.times(1)).translate(sifData, "zoneId");
    }

    @Test
    public void shouldCreateTranslatedAddEvents() throws ADKException {

        SchoolInfo sifData = new SchoolInfo();
        Event event = new Event(sifData, EventAction.ADD);
        String apiGuid = "SomeGuid";

        Zone zone = Mockito.mock(Zone.class);
        Mockito.when(zone.getZoneId()).thenReturn("zoneId");
        MessageInfo info = Mockito.mock(MessageInfo.class);
        Mockito.when(mockSlcInterface.create(Matchers.any(GenericEntity.class))).thenReturn(apiGuid);

        List<GenericEntity> translatedEntities = new ArrayList<GenericEntity>();
        translatedEntities.add(new GenericEntity("someType1", new HashMap<String, Object>()));
        translatedEntities.add(new GenericEntity("someType2", new HashMap<String, Object>()));

        Mockito.when(translationManager.translate(sifData, "zoneId")).thenReturn(translatedEntities);

        subscriber.onEvent(event, zone, info);

        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(translatedEntities.get(0));
        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(translatedEntities.get(1));
    }

    @Test
    public void shouldTrackSliIds() throws ADKException {

        SchoolInfo sifData = new SchoolInfo();
        sifData.setRefId("REF_ID");
        Event event = new Event(sifData, EventAction.ADD);

        Zone zone = Mockito.mock(Zone.class);
        Mockito.when(zone.getZoneId()).thenReturn("zoneId");
        MessageInfo info = Mockito.mock(MessageInfo.class);

        List<GenericEntity> translatedEntities = new ArrayList<GenericEntity>();
        translatedEntities.add(new GenericEntity("someType1", new HashMap<String, Object>()));
        translatedEntities.add(new GenericEntity("someType2", new HashMap<String, Object>()));

        Mockito.when(mockSlcInterface.create(translatedEntities.get(0))).thenReturn("SomeGuid");

        Mockito.when(translationManager.translate(sifData, "zoneId")).thenReturn(translatedEntities);

        subscriber.onEvent(event, zone, info);

        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(translatedEntities.get(0));
        Mockito.verify(mockSlcInterface, Mockito.times(1)).create(translatedEntities.get(1));

        Mockito.verify(mockSifIdResolver, Mockito.times(1)).putSliGuid("REF_ID", "someType1", "SomeGuid", "zoneId");
        // only track id's when guid is returned from api
        Mockito.verify(mockSifIdResolver, Mockito.times(0)).putSliGuid("REF_ID", "someType2", "SomeGuid", "zoneId");
    }

    @Test
    public void shouldDispatchFirstUpdateEvent() throws ADKException {
        SchoolInfo sifData = new SchoolInfo();
        Event event = new Event(sifData, EventAction.CHANGE);

        Zone zone = Mockito.mock(Zone.class);
        Mockito.when(zone.getZoneId()).thenReturn("zoneId");
        MessageInfo info = Mockito.mock(MessageInfo.class);

        List<GenericEntity> translatedEntities = new ArrayList<GenericEntity>();
        translatedEntities.add(new GenericEntity("someType1", new HashMap<String, Object>()));
        translatedEntities.add(new GenericEntity("someType2", new HashMap<String, Object>()));

        Mockito.when(translationManager.translate(sifData, "zoneId")).thenReturn(translatedEntities);

        subscriber.onEvent(event, zone, info);

        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(translatedEntities.get(0));
        Mockito.verify(mockSlcInterface, Mockito.times(1)).update(Mockito.any(GenericEntity.class));

    }

    @Test
    public void shouldMergeUpdateWithSliEntity() throws ADKException {

        SchoolInfo sifData = new SchoolInfo();
        sifData.setRefId("REF_ID");
        Event event = new Event(sifData, EventAction.CHANGE);

        Zone zone = Mockito.mock(Zone.class);
        Mockito.when(zone.getZoneId()).thenReturn("zoneId");
        MessageInfo info = Mockito.mock(MessageInfo.class);

        // build 2 maps, one with old data, one with new
        Map<String, Object> oldData = new HashMap<String, Object>();
        oldData.put("1", "a");
        oldData.put("2", "b");
        Map<String, Object> subMap = new HashMap<String, Object>();
        oldData.put("3", subMap);
        subMap.put("7", "g");
        subMap.put("8", "h");

        Map<String, Object> newData = new HashMap<String, Object>();
        oldData.put("2", "B");
        oldData.put("4", "D");
        Map<String, Object> newSubMap = new HashMap<String, Object>();
        newData.put("3", newSubMap);
        newSubMap.put("8", "H");
        newSubMap.put("9", "I");

        Entity oldSliEntity = new GenericEntity("type", oldData);

        Mockito.when(mockSifIdResolver.getSliEntity("REF_ID", "zoneId")).thenReturn(oldSliEntity);

        List<GenericEntity> translatedEntities = new ArrayList<GenericEntity>();
        translatedEntities.add(new GenericEntity("type", newData));

        Mockito.when(translationManager.translate(sifData, "zoneId")).thenReturn(translatedEntities);

        subscriber.onEvent(event, zone, info);

        Mockito.verify(mockSlcInterface).update(oldSliEntity);

        // Verify expected structure of final map, including nested maps
        Map<String, Object> finalMap = oldSliEntity.getData();
        Assert.assertEquals(finalMap.get("1"), "a");
        Assert.assertEquals(finalMap.get("2"), "B");
        Assert.assertEquals(finalMap.get("4"), "D");
        Assert.assertTrue(finalMap.get("3") instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> finalSubMap = (Map<String, Object>) finalMap.get("3");
        Assert.assertEquals(finalSubMap.get("7"), "g");
        Assert.assertEquals(finalSubMap.get("8"), "H");
        Assert.assertEquals(finalSubMap.get("9"), "I");

    }

}