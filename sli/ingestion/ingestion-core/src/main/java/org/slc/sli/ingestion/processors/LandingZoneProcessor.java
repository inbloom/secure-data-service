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

package org.slc.sli.ingestion.processors;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.tenant.TenantDA;

/**
 *
 * @author tshewchuk
 *
 */
@Component
public class LandingZoneProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(LandingZoneProcessor.class);

    @Autowired
    private TenantDA tenantDA;

    @Override
    public void process(Exchange exchange) throws Exception {
        // Verify that the landing zone is valid.
        File lzFile = exchange.getIn().getHeader("filePath", File.class);
        String lzDirectoryPathName = lzFile.getParent();
        boolean landingZoneIsValid = isLandingZoneValid(lzDirectoryPathName);
        if (!landingZoneIsValid) {
            LOG.error("LandingZoneProcessor: {} is not a valid landing zone.", lzDirectoryPathName);
        }

        exchange.getIn().setBody(lzFile, File.class);
        exchange.getIn().setHeader("hasErrors", !landingZoneIsValid);
}

    /**
     * Determine if the landing zone is valid.
     *
     * @param lzDirectoryPathName
     *        landing zone directory pathname.
     */
    private boolean isLandingZoneValid(String lzDirectoryPathName) {
        boolean result = false;
        try {
            String hostname = getHostname();
            result = tenantDA.getLzPaths(hostname).contains(lzDirectoryPathName);
        } catch (UnknownHostException e) {
            LOG.error("LandingZoneProcessor", e);
        }

        return result;
    }

    /**
     * Obtain the hostname for the ingestion server running.
     *
     * @throws UnknownHostException
     */
    private String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
