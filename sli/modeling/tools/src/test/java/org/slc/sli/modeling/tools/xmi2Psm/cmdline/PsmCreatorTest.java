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

package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test for PsmCreator class.
 */
public class PsmCreatorTest {

    @Test
    public void testMain() {
        String inputFilename = "src/test/resources/psm_sli.xmi";
        String outputFilename = "PsmCreatorOutput.xml";

        PsmCreator.main(new String[]{inputFilename, outputFilename});
        File file = new File(outputFilename);
        assertTrue(file.exists());
        file.delete();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new PsmCreator();
    }
}
