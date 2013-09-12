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

package org.slc.sli.bulk.extract;

import static org.slc.sli.bulk.extract.LogUtil.audit;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.extractor.DeltaExtractor;
import org.slc.sli.bulk.extract.extractor.LocalEdOrgExtractor;
import org.slc.sli.bulk.extract.extractor.StatePublicDataExtractor;
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slc.sli.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bulk extract launcher.
 * 
 * @author tke
 * 
 */
public class Launcher {
    private static final String USAGE = "Usage: bulk-extract <tenant> [isDelta]";
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    
    private String baseDirectory;

    @Autowired
    private DeltaExtractor deltaExtractor;
    
    private BulkExtractMongoDA bulkExtractMongoDA;
    
    private LocalEdOrgExtractor localEdOrgExtractor;
    
    @Autowired
    private SecurityEventUtil securityEventUtil;
    
    @Autowired
    private StatePublicDataExtractor statePublicDataExtractor;
    
    /**
     * Actually execute the extraction.
     * 
     * @param tenant
     *            Tenant for which extract has been initiated
     */
    public void execute(String tenant, boolean isDelta) {
        audit(securityEventUtil.createSecurityEvent(Launcher.class.getName(), "Bulk extract execution",
                LogLevelType.TYPE_INFO, BEMessageCode.BE_SE_CODE_0001));
        
        Entity tenantEntity = bulkExtractMongoDA.getTenant(tenant);
        if (tenantEntity != null) {
            DateTime startTime = new DateTime();
            if (isDelta) {
            	LOG.info("isDelta=true ... deltaExtractor.execute()");
                deltaExtractor.execute(tenant, startTime, baseDirectory);
            } else {
            	LOG.info("isDelta=false ... localEdOrgExtractor.execute()");
                localEdOrgExtractor.execute(tenant, getTenantDirectory(tenant), startTime);
                LOG.info("Starting public data extract...");
                statePublicDataExtractor.execute(tenant, getTenantDirectory(tenant), startTime);
            }
        } else {
            audit(securityEventUtil.createSecurityEvent(Launcher.class.getName(), "Bulk extract execution",
                    LogLevelType.TYPE_ERROR, BEMessageCode.BE_SE_CODE_0002, tenant));
            LOG.error(
                    "A bulk extract is not being initiated for the tenant {} because the tenant has not been onboarded.",
                    tenant);
        }
    }
    
    // those two methods should be moved to localEdOrgExtractor once we switched to
    // LEA level extract, for now it's duplicated in both classes.
    public static String getArchiveName(String tenant, Date startTime) {
        return tenant + "-" + getTimeStamp(startTime);
    }
    
    private File getTenantDirectory(String tenant) {
        File tenantDirectory = new File(baseDirectory, TenantAwareMongoDbFactory.getTenantDatabaseName(tenant));
        tenantDirectory.mkdirs();
        return tenantDirectory;
    }
    
    /**
     * Change the timestamp into our own format.
     * 
     * @param date
     *            Timestamp
     * @return
     *         returns the formatted timestamp
     */
    public static String getTimeStamp(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String timeStamp = df.format(date);
        return timeStamp;
    }
    
    /**
     * Set base dir.
     * 
     * @param baseDirectory
     *            Base directory of all bulk extract processes
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    /**
     * Main entry point.
     * 
     * @param args
     *            input arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            LOG.error(USAGE);
            return;
        }
        
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        
        Launcher main = context.getBean(Launcher.class);
        String tenantId = args[0];
        boolean isDelta = false;
        if (args.length == 2) {
            isDelta = Boolean.parseBoolean(args[1]);
        }
        
        main.execute(tenantId, isDelta);
        
    }
    
    public void setLocalEdOrgExtractor(LocalEdOrgExtractor localEdOrgExtractor) {
        this.localEdOrgExtractor = localEdOrgExtractor;
    }
    
    public LocalEdOrgExtractor getLocalEdOrgExtractor() {
        return localEdOrgExtractor;
    }
    
    public void setStatePublicDataExtractor(StatePublicDataExtractor statePublicDataExtractor) {
        this.statePublicDataExtractor = statePublicDataExtractor;
    }
    
    /**
     * Get bulkExtractMongoDA.
     * 
     * @return the bulkExtractMongoDA
     */
    public BulkExtractMongoDA getBulkExtractMongoDA() {
        return bulkExtractMongoDA;
    }
    
    /**
     * Set bulkExtractMongoDA.
     * 
     * @param bulkExtractMongoDA
     *            the bulkExtractMongoDA to set
     */
    public void setBulkExtractMongoDA(BulkExtractMongoDA bulkExtractMongoDA) {
        this.bulkExtractMongoDA = bulkExtractMongoDA;
    }
    
    /**
     * Set securityEventUtil.
     * 
     * @param securityEventUtil
     *            the securityEventUtil to set
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }
}
