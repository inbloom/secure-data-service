package org.slc.sli.sif.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.Agent;
import openadk.library.AgentMessagingMode;
import openadk.library.AgentProperties;
import openadk.library.Zone;

public class SIFAgent extends Agent {

    // TODO move this configuration to spring
    private static Map<String, String> zoneIdentifierMap = new HashMap<String, String>();
    static {
        zoneIdentifierMap.put("DistrictZone", "http://localhost:1337/DistrictZone");
        zoneIdentifierMap.put("SchoolZone", "http://localhost:1337/SchoolZone");
    }

    public SIFAgent() {
        this("default");
    }

    public SIFAgent(String agentId) {
        super(agentId);
    }

    public void initialize() throws Exception {
        super.initialize();
        // Set default properties
        AgentProperties props = getProperties();
        props.setMessagingMode( AgentMessagingMode.PUSH );

        // Set transport protocol properties
        // TODO

        for (Entry<String, String> entry : zoneIdentifierMap.entrySet()) {
            String id = entry.getKey();
            String url = entry.getValue();
            addZone(id, url);
        }
    }

    public Zone addZone(String id, String url) throws ADKException {
        Zone zone = getZoneFactory().getInstance(id, url);

        AgentProperties zoneProps = zone.getProperties();
        zoneProps.setMessagingMode(AgentMessagingMode.PUSH);
        zoneProps.setEncryptionLevel(3);
        zoneProps.setAuthenticationLevel(3);

        zone.connect(ADKFlags.PROV_REGISTER);

        return zone;
    }
}
