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
package org.slc.sli.bulk.extract.date;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * @author ablum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class EntityDateHelperTest {

    @Test
    public void testRetrieve() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "01-01-01");
        Entity studentProgramAssociation = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Assert.assertEquals("01-01-01", EntityDateHelper.retrieveDate(studentProgramAssociation));
    }

    @Test
    public void testIsBeforeOrEqualDate() {
        Assert.assertEquals(true, EntityDateHelper.isBeforeOrEqualDate("2001-01-01", DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat())));
        Assert.assertEquals(true, EntityDateHelper.isBeforeOrEqualDate("2000-01-01", DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat())));
        Assert.assertEquals(false, EntityDateHelper.isBeforeOrEqualDate("2002-01-01", DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testshouldExtractBeginDate() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2001-01-01");
        Entity studentProgramAssociation = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Assert.assertEquals(true, EntityDateHelper.shouldExtract(studentProgramAssociation, DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat())));
        Assert.assertEquals(true, EntityDateHelper.shouldExtract(studentProgramAssociation, null));
        Assert.assertEquals(false, EntityDateHelper.shouldExtract(studentProgramAssociation, DateTime.parse("2000-01-01", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testisBeforeOrEqualYear() {
        Assert.assertTrue(EntityDateHelper.isBeforeOrEqualYear("2007-2008", "2009"));
        Assert.assertTrue(EntityDateHelper.isBeforeOrEqualYear("2008-2009", "2009"));
        Assert.assertFalse(EntityDateHelper.isBeforeOrEqualYear("2009-2010", "2009"));
    }

    @Test
    public void testshouldExtractSchoolYear() {
        Map<String, Object> attendanceBody = new HashMap<String, Object>();
        attendanceBody.put(ParameterConstants.SCHOOL_YEAR, "2009-2010");
        Entity attendance = new MongoEntity(EntityNames.ATTENDANCE, attendanceBody);

        Assert.assertTrue(EntityDateHelper.shouldExtract(attendance, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(EntityDateHelper.shouldExtract(attendance, null));
        Assert.assertFalse(EntityDateHelper.shouldExtract(attendance, DateTime.parse("2009-05-24", DateHelper.getDateTimeFormat())));
    }

}
