package org.slc.sli.sif.zone;

import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.SubscriptionOptions;
import openadk.library.Zone;
import openadk.library.student.StudentDTD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.sif.subscriber.SifSubscriber;

public class SubscribeZoneConfigurator implements ZoneConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeZoneConfigurator.class);

    @Override
    public void configure(Zone[] allZones) {
        for (Zone zone : allZones) {
            try {
                // Connect to this zone
                LOG.info("- Connecting to zone \"" + zone.getZoneId() + "\" at " + zone.getZoneUrl());

                zone.setSubscriber(new SifSubscriber(), StudentDTD.SCHOOLINFO, new SubscriptionOptions());
                zone.setSubscriber(new SifSubscriber(), StudentDTD.STUDENTPERSONAL, new SubscriptionOptions());

                zone.connect(ADKFlags.PROV_REGISTER | ADKFlags.PROV_SUBSCRIBE);
            } catch (ADKException ex) {
                LOG.error("  " + ex.getMessage(), ex);
            }
        }

    }

}
