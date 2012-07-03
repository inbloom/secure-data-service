package org.slc.sli.sif.agent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentManager {

    @Autowired
    SifAgent agent;

    @PostConstruct
    public void setup() throws Exception {

        ADK.initialize();
        ADK.debug = ADK.DBG_ALL;

        agent.startAgent();
    }

    @PreDestroy
    public void cleanup() throws ADKException {
        agent.shutdown( ADKFlags.PROV_NONE );
    }
}
