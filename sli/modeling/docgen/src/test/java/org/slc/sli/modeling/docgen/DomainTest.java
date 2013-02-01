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
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test for Domain class.
 */
public class DomainTest {

    private static final String TITLE = "TITLE";
    private static final String DESCRIPTION = "DESCRIPTION";

    private enum DomainTestType {
        ENUM_VALUE_1,
        ENUM_VALUE_2;
    }

    private static final List<Diagram> DIAGRAMS = new ArrayList<Diagram>();

    static {
        DIAGRAMS.add(new Diagram("a", "b", "c", "d"));
        DIAGRAMS.add(new Diagram("e", "f", "g", "h"));
    }

    private static final List<Entity<DomainTestType>> ENTITIES = new ArrayList<Entity<DomainTestType>>();

    static {
        ENTITIES.add(new Entity<DomainTestType>("TITLE_1", DomainTestType.ENUM_VALUE_1, DIAGRAMS));
        ENTITIES.add(new Entity<DomainTestType>("TITLE_2", DomainTestType.ENUM_VALUE_2, DIAGRAMS));
    }

    private final Domain<DomainTestType> domain = new Domain<DomainTestType>(TITLE, DESCRIPTION, ENTITIES, DIAGRAMS);

    @Test
    public void testGetTitle() {
        assertEquals(TITLE, this.domain.getTitle());
    }

    @Test
    public void testGetDescription() {
        assertEquals(DESCRIPTION, this.domain.getDescription());
    }

    @Test
    public void testGetEntities() {
        assertEquals(ENTITIES, this.domain.getEntities());
    }

    @Test
    public void testToString() {

        //just test not null

        assertNotNull(domain.toString());

        // if interested in exact response, re-enable this:

        /*

          String expectedResult = "{title : \"TITLE\", description : \"DESCRIPTION\", entities : \"[{title : \"TITLE_1\", type : \"ENUM_VALUE_1\", diagrams : \"[{title : \"a\", source : \"b\", prolog : \"c\", epilog : \"d\"}, {title : \"e\", source : \"f\", prolog : \"g\", epilog : \"h\"}]\"}, {title : \"TITLE_2\", type : \"ENUM_VALUE_2\", diagrams : \"[{title : \"a\", source : \"b\", prolog : \"c\", epilog : \"d\"}, {title : \"e\", source : \"f\", prolog : \"g\", epilog : \"h\"}]\"}]\", diagrams : \"[{title : \"a\", source : \"b\", prolog : \"c\", epilog : \"d\"}, {title : \"e\", source : \"f\", prolog : \"g\", epilog : \"h\"}]\"}";
          String receivedResult = domain.toString();
          assertEquals(expectedResult, receivedResult);

          */
    }

    @Test
    public void testGetDiagrams() {
        assertEquals(DIAGRAMS, this.domain.getDiagrams());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new Domain<DomainTestType>(null, DESCRIPTION, ENTITIES, DIAGRAMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new Domain<DomainTestType>(TITLE, null, ENTITIES, DIAGRAMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam3() {
        new Domain<DomainTestType>(TITLE, DESCRIPTION, null, DIAGRAMS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam4() {
        new Domain<DomainTestType>(TITLE, DESCRIPTION, ENTITIES, null);
    }

}
