package org.slc.sli.ingestion;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.yammer.metrics.HealthChecks;
import com.yammer.metrics.core.HealthCheck;

@Component
public class IngestionHealthCheck extends HealthCheck {

    public IngestionHealthCheck() {
        super("Ingestion");
    }

    @Override
    public Result check() throws Exception {
        return Result.healthy();
        //UN: Technically, we can have it check various attributes here.
        //		like MongoDB connection, Files in process in Camel Routing, etc.
        //		For now, we will just return healthy if the service is
        //		running/responsive.
//        if (true) {
//            System.out.println("checked");
//            return Result.healthy();
//        } else {
//            return Result.unhealthy("Cannot connect to " );
//        }
    }

    @PostConstruct
    public void init()
    {
        HealthChecks.register(this);
    }
}