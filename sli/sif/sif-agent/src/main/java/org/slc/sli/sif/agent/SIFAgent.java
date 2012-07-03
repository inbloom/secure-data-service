package org.slc.sli.sif.agent;

import openadk.library.Agent;
import openadk.library.AgentMessagingMode;
import openadk.library.AgentProperties;
import openadk.library.Zone;

public class SIFAgent extends Agent {

    public SIFAgent(String agentId) {
        super(agentId);
    }

    public void initialize() throws Exception {
      super.initialize();
      // Set default properties
      AgentProperties props = getProperties();
      props.setMessagingMode( AgentMessagingMode.PUSH );
      // Set transport protocol properties
      // ...

      // Create the “district” zone and instruct it to use Push mode
      Zone dz = getZoneFactory().getInstance("DistrictZone", "http://localhost:1337/DistrictZone");
      AgentProperties zoneProps = dz.getProperties();
      zoneProps.setMessagingMode( AgentMessagingMode.PUSH );
      // Also change default security settings for this one zone
      zoneProps.setEncryptionLevel(3);
      zoneProps.setAuthenticationLevel(3);
    }
}
