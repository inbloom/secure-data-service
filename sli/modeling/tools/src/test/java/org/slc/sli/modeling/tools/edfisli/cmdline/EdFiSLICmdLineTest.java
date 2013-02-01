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

package org.slc.sli.modeling.tools.edfisli.cmdline;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;

/**
 * JUnit test for EdFiSLICmdLine class.
 */
public class EdFiSLICmdLineTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new EdFiSLICmdLine();
    }

    @Test
    public void testMain() {

        String inputSliXmiFilename = "src/test/resources/SLI.xmi";
        String inputEdfiXmiFilename = "src/test/resources/Ed-Fi-Core.xmi";
        String[] args = new String[]{inputSliXmiFilename, inputEdfiXmiFilename};

        final StringBuffer stringBuffer = new StringBuffer();

        PrintStream stdOut = System.out;
        PrintStream myOut = new PrintStream(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                stringBuffer.append((char) b);
            }

        });

        System.setOut(myOut);
        EdFiSLICmdLine.main(args);
        System.setOut(stdOut);

        assertFalse(stringBuffer.toString().equals(""));
    }
}
