package org.slc.sli.ingestion.routes;

import static org.junit.Assert.*;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelSpringTestSupport;

//import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Fault;

public class IngestionRouteBuilderTest extends CamelSpringTestSupport {

        
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/applicationContext-test.xml");
        return context;
    }
    
    @Test
    public void testBatchJobWithFaultsIsNotProcessed() throws Exception {
       
        final MockEndpoint mock = getMockEndpoint("mock:job");
        RouteDefinition route = context.getRouteDefinition("jobDispatch");
        route.adviceWith(context, new RouteBuilder() {
            
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:acceptedJobs")
                .skipSendToOriginalEndpoint()
                .to(mock);
            }
            
        });

        // expect 0 BatchJobs to pass through the routes...
        mock.expectedMessageCount(0);

        // create a faulty job
        BatchJob job = BatchJob.createDefault();
        job.addFault(Fault.createError("I have an error"));

        // put it on the assembledJobs queue
        template.sendBody("seda:assembledJobs", job);

        // check it was NOT passed along to the acceptedJobs queue
        mock.assertIsSatisfied();
    }

    @Test
    public void testBatchJobWithoutFaultsIsProcessed() throws Exception {

        final MockEndpoint mock = getMockEndpoint("mock:job");
        RouteDefinition route = context.getRouteDefinition("jobDispatch");
        route.adviceWith(context, new RouteBuilder() {
            
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:acceptedJobs")
                .skipSendToOriginalEndpoint()
                .to(mock);
            }
            
        });

        // expect 1 BatchJob to pass through the routes...
        mock.expectedMessageCount(1);

        // create a faulty job
        BatchJob job = BatchJob.createDefault();
        job.addFault(Fault.createWarning("Just a warning"));

        // put it on the assembledJobs queue
        template.sendBody("seda:assembledJobs", job);

        // check the job was passed along to the acceptedJobs queue
        mock.assertIsSatisfied();
    }

}
