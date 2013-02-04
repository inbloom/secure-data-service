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

package org.slc.sli.modeling.tools.wadl2Doc;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;

/**
 * JUnit test for WadlViewer class.
 */
public class WadlViewerTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testNonInstantiable() {
        new WadlViewer();
    }

    @Test
    public void testMain() {
        final StringBuffer stringBuffer = new StringBuffer();
        PrintStream stdOut = System.out;
        PrintStream myOut = new PrintStream(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                stringBuffer.append((char) b);
            }

        });

        System.setOut(myOut);
        WadlViewer.main(new String[]{"src/test/resources/domain_SLI.wadl"});
        System.setOut(stdOut);

        assertFalse(stringBuffer.toString().equals(""));
    }
}
