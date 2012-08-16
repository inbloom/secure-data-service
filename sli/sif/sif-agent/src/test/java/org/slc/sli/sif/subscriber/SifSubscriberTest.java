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

import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.sif.AdkTest;
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

}