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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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

    private Map<String, List<String>> dataSetLookup;

    public static final String TENANT_POLL_HEADER = "TENANT_POLL_STATUS";
    public static final String TENANT_POLL_SUCCESS = "SUCCESS";
    public static final String TENANT_POLL_FAILURE = "FAILURE";

    private static final String INVALID_CHARACTERS = "?";

    @Override
    public void process(Exchange exchange) throws Exception {

        // We need to extract the TenantID for each thread, so the DAL has access to it.
        // try {
        // ControlFileDescriptor cfd = exchange.getIn().getBody(ControlFileDescriptor.class);
        // ControlFile cf = cfd.getFileItem();
        // String tenantId = cf.getConfigProperties().getProperty("tenantId");
        // TenantContext.setTenantId(tenantId);
        // } catch (NullPointerException ex) {
        // LOG.error("Could Not find Tenant ID.");
        // TenantContext.setTenantId(null);
        // }

        try {
            // updateLzRoutes();

            createNewLandingZones();

            exchange.getIn().setHeader(TENANT_POLL_HEADER, TENANT_POLL_SUCCESS);

            doPreloads();

        } catch (Exception e) {
            exchange.getIn().setHeader(TENANT_POLL_HEADER, TENANT_POLL_FAILURE);
            LOG.error("Exception encountered adding tenant", e);
        }
    }

/*    public void setWorkItemQueueUri(String workItemQueueUri) {
        this.workItemQueueUri = workItemQueueUri;
    }*/

    /**
     * Attempt to create new landing zones based on the tenant DB collection.
     */
    private void createNewLandingZones() {
        try {
            List<String> lzPaths = tenantDA.getLzPaths(getHostname());
            LOG.debug("TenantProcessor: Localhost is {}", getHostname());
            for (String currLzPath : lzPaths) {
                // Skip currLzPath if path already exists, failed to create the landing zone or the
                // name is invalid.
                if (!isValidDirName(currLzPath) || !createValidDir(currLzPath)) {
                    continue;
                }
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
    private boolean createValidDir(String inboundDir) {

        File landingZoneDir = new File(inboundDir);
        try {
            FileUtils.forceMkdir(landingZoneDir);
        } catch (IOException e) {
            LOG.error("TenantProcessor: Failed to create landing zone: {} ", inboundDir);
            return false;
        }

        return true;
    }

    /**
     * Check if the inboundDir name contains any invalid characters
     *
     * @param inboundDir
     *            : directory name to be checked
     * @return : true if directory name doesn't contain any invalid character.
     */
    private boolean isValidDirName(String inboundDir) {
        boolean res = StringUtils.containsNone(inboundDir, INVALID_CHARACTERS);
        if (!res) {
            LOG.error("TenantProcessor: Landing zone {} contains invalid characters", inboundDir);
        }

        return res;
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
        try {
            if (!landingZoneDir.exists()) {
                landingZoneDir.createNewFile();
            }
        } catch (IOException e) {
            LOG.error("Could not create landing zone", e);
            return false;
        }
        if (landingZoneDir.exists() && landingZoneDir.isDirectory()) {
            boolean result = true;
            for (String dataSet : dataSets) {
                List<String> fileNames = getDataSetLookup().get(dataSet);
                if (fileNames != null) {
                    for (String fileName : fileNames) {
                        URL fileLocation = this.getClass().getClassLoader().getResource(fileName);
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
            preloadedFile.createNewFile();
            FileUtils.copyInputStreamToFile(sampleFile, preloadedFile);
        } catch (IOException e) {
            LOG.error("Error copying file to landingZone" + landingZoneDir.getAbsolutePath(), e);
            result = false;
        } finally {
            try {
                sampleFile.close();
            } catch (IOException e) {
                LOG.error("Error copying file to landingZone" + landingZoneDir.getAbsolutePath(), e);
                result = false;
            }
        }
        return result;
    }

    /**
     * Perform preloading
     *
     * @return
     */
    boolean doPreloads() {
        try {
            Map<String, List<String>> preloadMap = tenantDA.getPreloadFiles(getHostname());
            boolean result = true;
            for (Entry<String, List<String>> entry : preloadMap.entrySet()) {
                result &= preLoad(entry.getKey(), entry.getValue());
            }
            return result;
        } catch (UnknownHostException e) {
            LOG.error("Error preloading files", e);
            return false;
        }
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
