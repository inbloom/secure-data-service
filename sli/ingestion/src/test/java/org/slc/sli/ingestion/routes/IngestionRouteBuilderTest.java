package org.slc.sli.ingestion.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.queues.IngestionQueueProperties;

/**
 *
 * @author jsa
 *
 */

public class IngestionRouteBuilderTest extends CamelSpringTestSupport {

    IngestionQueueProperties ctrlFilePreProcessorQueueProperties;
    
    /**
     * Set the assembledJobsQueueProperties from the applicationContext.
     * This is a workaround to avoid using SpringJUnit4ClassRunner which seemed to cause problems
     * when injecting a bean with autowire.
     */
    private void setCtrlFilePreProcessorQueueProperties() {
        ctrlFilePreProcessorQueueProperties = 
                (IngestionQueueProperties) applicationContext.getBean("assembledJobsQueueProperties");
    }
    
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/applicationContext-test.xml");
        return context;
    }

    @Test
    public void testBatchJobWithFaultsIsNotProcessed() throws Exception {
        setCtrlFilePreProcessorQueueProperties();

        // create a mock endpoint against which we can set expectations
        // it will be swapped in for the seda:acceptedJobs queue later
        final MockEndpoint mock = getMockEndpoint("mock:acceptedJobs");

        // look up a specific route by id and get a reference to it
        RouteDefinition route = context.getRouteDefinition("jobDispatch");

        // use adviceWith to override the route config, intercepting messages
        // to the acceptedJobs queue and instead diverting them to our mock
        // endpoint
        route.adviceWith(context, new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:acceptedJobs")
                .skipSendToOriginalEndpoint()
                .to(mock);
            }

        });

        // expect 0 messages (BatchJobs) to passed along to the acceptedJobs q
        mock.expectedMessageCount(0);

        // create a BatchJob and make it have an error
        BatchJob job = BatchJob.createDefault();
        job.getFaultsReport().error("I have an error", this);

        
        // put it on the assembledJobs queue
        template.sendBody(ctrlFilePreProcessorQueueProperties.getQueueUri(), job);

        // check it did NOT make it downstream to acceptedJobs q
        mock.assertIsSatisfied();
    }

    @Test
    public void testBatchJobWithoutFaultsIsProcessed() throws Exception {

        setCtrlFilePreProcessorQueueProperties();
        String assembledJobsUri = ctrlFilePreProcessorQueueProperties.getQueueUri();
        
        // TODO boilerplate code - looks simple enough to factor out, but
        // so far all attempts have resulted in obscure camel/spring config
        // problems.  needs further investigation.

        // create a mock endpoint against which we can set expectations
        // it will be swapped in for the seda:acceptedJobs queue later
        final MockEndpoint mock = getMockEndpoint("mock:acceptedJobs");

        // look up a specific route by id and get a reference to it
        RouteDefinition route = context.getRouteDefinition("jobDispatch");


        // use adviceWith to override the route config, intercepting messages
        // to the acceptedJobs queue and instead diverting them to our mock
        // endpoint
        route.adviceWith(context, new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("seda:acceptedJobs")
                .skipSendToOriginalEndpoint()
                .to(mock);
            }

        });

        // expect 1 message (BatchJob) to pass to the acceptedJobs q
        mock.expectedMessageCount(1);

        long startTime = System.currentTimeMillis();
        
        // create a BatchJob, give it a warning, not an error
        BatchJob job = BatchJob.createDefault();
        job.getFaultsReport().warning("Just a warning", this);

        // put it on the assembledJobs queue
        template.sendBody(assembledJobsUri, job);
      
        // check the job was passed along to the acceptedJobs queue
        mock.assertIsSatisfied();
        
        // report how long it took for the message to be received
        long endTime = System.currentTimeMillis();
        int ellapsedTime = (int) (endTime - startTime);
        System.out.println("testBatchJobWithoutFaultsIsProcessed: queue " + assembledJobsUri +
                " took " + ellapsedTime + " ms to send the batch job"); 
    }
}
