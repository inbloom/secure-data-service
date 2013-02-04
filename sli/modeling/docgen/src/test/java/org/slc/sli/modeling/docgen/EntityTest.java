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
 * JUnit test for Entity class.
 */
public class EntityTest {
    private static final String ENTITY_TITLE = "TITLE";

    private static List<Diagram> diagrams = new ArrayList<Diagram>();

    static {
        diagrams.add(new Diagram("a", "b", "c", "d"));
        diagrams.add(new Diagram("e", "f", "g", "h"));
        diagrams.add(new Diagram("i", "j", "k", "l"));
    }

    private enum EntityType {
        FOO,
        BAR;
    }

    private final Entity<EntityType> fooEntity = new Entity<EntityType>(ENTITY_TITLE, EntityType.FOO, diagrams);
    private final Entity<EntityType> barEntity = new Entity<EntityType>(ENTITY_TITLE, EntityType.BAR, diagrams);

    @Test
    public void testGetTitle() {
        assertEquals(ENTITY_TITLE, this.fooEntity.getTitle());
        assertEquals(ENTITY_TITLE, this.barEntity.getTitle());
    }

    @Test
    public void testGetType() {
        assertEquals(EntityType.FOO, this.fooEntity.getType());
        assertEquals(EntityType.BAR, this.barEntity.getType());
    }

    @Test
    public void testGetDiagrams() {
        assertEquals(diagrams, this.fooEntity.getDiagrams());
        assertEquals(diagrams, this.barEntity.getDiagrams());
    }

    @Test
    public void testToString() {

        // just testing that to string does not return null
        assertNotNull(this.fooEntity.toString());
        assertNotNull(this.barEntity.toString());

        // if you actually want to test the return use this:

        /*

          String expectedFooResult = "{title : \"TITLE\", type : \"FOO\", diagrams : \"[{title : \"a\", source : \"b\", prolog : \"c\", epilog : \"d\"}, {title : \"e\", source : \"f\", prolog : \"g\", epilog : \"h\"}, {title : \"i\", source : \"j\", prolog : \"k\", epilog : \"l\"}]\"}";
          String receivedFooResult = this.fooEntity.toString();
          String expectedBarResult = "{title : \"TITLE\", type : \"BAR\", diagrams : \"[{title : \"a\", source : \"b\", prolog : \"c\", epilog : \"d\"}, {title : \"e\", source : \"f\", prolog : \"g\", epilog : \"h\"}, {title : \"i\", source : \"j\", prolog : \"k\", epilog : \"l\"}]\"}";
          String receivedBarResult = this.barEntity.toString();

          assertEquals(expectedFooResult, receivedFooResult);
          assertEquals(expectedBarResult, receivedBarResult);

          */
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new Entity<EntityType>(null, EntityType.FOO, diagrams);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new Entity<EntityType>(ENTITY_TITLE, null, diagrams);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParam3() {
        new Entity<EntityType>(ENTITY_TITLE, EntityType.FOO, null);
    }

}
