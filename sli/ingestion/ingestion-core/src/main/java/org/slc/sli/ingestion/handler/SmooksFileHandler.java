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

package org.slc.sli.ingestion.handler;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
@Component
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        try {

            generateNeutralRecord(fileEntry, errorReport, fileProcessStatus);

        } catch (IOException e) {
            LOG.error("IOException: Could not instantiate smooks, unable to read configuration file", e);
            errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
                    SmooksFileHandler.class);
        } catch (SAXException e) {
            LOG.error("SAXException: Could not instantiate smooks, problem parsing configuration file", e);
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    SmooksFileHandler.class);
        }

        return fileEntry;
    }

    @SuppressWarnings("unchecked")
    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) throws IOException, SAXException {

        // create instance of Smooks (with visitors already added)
        Smooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, errorReport);

        InputStream inputStream = new BufferedInputStream(new FileInputStream(ingestionFileEntry.getFile()));
        try {
            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));

            try {
                final Field f = smooks.getClass().getDeclaredField("visitorConfigMap");
                AccessController.doPrivileged(
                    new PrivilegedAction<Object>() {
                        public Object run() {
                            f.setAccessible(true);
                            return null;
                        }
                    }
                );
                VisitorConfigMap map = (VisitorConfigMap) f.get(smooks);
                ContentHandlerConfigMapTable<SAXVisitAfter> visitAfters = map.getSaxVisitAfters();
                SmooksEdFiVisitor visitAfter = (SmooksEdFiVisitor) visitAfters.getAllMappings().get(0)
                        .getContentHandler();

                int recordsPersisted = visitAfter.getRecordsPerisisted();
                fileProcessStatus.setTotalRecordCount(recordsPersisted);

                LOG.info("Parsed and persisted {} records to staging db from file: {}.", recordsPersisted,
                        ingestionFileEntry.getFileName());
            } catch (Exception e) {
                LOG.error("Error accessing visitor list in smooks", e);
            }
        } catch (SmooksException se) {
            LOG.error("smooks exception: encountered problem with " + ingestionFileEntry.getFile().getName() + "\n", se);
            errorReport.error("SmooksException encountered while filtering input.", SmooksFileHandler.class);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        return null;
    }
}
