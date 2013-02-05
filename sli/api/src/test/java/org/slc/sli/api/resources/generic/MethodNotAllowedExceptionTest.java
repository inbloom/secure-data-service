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
package org.slc.sli.api.resources.generic;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 *
 * Unit tests
 *
 */

public class MethodNotAllowedExceptionTest {

    private MethodNotAllowedException exception;

    @Before
    public void setup() {
        Set<String> methods = new HashSet<String>();
        methods.add("GET");
        methods.add("POST");

        exception = new MethodNotAllowedException(methods);
    }

    @Test
    public void testExceptionMethods() {
        assertEquals("Should match", 2, exception.getAllowedMethods().size());
        assertTrue("Should be true", exception.getAllowedMethods().contains("GET"));
        assertTrue("Should be true", exception.getAllowedMethods().contains("POST"));
    }
}
