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

public class PublishZoneConfiguratorTest {

    @InjectMocks
    PublishZoneConfigurator publishZoneConfigurator;

    @Mock
    private Zone zone1;

    @Mock
    private Zone zone2;

    @Mock
    private Zone zone3;

    @Before
    public void setup() {
        publishZoneConfigurator = new PublishZoneConfigurator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConfigure() throws ADKException {
        Zone[] zones = {zone1, zone2, zone3};
        publishZoneConfigurator.configure(zones);
        Mockito.verify(zone1, Mockito.times(1)).connect(Mockito.anyInt());
        Mockito.verify(zone2, Mockito.times(1)).connect(Mockito.anyInt());
        Mockito.verify(zone3, Mockito.times(1)).connect(Mockito.anyInt());

        Assert.assertTrue(publishZoneConfigurator instanceof ZoneConfigurator);
    }

}
