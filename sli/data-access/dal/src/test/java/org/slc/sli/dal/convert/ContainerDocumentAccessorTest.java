/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates
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

package org.slc.sli.dal.convert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.slc.sli.domain.MongoEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ContainerDocumentAccessorTest {

    @InjectMocks
    private ContainerDocumentAccessor testAccessor;

    @Mock
    private ContainerDocumentHolder mockHolder;

    @Before
    public void setup() {
        testAccessor = new ContainerDocumentAccessor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsContainerDocument() {
        when(mockHolder.isContainerDocument("attendance")).thenReturn(true);

        boolean actual = testAccessor.isContainerDocument("attendance");
        assertEquals(true, actual);
    }

    @Test
    public void testCreateParentKey() {
        Map<String,String> parentKeyMap = new HashMap<String, String>();
         parentKeyMap.put("studentId","studentId");
        parentKeyMap.put("schoolId","studentId");
                parentKeyMap.put("schoolYear","studentId");
        final ContainerDocument attendance = ContainerDocument.builder().forCollection("testCollection")
                .forField("array_field")
                .withParent(parentKeyMap).build();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId","student1");
        body.put("schoolId","school1");
        body.put("schoolYear","schoolyear1");
        MongoEntity entity = new MongoEntity("attendance", body);

        when(mockHolder.getContainerDocument(Mockito.anyString())).thenReturn(attendance);
        String parentUUid = testAccessor.createParentKey(entity);
        System.out.print(parentUUid);
        assertFalse(parentUUid.isEmpty());
    }
}
