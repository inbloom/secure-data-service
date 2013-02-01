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

package org.slc.sli.ingestion.routes.orchestra;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Latching mechanism to synchronize WorkNote processing program flow
 *
 * @author dduran
 *
 */
@Component
public class WorkNoteLatch {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Handler
    public void receive(Exchange exchange) throws Exception {
        exchange.getIn().setHeader("latchOpened", false);

        String messageType = exchange.getIn().getHeader("IngestionMessageType").toString();

        RangedWorkNote workNote = exchange.getIn().getBody(RangedWorkNote.class);

        TenantContext.setJobId(workNote.getBatchJobId());

        String recordType = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();

        if (batchJobDAO.countDownLatch(messageType, workNote.getBatchJobId(), recordType)) {

            exchange.getIn().setHeader("latchOpened", true);

        }
    }
}
