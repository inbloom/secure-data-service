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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ablum
 *
 */
public class FileEntryWorkNoteTest {

    @Test
    public void testCreateSimpleWorkNote() {
        FileEntryWorkNote workNote = new FileEntryWorkNote("batchJobId", "SLI", null, false);
        Assert.assertEquals("batchJobId", workNote.getBatchJobId());
        Assert.assertEquals(null, workNote.getFileEntry());
        Assert.assertEquals("SLI", workNote.getTenantId());
    }

}
