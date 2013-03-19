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
package org.slc.sli.bulk.extract.metadata;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;

/**
 * @author tke
 *
 */
public class DataFileTest {

    @Test
    public void testGetVersion() throws IOException {
        DataFile meta = new DataFile();

        String version = meta.getApiVersion();

        Assert.assertTrue(version.equals("v1.4"));

        OutstreamZipFile zip = Mockito.mock(OutstreamZipFile.class);

        String testTimeStamp = "testTime";

        meta.writeToZip(zip, testTimeStamp);

        Mockito.verify(zip, Mockito.times(1)).writeData(Matchers.eq(DataFile.METADATA_VERSION));
        Mockito.verify(zip, Mockito.times(1)).writeData(Matchers.eq(DataFile.API_VERSION + version));
        Mockito.verify(zip, Mockito.times(1)).writeData(Matchers.eq(DataFile.TIME_STAMP + testTimeStamp));
    }

}
