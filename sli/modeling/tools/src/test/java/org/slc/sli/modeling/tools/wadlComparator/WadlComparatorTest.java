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

package org.slc.sli.modeling.tools.wadlComparator;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test for WadlComparator class.
 */
public class WadlComparatorTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new WadlComparator();
    }

    @Test
    public void testMain() throws IOException {

        String pathToGoldenWadl = "src/test/resources/domain_SLI.wadl";
        String pathToGeneratedWadl = "src/test/resources/scaffold_eapplication.wadl";
        String pathToReportFile = "WadlComparatorOutput.txt";

        WadlComparator.main(new String[]{pathToGoldenWadl, pathToGeneratedWadl, pathToReportFile});
        File file = new File(pathToReportFile);
        assertTrue(file.exists());
        file.delete();

    }
}
