/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.tenant.TenantDA;

/**
 * Processor for tenant collection polling
 *
 * @author jtully
 *
 */
@Component
public class TenantProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TenantProcessor.class);

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private TenantDA tenantDA;

    @Value("${sli.ingestion.queue.landingZone.queueURI}")
    private String landingZoneQueueUri;

    private Map<String, List<String>> dataSetLookup;

    public static final String TENANT_POLL_HEADER = "TENANT_POLL_STATUS";
    public static final String TENANT_POLL_SUCCESS = "SUCCESS";
    public static final String TENANT_POLL_FAILURE = "FAILURE";

    @Override
    public void process(Exchange exchange) throws Exception {
        try {

            createNewLandingZones();

            exchange.getIn().setHeader(TENANT_POLL_HEADER, TENANT_POLL_SUCCESS);

            doPreloads();

        } catch (Exception e) {
            exchange.getIn().setHeader(TENANT_POLL_HEADER, TENANT_POLL_FAILURE);
            LOG.error("Exception encountered adding tenant", e);
        }
    }

    /**
     * Attempt to create new landing zones based on the tenant DB collection.
     */
    private void createNewLandingZones() {
        try {
            List<String> lzPaths = tenantDA.getLzPaths();
            LOG.debug("TenantProcessor: Localhost is {}", getHostname());
            for (String currLzPath : lzPaths) {

                createDirIfNotExists(currLzPath);

            }
        } catch (UnknownHostException e) {
            LOG.error("TenantProcessor", e);
        }
    }

    /**
     * Creating the directory, so that camel won't complain about
     * directory name with dot(.) in it.
     *
     * @param inboundDir
     *            : the absolute directory path to be created
     * @return : true if successfully created the directory.
     **/
    private boolean createDirIfNotExists(String inboundDir) {

        File landingZoneDir = new File(inboundDir);
        try {
            // will try to create if dir doesn't already exist
            FileUtils.forceMkdir(landingZoneDir);
        } catch (IOException e) {
            LOG.error("TenantProcessor: Failed to create landing zone: {} ", inboundDir);
            return false;
        }

        return true;
    }

    /**
     * Obtain the hostname for the ingestion server running.
     *
     * @throws UnknownHostException
     */
    private String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    /**
     * Preload the given files into the given landing zone
     *
     * @param landingZone
     *            the landing zone to preload the files into
     * @param dataSets
     *            the files to preload
     * @return whether or not every file was successfully preloaded
     */
    boolean preLoad(String landingZone, List<String> dataSets) {
        File landingZoneDir = new File(landingZone);
        if (!landingZoneDir.exists()) {
            try {
                // will try to create if dir doesn't already exist
                FileUtils.forceMkdir(landingZoneDir);
            } catch (IOException e) {
                LOG.error("TenantProcessor: Failed to create landing zone: {} with absolute path {}", landingZone, landingZoneDir.getAbsolutePath());
                return false;
            }
        }
        if (landingZoneDir.exists() && landingZoneDir.isDirectory()) {
            boolean result = true;
            for (String dataSet : dataSets) {
                List<String> fileNames = getDataSetLookup().get(dataSet);
                if (fileNames != null) {
                    for (String fileName : fileNames) {
                        URL fileLocation = Thread.currentThread().getContextClassLoader().getResource(fileName);
                        try {
                            InputStream sampleFile = fileLocation == null ? new FileInputStream(fileName)
                                    : fileLocation.openStream();
                            result &= sendToLandingZone(landingZoneDir, sampleFile);
                        } catch (FileNotFoundException e) {
                            LOG.error("sample data set {} doesn't exists", fileName);
                            result = false;
                        } catch (IOException e) {
                            LOG.error("error loading sample data set", e);
                        }
                    }
                } else {
                    result = false;
                }
            }
            return result;
        }
        return false;
    }

    /**
     * Send the given file to the landing zone
     *
     * @param landingZoneDir
     *            the file representing the ladnding zone
     * @param sampleFile
     *            the file to send to the landing zone
     * @return true if the file (or all of its children if it is a directory) was successfully
     *         copied over to the landing zone directory
     */
    private boolean sendToLandingZone(File landingZoneDir, InputStream sampleFile) {
        boolean result = true;
        File preloadedFile = new File(landingZoneDir, "preload-" + (new Date()).getTime() + ".zip");
        try {
            if (!preloadedFile.createNewFile()) {
                LOG.debug("Failed to createNewFile: {}", preloadedFile.getPath());
            }
            FileUtils.copyInputStreamToFile(sampleFile, preloadedFile);

            sendMessageToLzQueue(preloadedFile.getPath());

        } catch (IOException e) {
            LOG.error("Error creating sample file in landingZone" + landingZoneDir.getAbsolutePath(), e);
            result = false;
        } finally {
            try {
                sampleFile.close();
            } catch (IOException e) {
                LOG.error("Error creating sample file in landingZone" + landingZoneDir.getAbsolutePath(), e);
                result = false;
            }
        }
        return result;
    }

    /**
     * Send a message to the landing zone queue for the given file.
     *
     * @param filePathname
     *            the file to be ingested
     *
     * @return true if the message was successfully sent to the landing zone queue
     *
     * @throws IOException
     */
    private void sendMessageToLzQueue(String filePathname) {
        // Create a new process to invoke the ruby script to send the message.
        try {
            /*
             * The logic to send this message is also present in following ruby script. Any changes
             * here should also be made to the script.
             * sli/opstools/ingestion_trigger/publish_file_uploaded.rb
             */
            ProducerTemplate template = new DefaultProducerTemplate(camelContext);
            template.start();
            template.sendBodyAndHeader(landingZoneQueueUri, "Sample lzfile message", "filePath", filePathname);
            template.stop();
        } catch (Exception e) {
            LOG.error("Error publishing sample file " + filePathname + " for ingestion", e);
        }
    }

    /**
     * Perform preloading
     *
     * @return
     */
    boolean doPreloads() {

        Map<String, List<String>> preloadMap = tenantDA.getPreloadFiles();
        boolean result = true;
        for (Entry<String, List<String>> entry : preloadMap.entrySet()) {
            result &= preLoad(entry.getKey(), entry.getValue());
        }
        return result;
    }

    Map<String, List<String>> getDataSetLookup() {
        return dataSetLookup;
    }

    @Value("${sli.ingestion.dataset.sample}")
    @SuppressWarnings("unchecked")
    void setDataSetLookup(String dataSetLookup) throws IOException {
        ObjectMapper om = new ObjectMapper();
        this.dataSetLookup = om.readValue(dataSetLookup, Map.class);
    }

}
