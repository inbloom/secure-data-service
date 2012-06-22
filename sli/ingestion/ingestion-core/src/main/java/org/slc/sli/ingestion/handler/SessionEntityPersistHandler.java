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

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class used to persist Session .
 *
 * @author ablum
 *
 */
public class SessionEntityPersistHandler extends EntityPersistHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEntityPersistHandler.class);

    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        Entity mongoSession = super.doHandling(entity, errorReport, fileProcessStatus);
        LOG.debug("created session : {}", mongoSession);
        return mongoSession;
    }
}
