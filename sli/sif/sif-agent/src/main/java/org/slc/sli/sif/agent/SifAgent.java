package org.slc.sli.sif.agent;

import openadk.library.Agent;

import org.springframework.stereotype.Component;

@Component
public class SifAgent extends Agent {

    public SifAgent() {
        super("SifAgent");
        setName("SIF Agent");
    }

}
