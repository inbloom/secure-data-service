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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slc.sli.bulk.extract.extractor.TenantExtractor;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slc.sli.dal.repository.connection.TenantAwareMongoDbFactory;
/**
 * Bulk extract launcher.
 *
 * @author tke
 *
 */
public class Launcher {
    private static final String USAGE = "Usage: bulk-extract <tenant>";
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private String baseDirectory;

    private TenantExtractor tenantExtractor;

    /**
     * actually execute the extraction
     *
     * @param tenant
     */
    public void execute(String tenant){

        Date startTime = new Date();
        OutstreamZipFile outputStream = null;
        try {
            outputStream = createExtractArchiveFile(tenant, startTime);
            tenantExtractor.execute(tenant, outputStream, startTime);
        } catch (IOException e) {
            LOG.error("Error while extracting data for tenant " + tenant, e);
        }
    }

    private OutstreamZipFile createExtractArchiveFile(String tenant, Date startTime) throws IOException {

        return new OutstreamZipFile(getTenantDirectory(tenant), tenant + "-" + getTimeStamp(startTime));
    }

    private String getTenantDirectory(String tenant) {

        File tenantDirectory = new File(baseDirectory, TenantAwareMongoDbFactory.getTenantDatabaseName(tenant));
        tenantDirectory.mkdirs();
        return tenantDirectory.getPath();
    }

    /**
     * change the timestamp into our own format
     * @param date
     * @return
     */
    public static String getTimeStamp(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String timeStamp = df.format(date);
        return timeStamp;
    }

    /**
     * set base dir
     * @param baseDirectory
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * set tenant extractor
     * @param tenantExtractor
     */
    public void setTenantExtractor(TenantExtractor tenantExtractor){
        this.tenantExtractor = tenantExtractor;
    }

    /**
     * main entry
     * @param args
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
