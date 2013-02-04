/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.ingestion.tenant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.util.LogUtil;

/**
 * Populates the tenant database collection with default tenant collections.
 *
 * @author jtully
 */
@Component
public class TenantPopulator implements ResourceLoaderAware {

    private static final Logger LOG = LoggerFactory.getLogger(TenantPopulator.class);

    @Autowired
    private TenantDA tenantDA;

    private ResourceLoader resourceLoader;

    private String parentLandingZoneDir;

    private List<String> tenantRecordResourcePaths;

    private static final String HOSTNAME_PLACEHOLDER = "<hostname>";
    private static final String PARENT_LZ_PATH_PLACEHOLDER = "<lzpath>";

    /**
     * Populate the tenant data store with a default set of tenants.
     *
     */
    public void populateDefaultTenants() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            createParentLzDirectory();
            List<TenantRecord> tenants = constructDefaultTenantCollection(hostName);
            for (TenantRecord tenant : tenants) {
                tenantDA.insertTenant(tenant);
            }
        } catch (Exception e) {
            LOG.error("Exception encountered populating default tenants:", e);
        }
    }

    /**
     * Construct the default tenant collection based on the configured TenantRecord resources.
     *
     * @param hostName
     * @return a list of constructed TenantRecord objects
     */
    private List<TenantRecord> constructDefaultTenantCollection(String hostname) {
        List<TenantRecord> tenants = new ArrayList<TenantRecord>();
        for (String tenantResourcePath : tenantRecordResourcePaths) {
            TenantRecord tenant = loadTenant(tenantResourcePath);
            replaceTenantPlaceholderFields(tenant, hostname);
            createTenantLzDirectory(tenant);
            tenants.add(tenant);
        }
        return tenants;
    }

    /**
     *
     * Create the landing zone directory for the parent landing zone
     *
     */
    private void createParentLzDirectory() {
        String lzPath = Matcher.quoteReplacement(parentLandingZoneDir);
        File lzDirectory = new File(lzPath);
        if (!lzDirectory.mkdir()) {
            LOG.debug("Failed to mkdir: {}", lzDirectory.getPath());
        }
        if (!lzDirectory.setReadable(true, false)) {
            LOG.debug("Failed to setReadable: {}", lzDirectory.getPath());
        }
        if (!lzDirectory.setWritable(true, false)) {
            LOG.debug("Failed to setWritable: {}", lzDirectory.getPath());
        }
    }

    /**
     *
     * Create the landing zone directory for a tenant.
     *
     * @param tenant
     *            , the tenant for which to create landing zone directories
     *
     */
    private void createTenantLzDirectory(TenantRecord tenant) {
        List<LandingZoneRecord> landingZones = tenant.getLandingZone();
        for (LandingZoneRecord lz : landingZones) {
            String lzPath = lz.getPath();
            File lzDirectory = new File(lzPath);
            if (!lzDirectory.mkdir()) {
                LOG.debug("Failed to mkdir: {}", lzDirectory.getPath());
            }
            if (!lzDirectory.setReadable(true, false)) {
                LOG.debug("Failed to setReadable: {}", lzDirectory.getPath());
            }
            if (!lzDirectory.setWritable(true, false)) {
                LOG.debug("Failed to setWritable: {}", lzDirectory.getPath());
            }
        }
    }

    /**
     *
     * Process TenantRecord, Replacing hostname and lzPath placeholder fields with
     * the actual values.
     *
     * @param tenant
     *            record to be processed
     * @param hostname
     *            the hostname to be used in placeholder replacement
     */
    private void replaceTenantPlaceholderFields(TenantRecord tenant, String hostname) {
        List<LandingZoneRecord> landingZones = tenant.getLandingZone();
        for (LandingZoneRecord lz : landingZones) {
            // replace hostname field
            String serverVal = lz.getIngestionServer();
            serverVal = serverVal.replaceFirst(HOSTNAME_PLACEHOLDER, hostname);
            lz.setIngestionServer(serverVal);

            String pathVal = lz.getPath();
            pathVal = pathVal.replaceFirst(PARENT_LZ_PATH_PLACEHOLDER, Matcher.quoteReplacement(parentLandingZoneDir));
            lz.setPath(new File(pathVal).getAbsolutePath());
        }
    }

    /**
     * Loads a TenantRecord from a tenant resource
     *
     * @param resourcePath
     *            to load into a tenant record
     * @return the loaded tenant
     */
    private TenantRecord loadTenant(String resourcePath) {
        TenantRecord tenant = null;
        InputStream tenantIs = null;
        try {
            Resource config = resourceLoader.getResource(resourcePath);

            if (config.exists()) {
                tenantIs = config.getInputStream();
                tenant = TenantRecord.parse(tenantIs);
            }
        } catch (IOException e) {
            LogUtil.error(LOG, "Exception encountered loading tenant resource: ", e);
        } finally {
            IOUtils.closeQuietly(tenantIs);
        }
        return tenant;
    }

    /**
     * Obtain the hostname for the ingestion server running
     *
     * @throws UnknownHostException
     */
    private String getHostname() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @return the parentLandingZoneDir
     */
    public String getParentLandingZoneDir() {
        return parentLandingZoneDir;
    }

    /**
     * @param parentLandingZoneDir
     *            the parentLandingZoneDir to set
     */
    public void setParentLandingZoneDir(String singleLandingZoneDir) {
        File lzFile = new File(singleLandingZoneDir);
        this.parentLandingZoneDir = lzFile.getAbsolutePath();
    }

    public List<String> getTenantRecordResourcePaths() {
        return tenantRecordResourcePaths;
    }

    public void setTenantRecordResourcePaths(List<String> tenantRecordResourcePaths) {
        this.tenantRecordResourcePaths = tenantRecordResourcePaths;
    }

}
