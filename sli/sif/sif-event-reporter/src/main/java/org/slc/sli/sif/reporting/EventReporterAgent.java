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

package org.slc.sli.sif.reporting;

import java.util.Properties;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.SIFVersion;
import openadk.library.Zone;

import org.slc.sli.sif.zone.ZoneConfigurator;

/**
 * SIF agent
 *
 */
public class EventReporterAgent extends Agent {

    private String configFilePath;
    private ZoneConfigurator zoneConfigurator;
    private Properties agentProperties;
    private Properties httpProperties;
    private Properties httpsProperties;
    private SIFVersion sifVersion;
    private String zoneId;
    private String agentId;
    private String zoneUrl;

    public EventReporterAgent() {
        this("EventReporterAgent");
    }

    public EventReporterAgent(String id) {
        super(id);
        setName(id);
    }

    public EventReporterAgent(String id, ZoneConfigurator zoneConfig, Properties agentProperties,
            Properties httpProperties, Properties httpsProperties, String zoneId,
            String zoneUrl, SIFVersion sifVersion) {
        super(id);
        this.agentId = id;
        this.zoneConfigurator = zoneConfig;
        this.agentProperties = agentProperties;
        this.httpProperties = httpProperties;
        this.httpsProperties = httpsProperties;
        this.zoneId = zoneId;
        this.zoneUrl = zoneUrl;
        this.sifVersion = sifVersion;
        setName(id);
    }

    public void startAgent() throws Exception {
        // initialize once the configuration file has been read
        super.initialize();

        setProperties();

        // Connect to each zone specified in the configuration file, registering
        // this agent as the Provider of the SIS objects.
        Zone[] allZones = getZoneFactory().getAllZones();
        zoneConfigurator.configure(allZones);
    }

    private void setProperties() throws ADKException {
        ADK.setVersion(SIFVersion.SIF23);

        //set the agentId
        setId(agentId);

        //apply properties
        getProperties().putAll(agentProperties);
        getDefaultHttpProperties().putAll(httpProperties);
        getDefaultHttpsProperties().putAll(httpsProperties);

        //add zone
        this.getZoneFactory().getInstance(zoneId, zoneUrl);
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public String getConfigFilePath() {
        return this.configFilePath;
    }

    public void setZoneConfigurator(ZoneConfigurator zoneConfigurator) {
        this.zoneConfigurator = zoneConfigurator;
    }

    public ZoneConfigurator getZoneConfigurator() {
        return this.zoneConfigurator;
    }

    public void setAgentProperties(Properties agentProperties) {
        this.agentProperties = agentProperties;
    }

    public Properties getAgentProperties() {
        return agentProperties;
    }

    public void setHttpProperties(Properties httpProperties) {
        this.httpProperties = httpProperties;
    }

    public Properties getHttpProperties() {
        return httpProperties;
    }

    public void setHttpsProperties(Properties httpsProperties) {
        this.httpsProperties = httpsProperties;
    }

    public Properties getHttpsProperties() {
        return httpsProperties;
    }

    public void setSifVersion(SIFVersion sifVersion) {
        this.sifVersion = sifVersion;
    }

    public SIFVersion getSifVersion() {
        return sifVersion;
    }
}
