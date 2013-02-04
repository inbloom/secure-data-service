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

package org.slc.sli.search.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author jstokes
 */
public class DotPathTest {

    private List<String> dotPathList;

    @Before
    public void setup() {
        this.dotPathList = new ArrayList<String>();
    }

    @Test
    public void testClone() throws Exception {
        final DotPath dp = new DotPath();
        dp.add("foo");

        final DotPath cloned = dp.clone();
        assertTrue(cloned.get(0).equals("foo"));
    }

    @Test
    public void testFrom() throws Exception {
        final DotPath dp = DotPath.from(Arrays.asList("foo"));
        assertTrue(dp.get(0).contains("foo"));
    }

    @Test
    public void testTo() throws Exception {
        final DotPath dp = new DotPath("foo.bar");
        assertTrue(DotPath.to(dp).contains("foo"));
        assertTrue(DotPath.to(dp).contains("bar"));
    }

    @Test
    public void testSize() throws Exception {
        final DotPath dp = new DotPath("foo.bar");
        assertEquals(2, dp.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        final DotPath dp = new DotPath();
        assertTrue(dp.isEmpty());
    }

    @Test
    public void testRemoveAll() throws Exception {
        final DotPath dp = new DotPath("foo.bar.baz");
        final List<String> toRemove = Arrays.asList("foo", "bar", "baz");
        final boolean removed = dp.removeAll(toRemove);
        assertTrue(removed);
        assertEquals(0, dp.size());
    }

    @Test
    public void testEquals() throws Exception {
        final List<String> pathList = Arrays.asList("foo", "bar", "baz");
        assertEquals(DotPath.from(pathList), new DotPath("foo.bar.baz"));
    }

    @Test
    public void testHashCode() throws Exception {
        final List<String> pathList = Arrays.asList("foo", "bar", "baz");
        assertEquals(DotPath.from(pathList).hashCode(), new DotPath("foo.bar.baz").hashCode());
    }

    @Test
    public void testGet() throws Exception {
        final DotPath dp = new DotPath("foo.bar.baz");
        assertEquals("foo", dp.get(0));
        assertEquals("bar", dp.get(1));
    }

    @Test
    public void testAdd() throws Exception {
        final DotPath dp = new DotPath();
        dp.add("foo");
        assertEquals("foo", dp.get(0));
    }

    @Test
    public void testRemove() throws Exception {
        final DotPath dp = new DotPath("foo.bar.baz");
        dp.remove("foo");
        List<String> removed = DotPath.to(dp);
        assertFalse(removed.contains("foo"));
    }
}
