package org.slc.sli.sif.agent;

import java.io.File;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.Agent;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.student.StudentDTD;
import openadk.library.tools.cfg.AgentConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.subscriber.SifSubscriber;

@Component
public class SifAgent extends Agent {

    AgentConfig fCfg;

    @Value("classpath:/sif/agent-config.xml")
    Resource configFile;

    public SifAgent() {
        super("SifAgent");
        setName("SIF Agent");
    }

    public void startAgent() throws Exception {

        File file = configFile.getFile();
        // Read the configuration file
        fCfg = new AgentConfig();
        fCfg.read(file.getAbsolutePath(), false);

        // Override the SourceId passed to the constructor with the SourceId
        // specified in the configuration file
        setId(fCfg.getSourceId());

        // Inform the ADK of the version of SIF specified in the sifVersion=
        // attribute of the <agent> element
        SIFVersion version = fCfg.getVersion();
        ADK.setVersion(version);

        // initialize once the configuration file has been read
        super.initialize();

        fCfg.apply(this, true);

        // Connect to each zone specified in the configuration file, registering
        // this agent as the Provider of the SIS objects.

        Zone[] allZones = getZoneFactory().getAllZones();
        for (Zone zone : allZones) {
            try {
                // Connect to this zone

                System.out.println("- Connecting to zone \"" + zone.getZoneId() + "\" at " + zone.getZoneUrl());

                zone.setSubscriber(new SifSubscriber(), StudentDTD.SCHOOLINFO);
                zone.connect(ADKFlags.PROV_REGISTER);

            } catch (ADKException ex) {
                System.out.println("  " + ex.getMessage());
            }
        }
    }

}
