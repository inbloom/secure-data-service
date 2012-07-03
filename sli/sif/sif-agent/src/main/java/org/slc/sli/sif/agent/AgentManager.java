package org.slc.sli.sif.agent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AgentManager {

    @Autowired
    SifAgent agent;

    @PostConstruct
    public void setup(){
    }

    @PreDestroy
    public void cleanup(){
    }
}
