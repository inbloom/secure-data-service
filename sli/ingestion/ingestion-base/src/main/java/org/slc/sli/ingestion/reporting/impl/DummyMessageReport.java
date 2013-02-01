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

package org.slc.sli.ingestion.reporting.impl;

import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.MessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;

/**
 * Dummy implementation of the AbstractMessageReport. Do not report warnings/errors.
 *
 * @author okrook
 *
 */
@Component
public class DummyMessageReport extends AbstractMessageReport {

    @Override
    public void reportError(ReportStats reportStats, Source source, MessageCode code, Object... args) {
        // Do nothing
    }

    @Override
    public void reportWarning(ReportStats reportStats, Source source, MessageCode code, Object... args) {
     // Do nothing
    }

    @Override
    protected void reportInfo(ReportStats reportStats, Source source, MessageCode code, Object... args) {

        // Do nothing
    }
}
