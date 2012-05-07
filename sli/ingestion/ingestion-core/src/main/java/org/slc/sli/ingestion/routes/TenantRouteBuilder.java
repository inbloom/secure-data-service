package org.slc.sli.ingestion.routes;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.processors.ControlFilePreProcessor;
import org.slc.sli.ingestion.processors.ZipFileProcessor;
import org.slc.sli.ingestion.tenant.LandingZoneRecord;
import org.slc.sli.ingestion.tenant.TenantRecord;

/**
 * Route Builder for dynamically building landing zones based 
 * on a TenantRecord
 * 
 * 
 * @author jtully
 *
 */
public class TenantRouteBuilder extends RouteBuilder {

    ZipFileProcessor zipFileProcessor;

    ControlFilePreProcessor controlFilePreProcessor;
    
    List<LandingZoneRecord> landingZones;
    
    String workItemQueueUri;
    
    public TenantRouteBuilder(TenantRecord tenant, String workItemQueueUri, 
            ZipFileProcessor zipFileProcessor, ControlFilePreProcessor controlFilePreProcessor) {
        
        landingZones = tenant.getLandingZone();
        this.zipFileProcessor = zipFileProcessor;
        this.controlFilePreProcessor = controlFilePreProcessor;
        this.workItemQueueUri = workItemQueueUri;
    }
    
    @Override
    public void configure() throws Exception {
        //TODO if possible, refactor the code here that is common with ingestionRouteBuilder
        
        for (LandingZoneRecord lz : landingZones) {
            String inboundDir = lz.getPath();
            log.info("Configuring route for landing zone: {} ", inboundDir);
            // routeId: ctlFilePoller
            from(
                    "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.CONTROL_FILE.getExtension() + "$"
                            + "&move=" + inboundDir + "/.done/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                            + "&moveFailed=" + inboundDir + "/.error/${file:onlyname}.${date:now:yyyyMMddHHmmssSSS}"
                            + "&readLock=changed")
                    .routeId("ctlFilePoller-" + inboundDir)
                    .log(LoggingLevel.INFO, "Job.PerformanceMonitor", "- ${id} - ${file:name} - Processing file.")
                    .process(controlFilePreProcessor)
                    .to(workItemQueueUri);

            // routeId: zipFilePoller
            from(
                    "file:" + inboundDir + "?include=^(.*)\\." + FileFormat.ZIP_FILE.getExtension() + "$&preMove="
                            + inboundDir + "/.done&moveFailed=" + inboundDir
                            + "/.error"
                            + "&readLock=changed")
                    .routeId("zipFilePoller-" + inboundDir)
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
