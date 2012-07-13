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

import junit.framework.Assert;
import openadk.library.ADKException;
import openadk.library.DataObjectInputStream;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.SIFDataObject;
import openadk.library.Subscriber;
import openadk.library.Zone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.sif.slcinterface.SlcInterface;

/**
 * JUnit tests for SifSubscriber
 *
 * @author jtully
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/applicationContext.xml" })
public class SifSubscriberTest {

    @InjectMocks
    SifSubscriber subscriber;

    @Mock
    private SlcInterface mockSlcInterface;

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
}