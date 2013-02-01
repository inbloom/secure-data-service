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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test for Documentation class.
 */
public class DocumentationTest {

    private enum MyType {
        TYPE_1,
        TYPE_2;
    }

    private static final List<Domain<MyType>> DOMAINS = new ArrayList<Domain<MyType>>();

    static {
        DOMAINS.add(new Domain<MyType>("title1", "description1", new ArrayList<Entity<MyType>>(), new ArrayList<Diagram>()));
        DOMAINS.add(new Domain<MyType>("title2", "description2", new ArrayList<Entity<MyType>>(), new ArrayList<Diagram>()));
    }

    private final Documentation<MyType> documentation = new Documentation<MyType>(DOMAINS);

    @Test
    public void testGetDomains() {
        assertEquals(DOMAINS, documentation.getDomains());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam() {
        new Documentation<MyType>(null);
    }

}
