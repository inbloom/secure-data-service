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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.extractor.TenantExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Bulk extract launcher.
 *
 * @author tke
 *
 */
public class Launcher {
    private static final String USAGE = "Usage: bulk-extract <tenant>";
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    private static final String TENANT = "tenant";

    private String baseDirectory;
    private TenantExtractor tenantExtractor;
    private Repository<Entity> repository;

    /**
     * Actually execute the extraction.
     *
     * @param tenant
     *          Tenant for which extract has been initiated
     */
    public void execute(String tenant) {
        if (tenantExists(tenant)) {
            DateTime startTime = new DateTime();
            ExtractFile extractFile = null;
            extractFile = new ExtractFile(getTenantDirectory(tenant),
                    getArchiveName(tenant, startTime.toDate()));
            tenantExtractor.execute(tenant, extractFile, startTime);
        } else {
            LOG.error("A bulk extract is not being initiated for the tenant {} because the tenant has not been onboarded.", tenant);
        }
    }

    private String getArchiveName(String tenant, Date startTime) {
        return tenant + "-" + getTimeStamp(startTime);
    }

    private File getTenantDirectory(String tenant) {
        File tenantDirectory = new File(baseDirectory, TenantAwareMongoDbFactory.getTenantDatabaseName(tenant));
        tenantDirectory.mkdirs();
        return tenantDirectory;
    }

    private boolean tenantExists(String tenant) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL ,tenant));
        query.addCriteria(new NeutralCriteria("tenantIsReady", NeutralCriteria.OPERATOR_EQUAL, true));
        TenantContext.setIsSystemCall(true);
        Entity tenantEntity = repository.findOne(TENANT, query);
        TenantContext.setIsSystemCall(false);
        return tenantEntity != null ? true : false;
    }

    /**
     * Change the timestamp into our own format.
     * @param date
     *      Timestamp
     * @return
     *      returns the formatted timestamp
     */
    public static String getTimeStamp(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String timeStamp = df.format(date);
        return timeStamp;
    }

    /**
     * Set base dir.
     * @param baseDirectory
     *          Base directory of all bulk extract processes
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Set tenant extractor.
     * @param tenantExtractor
     *          TenantExtractor object
     */
    public void setTenantExtractor(TenantExtractor tenantExtractor){
        this.tenantExtractor = tenantExtractor;
    }

    /**
     * Set repository.
     * @param repository
     *      Repository object
     */
    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }
    
    /**
     * A helper function to get the list of approved app ids that have bulk extract enabled
     * 
     * @return a set of approved bulk extract app ids
     */
    public Set<String> getBulkExctractApps() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("isBulkExtract", NeutralCriteria.OPERATOR_EQUAL,
                true));
        appQuery.addCriteria(new NeutralCriteria("registration.status", NeutralCriteria.OPERATOR_EQUAL, "APPROVED"));
        Iterable<Entity> apps = repository.findAll("application", appQuery);
        Set<String> appIds = new HashSet<String>();
        for (Entity app : apps) {
            appIds.add(app.getEntityId());
        }
        return appIds;
    }
    
    /**
     * Attempts to get all of the LEAs that should have a LEA level extract scheduled.
     * 
     * @return a set of the LEA ids that need a bulk extract
     */
    @SuppressWarnings("unchecked")
    public Set<String> getBulkExtractLEAs() {
        NeutralQuery appQuery = new NeutralQuery(new NeutralCriteria("applicationId", NeutralCriteria.CRITERIA_IN,
                getBulkExctractApps()));
        Iterable<Entity> apps = repository.findAll("applicationAuthorization", appQuery);
        Set<String> edorgIds = new HashSet<String>();
        for (Entity app : apps) {
            edorgIds.addAll((Collection<String>) app.getBody().get("edorgs"));
        }
        return edorgIds;
    }

    /**
     * Main entry point.
     * @param args
     *      input arguments
     */
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");

        Launcher main = context.getBean(Launcher.class);

        if (args.length != 1) {
            LOG.error(USAGE);
            return;
        }

        String tenantId = args[0];

        main.execute(tenantId);

    }
}
