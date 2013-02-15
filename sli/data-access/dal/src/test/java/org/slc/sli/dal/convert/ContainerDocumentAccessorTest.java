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

package org.slc.sli.dal.convert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slc.sli.common.domain.ContainerDocument;
import org.slc.sli.common.domain.ContainerDocumentHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
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
        final ContainerDocument attendance = ContainerDocument.builder().forCollection("testCollection")
                .forField("array_field")
                .withParent(Collections.EMPTY_MAP).build();
    }
}
