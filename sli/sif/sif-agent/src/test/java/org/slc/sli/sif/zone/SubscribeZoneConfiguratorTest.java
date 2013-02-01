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

package org.slc.sli.sif.zone;

import junit.framework.Assert;
import openadk.library.ADKException;
import openadk.library.Zone;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for PublishZoneConfigurator
 *
 * @author vmcglaughlin
 *
 */
public class SubscribeZoneConfiguratorTest {

    @InjectMocks
    SubscribeZoneConfigurator subscribeZoneConfigurator;

    @Mock
    private Zone zone1;

    @Mock
    private Zone zone2;

    @Mock
    private Zone zone3;

    @Before
    public void setup() {
        subscribeZoneConfigurator = new SubscribeZoneConfigurator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigure() throws ADKException {
        Zone[] zones = {zone1, zone2, zone3};
        subscribeZoneConfigurator.configure(zones);
        Mockito.verify(zone1, Mockito.times(1)).connect(Mockito.anyInt());
        Mockito.verify(zone2, Mockito.times(1)).connect(Mockito.anyInt());
        Mockito.verify(zone3, Mockito.times(1)).connect(Mockito.anyInt());

        Assert.assertTrue(subscribeZoneConfigurator instanceof ZoneConfigurator);
    }

}
