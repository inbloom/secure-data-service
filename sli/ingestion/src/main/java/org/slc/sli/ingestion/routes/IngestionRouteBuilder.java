package org.slc.sli.ingestion.routes;

import static org.apache.camel.builder.PredicateBuilder.or;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import javax.jms.ConnectionFactory;

import ch.qos.logback.classic.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.spring.SpringRouteBuilder;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ControlFileProcessor;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.queues.IngestionQueueProperties;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Ingestion route builder.
 *
 * @author okrook
 *
 */
@Component
public class IngestionRouteBuilder extends SpringRouteBuilder {

    @Autowired
    EdFiProcessor edFiProcessor;

    @Autowired
    ControlFileProcessor ctlFileProcessor;

    @Autowired
    ZipFileProcessor zipFileProcessor;

    @Autowired(required = true)
    PersistenceProcessor persistenceProcessor;

    @Autowired
    LocalFileSystemLandingZone lz;

    @Autowired
    LocalFileSystemLandingZone tempLz;

    @Autowired
    IngestionQueueProperties workItemQueueProperties;
    
    ConnectionFactory connectionFactory;
    
    private void addJmsHornetQCamelComponent() {

        String queueHostName = workItemQueueProperties.getHostName();
        int queuePort = workItemQueueProperties.getPort();
        
        //TODO Connect with credentials.

        //transport properties - set the server
        HashMap<String, Object> tcMap = new HashMap<String, Object>();
        tcMap.put(TransportConstants.HOST_PROP_NAME, queueHostName);
        tcMap.put(TransportConstants.PORT_PROP_NAME, queuePort);
        
        //TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), tcMap);
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName());
        //create a hornetQ connection factory
        
        
        connectionFactory = (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
       
        //Add jms connection factory component to camel context
        CamelContext context = this.getContext();
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
    }
    
    @Override
    public void configure() throws Exception {
        
        if (!workItemQueueProperties.isUsingSedaQueues()) addJmsHornetQCamelComponent();
        
        String workItemQueue = workItemQueueProperties.getQueueUri();
        
        String inboundDir = lz.getDirectory().getPath();

        // routeId: ctlFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.ctl$"
                        + "&move=" + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                        + "&moveFailed=" + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}")
                .routeId("ctlFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                .process(new ControlFilePreProcessor(lz))
                .to(workItemQueue);

        // routeId: ctlFilePreprocessor
        from(workItemQueue)
                .routeId("ctlFilePreprocessor")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing control file.")
                .choice()
                    .when(header("IngestionMessageType").isEqualTo(MessageType.BATCH_REQUEST.name()))
                    .process(ctlFileProcessor)
                    .to("seda:assembledJobs")
                .otherwise()
                    .to("direct:stop");
        
        // routeId: zipFilePoller
        from(
                "file:" + inboundDir + "?include=^(.*)\\.zip$&preMove="
                        + inboundDir + "/.done&moveFailed=" + inboundDir
                        + "/.error")
                .routeId("zipFilePoller")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing zip file.")
                .process(zipFileProcessor)
                .choice()
                    .when(body().isInstanceOf(BatchJob.class))
                    .to("seda:assembledJobs")
                .otherwise()
                    .process(new Processor() {

                        // set temporary path to where the files were unzipped
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            File ctlFile = exchange.getIn().getBody(File.class);
                            tempLz.setDirectory(ctlFile.getParentFile());
                        }
                    }).process(new ControlFilePreProcessor(tempLz))
                    .to(workItemQueue);
        
        // routeId: jobDispatch  
        from("seda:assembledJobs")
                .routeId("jobDispatch")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Dispathing jobs for file.")
                .choice()
                    .when(header("hasErrors").isEqualTo(true))
                    .to("direct:stop")
                .otherwise()
                    .to("seda:acceptedJobs");
        
        // routeId: jobReporting
        from("direct:jobReporting")
                .routeId("jobReporting")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Reporting on jobs for file.")
                .process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {

                        // get job from exchange
                        BatchJob job = exchange.getIn().getBody(BatchJob.class);

                        Logger jobLogger = BatchJobLogger.createLoggerForJob(job, lz);

                        // add output as lines
                        jobLogger.info("jobId: " + job.getId());
                        
                        for (IngestionFileEntry fileEntry : job.getFiles()) {
                            jobLogger.info("[file] " + fileEntry.getFileName()
                                    + " (" + fileEntry.getFileFormat() + "/"
                                    + fileEntry.getFileType() + ")");
                        }

                        Enumeration names = job.propertyNames();
                        while (names.hasMoreElements()) {
                            String key = (String) names.nextElement();
                            jobLogger.info("[configProperty] " + key + ": "
                                    + job.getProperty(key));
                        }

                        FaultsReport fr = job.getFaultsReport();

                        for (Fault fault: fr.getFaults()) {
                            if (fault.isError()) {
                                jobLogger.error(fault.getMessage());
                            } else {
                                jobLogger.warn(fault.getMessage());
                            }
                        }

                        if (fr.hasErrors()) {
                            jobLogger.info("Job rejected due to errors");
                        } else {
                            jobLogger.info("Job ready for processing");
                        }

                        jobLogger.info("Ingested " + exchange.getProperty("records.processed") + " records into datastore.");
                        
                        // clean up after ourselves
                        jobLogger.detachAndStopAllAppenders();
                    }

                });

        // routeId: jobPipeline
        from("seda:acceptedJobs")
                .routeId("jobPipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Job Pipeline for file.")
                .process(edFiProcessor)
                .to("seda:persist");

        // routeId: persistencePipeline
        from("seda:persist")
                .routeId("persistencePipeline")
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Persisiting data for file.")
                .log("persist: jobId: " + header("jobId").toString())
                .choice()
                    .when(or(header("dry-run").isEqualTo(true), header("hasErrors").isEqualTo(true)))
                        .log("job has errors or dry-run specified; data will not be published")
                        .to("direct:stop")
                    .otherwise()
                        .log("publishing data now!")
                        .process(persistenceProcessor)
                        .to("direct:stop");

        // end of routing
        from("direct:stop")
                .routeId("stop")
                .wireTap("direct:jobReporting")
                .log("end of job: " + header("jobId").toString())
                .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - File processed.")
                .stop();
   
    }

}
