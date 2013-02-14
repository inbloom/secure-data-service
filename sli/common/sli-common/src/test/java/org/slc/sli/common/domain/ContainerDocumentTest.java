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

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * ContainerDocument Tester.
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
public class ContainerDocumentTest {

    @Test
    public void getContainerDocument() {
        final Map<String, String> parentMap = new HashMap<String, String>();
        parentMap.put("k1", "v1");
        final ContainerDocument testDocument = new ContainerDocument.ContainerDocumentBuilder()
                .forCollection("testCollection")
                .forField("testField")
                .withParent(parentMap).build();

        assertEquals("testCollection", testDocument.getCollectionName());
        assertEquals("testField", testDocument.getFieldToPersist());
        assertEquals("v1", testDocument.getParentNaturalKeyMap().get("k1"));
    }
}
