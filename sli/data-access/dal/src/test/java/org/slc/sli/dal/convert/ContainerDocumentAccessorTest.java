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
import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.MongoEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private static final String ATTENDANCE = "attendance";

    @InjectMocks
    private ContainerDocumentAccessor testAccessor;

    @Mock
    private ContainerDocumentHolder mockHolder;

    @Mock
    private UUIDGeneratorStrategy generatorStrategy;

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        testAccessor = new ContainerDocumentAccessor(generatorStrategy, mongoTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsContainerDocument() {
        when(mockHolder.isContainerDocument(ATTENDANCE)).thenReturn(true);

        boolean actual = testAccessor.isContainerDocument(ATTENDANCE);
        assertEquals(true, actual);
    }

    @Test
    public void testCreateParentKey() {
        final ContainerDocument attendance = createAttendanceContainer();
        final MongoEntity entity = createAttendanceEntity();
        final NaturalKeyDescriptor stubKeyDescriptor =
                ContainerDocumentHelper.extractNaturalKeyDescriptor(entity, attendance.getParentNaturalKeys());

        when(mockHolder.getContainerDocument(ATTENDANCE)).thenReturn(attendance);
        when(generatorStrategy.generateId(stubKeyDescriptor)).thenReturn("abc-123");

        final String parentUUID = testAccessor.createParentKey(entity);
        final String expected = "abc-123";

        assertFalse(parentUUID == null);
        assertFalse(parentUUID.isEmpty());
        assertEquals(expected, parentUUID);
    }

    //@Test
    public void testCreateEntityBody() {

    }

    @Test
    public void testInsert() {

    }

    private ContainerDocument createAttendanceContainer() {
        return ContainerDocument.builder().forCollection("testCollection")
                .forField("array_field")
                .withParent(createParentKeyMap()).build();
    }

    private List<String> createParentKeyMap() {
        return Arrays.asList("studentId", "schoolId", "schoolYear");
    }

    private MongoEntity createAttendanceEntity() {
        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", "student1");
        body.put("schoolId", "school1");
        body.put("schoolYear", "schoolyear1");
        return new MongoEntity(ATTENDANCE, body);
    }
}
