package org.slc.sli.ingestion;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.BrowsableEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.FileUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.ResourceUtils;

//import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;

/**
 * 
 * @author jsa
 *
 */
public class E2eZipUploadSuccessTest extends CamelSpringTestSupport {

    Logger log = LoggerFactory.getLogger(E2eZipUploadSuccessTest.class);
            
    @Autowired
    LocalFileSystemLandingZone lz;
        
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/applicationContext-test.xml");
        
        // Not sure why Autowired isn't doing the trick
        lz = (LocalFileSystemLandingZone) context.getBean("lz");
        
        return context;
    }
    
    @Test
    public void testZipUploadSuccess() throws Exception {

        // look up a specific route by id and get a reference to it
        RouteDefinition route = context.getRouteDefinition("jobDispatch");
        
        // use adviceWith to override the route config, intercepting all
        // the assembledJobs and copying them into an intercepted queue, 
        // where we can investigate them
        route.adviceWith(context, new RouteBuilder() {
            
            @Override
            public void configure() throws Exception {
                interceptFrom("seda:assembledJobs")
                .wireTap("seda:interceptedJobs");
            }
        });
        
        // get a reference to the landingzone directory
        File directory = FileUtils.getFile(lz.getDirectory());

        // get our input file
        File successFile = ResourceUtils.getFile("classpath:zip/AllFiles.zip");

        // copy the file into the lz
        FileUtils.copyFileToDirectory(successFile, directory);

        // wait a second
        Thread.sleep(1000);
        
        // check the intercepted job queue, make sure a job went through,
        // and find out its jobId
        BrowsableEndpoint be = context.getEndpoint("seda:interceptedJobs", BrowsableEndpoint.class);
        List<Exchange> list = be.getExchanges();
        assertEquals(1, list.size());
        BatchJob job = list.get(0).getIn().getBody(BatchJob.class);
        assertNotNull("job was null", job);
        
        // look for a success report for this job
        int matchCount = 0;
        File reportFile = null;
        String[] exts = {"log"};
        List<File> dirFiles = (List<File>) FileUtils.listFiles(directory, exts, false);
        for (File dirFile : dirFiles) {
            if (dirFile.getName().indexOf(job.getId()) > -1) {
                matchCount++;
                reportFile = dirFile;
            }
        }
        assertNotNull("no report file match for the expected jobId", reportFile);
        assertEquals("more than one file matched for the expected jobId",
                        1, matchCount);
        
        // make sure the file contained an indication of success
        String fileBody = FileUtils.readFileToString(reportFile);
        
        // TODO assert the content of the report is success reading the zip.
        
    }

}
