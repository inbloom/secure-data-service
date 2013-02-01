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

package org.slc.sli.ingestion.handler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.validation.Validator;

/**
 * A Junit test to test AbstractIngestionHandler
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AbstractIngestionHandlerTest {

    AbstractIngestionHandler<Resource, Object> handler;

    @SuppressWarnings("unchecked")
    @Test
    public void handleTest() {
        handler = Mockito.mock(AbstractIngestionHandler.class);

        Mockito.doCallRealMethod()
                .when(handler)
                .handle(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class), Mockito.any(ReportStats.class));
        Mockito.doCallRealMethod()
                .when(handler)
                .handle(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(FileProcessStatus.class));
        Mockito.doCallRealMethod().when(handler)
                .pre(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class), Mockito.any(ReportStats.class));
        Mockito.doCallRealMethod().when(handler)
                .post(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class), Mockito.any(ReportStats.class));
        Mockito.doCallRealMethod().when(handler).setPreValidators(Mockito.anyList());
        Mockito.doCallRealMethod().when(handler).setPostValidators(Mockito.anyList());

        Validator<Resource> preValidator = Mockito.mock(Validator.class);
        Mockito.when(
                preValidator.isValid(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(Source.class))).thenReturn(true);
        List<Validator<Resource>> preValidators = new ArrayList<Validator<Resource>>();
        preValidators.add(preValidator);
        handler.setPreValidators(preValidators);

        Validator<Resource> postValidator = Mockito.mock(Validator.class);
        Mockito.when(
                postValidator.isValid(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(Source.class))).thenReturn(true);
        List<Validator<Resource>> postValidators = new ArrayList<Validator<Resource>>();
        postValidators.add(postValidator);
        handler.setPostValidators(postValidators);

        Resource ife = Mockito.mock(Resource.class);
        Mockito.when(
                handler.doHandling(Mockito.any(Resource.class), Mockito.any(AbstractMessageReport.class),
                        Mockito.any(ReportStats.class), Mockito.any(FileProcessStatus.class))).thenReturn(ife);

        AbstractMessageReport report = Mockito.mock(AbstractMessageReport.class);
        ReportStats reportStats = Mockito.mock(ReportStats.class);

        Object fileEntry = null;
        fileEntry = handler.handle(ife, report, reportStats);

        Assert.assertNotNull(fileEntry);
    }

}
