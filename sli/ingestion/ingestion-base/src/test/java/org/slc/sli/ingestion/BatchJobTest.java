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

package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Unit tests for BatchJob functionality.
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class BatchJobTest {

    @Test
    public void testCreateId() {
        String id1 = BatchJob.createId(null);
        String id2 = BatchJob.createId(null);

        assertFalse(id1.equals(id2));
    }

    @Test
    public void testProperties() {

        Job job = BatchJob.createDefault();

        Set<String> names = job.propertyNames();
        assertEquals(0, names.size());

        assertNull(job.getProperty("hello"));
        assertEquals("world", job.getProperty("hello", "world"));

        job.setProperty("hello", "dolly");

        assertEquals("dolly", job.getProperty("hello"));
        assertEquals("dolly", job.getProperty("hello", "world"));
    }

    @Test
    public void testCreateDefault() throws InterruptedException {

        // generate dates before and after the BatchJob is instantiated,
        // so we can verify its creationDate is accurate.
        Job job = BatchJob.createDefault();

        ArrayList<IngestionFileEntry> files = (ArrayList<IngestionFileEntry>) job.getFiles();
        assertEquals(files.size(), 0);

        Job job2 = BatchJob.createDefault("TEST");
        assertEquals(true, job2.getId().startsWith("TEST"));

    }
}
