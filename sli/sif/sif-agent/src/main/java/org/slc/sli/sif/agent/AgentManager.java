package org.slc.sli.sif.agent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

@Component
public class AgentManager {

    @PostConstruct
    public void setup(){
    }

    @PreDestroy
    public void cleanup(){
    }
}
