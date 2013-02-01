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

import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.Zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for zone publishers
 */
public class PublishZoneConfigurator implements ZoneConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(PublishZoneConfigurator.class);

    @Override
    public void configure(Zone[] allZones) {
        for (Zone zone : allZones) {
            try {
                // Connect to this zone
                LOG.info("- Connecting to zone \"" + zone.getZoneId() + "\" at " + zone.getZoneUrl());

                zone.connect(ADKFlags.PROV_REGISTER | ADKFlags.PROV_PROVIDE);
            } catch (ADKException ex) {
                LOG.error("  " + ex.getMessage(), ex);
            }
        }

    }

}
