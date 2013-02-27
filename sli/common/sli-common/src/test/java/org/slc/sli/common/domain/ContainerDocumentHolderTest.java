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

package org.slc.sli.common.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class ContainerDocumentHolderTest {

    private ContainerDocumentHolder testHolder;

    @Test
    public void testGetContainerDocument() {
        final Map<String, ContainerDocument> testContainer = createContainerMap();
        testHolder = new ContainerDocumentHolder(testContainer);
        assertEquals(testContainer.get("test"), testHolder.getContainerDocument("test"));
    }

    @Test
    public void testIsContainerDocument() {
        final Map<String, ContainerDocument> testContainer = createContainerMap();
        testHolder = new ContainerDocumentHolder(testContainer);
        assertFalse(testHolder.isContainerDocument("foo"));
        assertTrue(testHolder.isContainerDocument("test"));
    }

    private Map<String, ContainerDocument> createContainerMap() {
        final Map<String, ContainerDocument> testContainer = new HashMap<String, ContainerDocument>();
        final List<String> parentKeys = Arrays.asList("studentId", "schoolId", "schoolYear");
        final ContainerDocument attendance = ContainerDocument.builder().forCollection("attendance")
                .forField("attendanceEvent")
                .withParent(parentKeys).build();
        testContainer.put("test", attendance);
        return testContainer;
    }
}
