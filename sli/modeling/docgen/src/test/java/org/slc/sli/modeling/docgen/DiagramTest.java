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

package org.slc.sli.modeling.docgen;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for Diagram class.
 */
public class DiagramTest {

    private static final String TITLE = "TITLE";
    private static final String SOURCE = "SOURCE";
    private static final String PROLOG = "PROLOGUE";
    private static final String EPILOG = "EPILOGUE";

    @Test
    public void test() {
        Diagram diagram = new Diagram(TITLE, SOURCE, PROLOG, EPILOG);

        assertEquals(TITLE, diagram.getTitle());
        assertEquals(SOURCE, diagram.getSource());
        assertEquals(PROLOG, diagram.getProlog());
        assertEquals(EPILOG, diagram.getEpilog());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new Diagram(null, SOURCE, PROLOG, EPILOG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new Diagram(TITLE, null, PROLOG, EPILOG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam3() {
        new Diagram(TITLE, SOURCE, null, EPILOG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam4() {
        new Diagram(TITLE, SOURCE, PROLOG, null);
    }

    @Test
    public void testToString() {

        // just check not null
        assertNotNull(new Diagram(TITLE, SOURCE, PROLOG, EPILOG).toString());


        // if you want to test toString() expected output, reenable this:

        /*

          String expectedResult = "{title : \"TITLE\", source : \"SOURCE\", prolog : \"PROLOGUE\", epilog : \"EPILOGUE\"}";
          String receivedResult = new Diagram(TITLE, SOURCE, PROLOG, EPILOG).toString();

          assertEquals(expectedResult, receivedResult);

          */
    }
}
