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
package org.slc.sli.bulk.extract.extractor;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.context.resolver.EdOrgContextResolverFactory;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.repository.DeltaJournal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

@Component
public class DeltaExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(DeltaExtractor.class);

    @Autowired
    EdOrgContextResolverFactory resolverFactory;

    @Autowired
    @Qualifier("validationRepo")
    Repository<Entity> repo;

    @Autowired
    DeltaJournal deltaJournal;

    public void execute(String tenant, DateTime deltaUptoTime) {
        LOG.info("Generating deltas for tenant: %s", tenant);
        TenantContext.setTenantId(tenant);
        long lastDeltaTime = getLastDeltaRun(tenant);
        LOG.info(String.format("creating delta between %d and %d", lastDeltaTime, deltaUptoTime.getMillis()));
        
        Iterator<Map<String, Object>> deltaCursor = deltaJournal.findDeltaRecordBetween(lastDeltaTime, deltaUptoTime.getMillis());
        while (deltaCursor.hasNext()) {
            Map<String, Object> delta = deltaCursor.next(); 

            long deletedTime = 0;
            long updatedTime = 0;
            if (delta.containsKey("d")) {
                deletedTime = (Long) delta.get("d");
            }
            if (delta.containsKey("u")) {
                updatedTime = (Long) delta.get("u");
            }
            
            if (deletedTime > updatedTime) {
                // this entity is deleted
                LOG.debug("entity: " + delta.get("_id") + " has been deleted");
            } else {
                // last operation is update
                if (delta.get("c").equals("educationOrganization")) {
                    Entity edorg = repo.findById((String) delta.get("c"), (String) delta.get("_id"));
                    if (edorg != null) {
                        LOG.info("entity belongs to: " + resolverFactory.getResolver((String) delta.get("c")).findGoverningLEA(edorg));
                    }
                }
            }
        }
    }

    private long getLastDeltaRun(String tenant) {
        long lastRun = 0; // assume if we can't find last time it ran, we need to get all the deltas

        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", NeutralCriteria.OPERATOR_EQUAL, tenant));
        query.addCriteria(new NeutralCriteria("isDelta", NeutralCriteria.OPERATOR_EQUAL, "true"));
        query.setSortBy("body.date");
        query.setSortOrder(NeutralQuery.SortOrder.descending);
        query.setIncludeFields(Arrays.asList("body.date"));

        Iterable<Entity> entities = repo.findAll(BulkExtractMongoDA.BULK_EXTRACT_COLLECTION, query);
        if (entities == null) {
            return lastRun;
        }
        
        if (entities.iterator().hasNext()) {
            Map<String, Object> body = entities.iterator().next().getBody();
            if (body != null) {
                Date date = (Date) body.get("date");
                lastRun = (date != null) ? date.getTime() : 0;
            }
        }

        return lastRun;

    }
}
