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

package org.slc.sli.ingestion.smooks;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.handler.XmlFileHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.service.IngestionExecutor;

/**
 * Tests for SmooksCallable
 *
 * @author dduran
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksCallableTest {

    @Autowired
    XmlFileHandler xmlFileHandler;

    @Autowired
    SliSmooksFactory sliSmooksFactory;

    @Mock
    private NewBatchJob newBatchJob;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCallable() throws InterruptedException, ExecutionException {

        String batchJobId = "7157248518";

        String filePath = SmooksCallableTest.class.getClassLoader()
                .getResource("smooks/unitTestData/InterchangeStaffAssociation.xml").getPath();
        File fileToIngest = new File(filePath);

        IngestionFileEntry fe = new IngestionFileEntry(fileToIngest.getParentFile().getAbsolutePath(), FileFormat.EDFI_XML, FileType.XML_STUDENT_ATTENDANCE, fileToIngest.getName(), null);
        fe.setBatchJobId(batchJobId);

        Stage stage = new Stage();

        List<FutureTask<Boolean>> smooksOfTheFutureList = new ArrayList<FutureTask<Boolean>>();
        for (int i = 0; i < 3; i++) {
            SmooksCallable smooksCallabe = new SmooksCallable(newBatchJob, xmlFileHandler, fe, null, null, stage, sliSmooksFactory);

            FutureTask<Boolean> smooksOfTheFuture = IngestionExecutor.execute(smooksCallabe);

            smooksOfTheFutureList.add(smooksOfTheFuture);
        }

        for (FutureTask<Boolean> smooksOfTheFuture : smooksOfTheFutureList) {
            assertFalse(smooksOfTheFuture.get());
        }
    }
}
