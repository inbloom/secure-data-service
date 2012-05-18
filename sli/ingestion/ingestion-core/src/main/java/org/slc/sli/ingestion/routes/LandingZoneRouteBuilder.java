package org.slc.sli.ingestion.routes;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;

/**
 * RouteBuilder class to create file polling routes 
 * for a list of landing zone paths.
 * 
 * This class must be instantiated every time file polling routes need to be added.
 * 
 * @author jtully
 *
 */
public class LandingZoneRouteBuilder extends RouteBuilder {
    
    //TODO is it possible to inject these?
    private ZipFileProcessor zipFileProcessor;
    private ControlFilePreProcessor controlFilePreProcessor;
    
    private List<String> landingZonePaths;
    
    public static final String CTRL_POLLER_PREFIX = "ctlFilePoller-";
    public static final String ZIP_POLLER_PREFIX = "zipFilePoller-";
    
    private String workItemQueueUri;
    
    /**
     * @param landingZonePaths, the landing zone directories to poll
     * @param workItemQueueUri, the URI for the main ingestion queue
     * @param zipFileProcessor, the ingestion zip processor 
     * @param controlFilePreProcessor, the ingestion controlFilePreProcessor
     */
    public LandingZoneRouteBuilder(List<String> landingZonePaths, String workItemQueueUri, 
            ZipFileProcessor zipFileProcessor, ControlFilePreProcessor controlFilePreProcessor) {
        
        this.landingZonePaths = landingZonePaths;
        this.zipFileProcessor = zipFileProcessor;
        this.controlFilePreProcessor = controlFilePreProcessor;
        this.workItemQueueUri = workItemQueueUri;
    }
    
    @Override
    public void configure() throws Exception {
        for (String inboundDir : landingZonePaths) {
            log.info("Configuring route for landing zone: {} ", inboundDir);
            // routeId: ctlFilePoller-inboundDir
            from(
                    "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.CONTROL_FILE.getExtension() + "$"
                            + "&move=" + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                            + "&moveFailed=" + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                            + "&readLock=changed")
                    .routeId(CTRL_POLLER_PREFIX + inboundDir)
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                    .process(controlFilePreProcessor)
                    .to(workItemQueueUri);

            // routeId: zipFilePoller-inboundDir
            from(
                    "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.ZIP_FILE.getExtension() + "$&preMove="
                            + inboundDir + "/.done&moveFailed=" + inboundDir
                            + "/.error"
                            + "&readLock=changed")
                    .routeId(ZIP_POLLER_PREFIX + inboundDir)
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing zip file.")
                    .process(zipFileProcessor)
                    .choice()
                    .when(header("hasErrors").isEqualTo(true))
                        .to("direct:stop")
                    .otherwise()
                        .process(controlFilePreProcessor)
                        .to(workItemQueueUri);
        }
    }
}
