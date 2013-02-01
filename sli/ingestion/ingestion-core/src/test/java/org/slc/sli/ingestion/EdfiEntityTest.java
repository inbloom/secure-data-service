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


package org.slc.sli.ingestion;

import java.util.EnumSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dkornishev
 *
 */
public class EdfiEntityTest {

    private static final Logger LOG = LoggerFactory.getLogger(EdfiEntityTest.class);

    @Test
    public void testSmallSubset() {
        Set<EdfiEntity> expected = EnumSet.of(EdfiEntity.STUDENT, EdfiEntity.PARENT);
        Set<EdfiEntity> actual = EdfiEntity.cleanse(EnumSet.of(EdfiEntity.STUDENT, EdfiEntity.STUDENT_ACADEMIC_RECORD, EdfiEntity.PARENT, EdfiEntity.STUDENT_PARENT_ASSOCIATION));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAll() {
        Set<EdfiEntity> expected = EnumSet.of(EdfiEntity.SELF, EdfiEntity.ASSESSMENT_FAMILY, EdfiEntity.ASSESSMENT_PERIOD_DESCRIPTOR, EdfiEntity.CALENDAR_DATE, EdfiEntity.CLASS_PERIOD,
                EdfiEntity.LEARNING_STANDARD, EdfiEntity.LOCATION, EdfiEntity.PARENT, EdfiEntity.PROGRAM, EdfiEntity.STAFF, EdfiEntity.STUDENT, EdfiEntity.TEACHER, EdfiEntity.BELL_SCHEDULE, EdfiEntity.COMPETENCY_LEVEL_DESCRIPTOR,
                EdfiEntity.CREDENTIAL_FIELD_DESCRIPTOR, EdfiEntity.PERFORMANCE_LEVEL_DESCRIPTOR, EdfiEntity.SERVICE_DESCRIPTOR);

        Set<EdfiEntity> actual = EdfiEntity.cleanse(EnumSet.allOf(EdfiEntity.class));


        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNoSelf() {
        Assert.assertFalse("SELF must be resolved!", EdfiEntity.LEARNING_OBJECTIVE.getNeededEntities().contains(EdfiEntity.SELF));
        Assert.assertTrue("SELF must be resolved!", EdfiEntity.LEARNING_OBJECTIVE.getNeededEntities().contains(EdfiEntity.LEARNING_OBJECTIVE));
    }

    @Test
    public void testDeps() {
        EnumSet<EdfiEntity> expected = EnumSet.of(EdfiEntity.SECTION, EdfiEntity.STUDENT_ACADEMIC_RECORD, EdfiEntity.STUDENT_PARENT_ASSOCIATION);
        Set<EdfiEntity> actual = EdfiEntity.cleanse(expected);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testLayersFinish() {
        Set<EdfiEntity> set = EnumSet.allOf(EdfiEntity.class);

        for (int i = 1; i < 100; i++) {
            Set<EdfiEntity> cleansed = EdfiEntity.cleanse(set);
            set.removeAll(cleansed);
            LOG.info(cleansed.toString());
            if (set.isEmpty()) {
                LOG.info("Dependency diminuation finished in: {} passes", i);
                break;
            }
        }
    }
}
