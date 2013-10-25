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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
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
        Assert.assertTrue(EntityDateHelper.isBeforeOrEqualYear("2007-2008", 2009));
        Assert.assertTrue(EntityDateHelper.isBeforeOrEqualYear("2008-2009", 2009));
        Assert.assertFalse(EntityDateHelper.isBeforeOrEqualYear("2009-2010", 2009));
        Assert.assertFalse(EntityDateHelper.isBeforeOrEqualYear("2010-2011", 2009));
    }

    @Test
    public void testshouldExtractSchoolYear() {
        Map<String, Object> gradeBody = new HashMap<String, Object>();
        gradeBody.put(ParameterConstants.SCHOOL_YEAR, "2009-2010");
        Entity grade = new MongoEntity(EntityNames.GRADE, gradeBody);

        Assert.assertTrue(EntityDateHelper.shouldExtract(grade, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(EntityDateHelper.shouldExtract(grade, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(EntityDateHelper.shouldExtract(grade, null));
        Assert.assertFalse(EntityDateHelper.shouldExtract(grade, DateTime.parse("2009-05-24", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(EntityDateHelper.shouldExtract(grade, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testshouldExtractDateAndType() {
        String attendanceDate = "2009-2010";

        Assert.assertTrue(EntityDateHelper.shouldExtract(attendanceDate, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()),
                EntityNames.ATTENDANCE));
        Assert.assertTrue(EntityDateHelper.shouldExtract(attendanceDate, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat()),
                EntityNames.ATTENDANCE));
        Assert.assertTrue(EntityDateHelper.shouldExtract(attendanceDate, null, EntityNames.ATTENDANCE));
        Assert.assertFalse(EntityDateHelper.shouldExtract(attendanceDate, DateTime.parse("2009-05-24", DateHelper.getDateTimeFormat()),
                EntityNames.ATTENDANCE));
        Assert.assertFalse(EntityDateHelper.shouldExtract(attendanceDate, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat()),
                EntityNames.ATTENDANCE));
    }

    @Test
    public void testSholdExtractEntities() {
        List<Entity> entities = new ArrayList<Entity>();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2001-01-01");
        Entity entity1 = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Map<String, Object> body2 = new HashMap<String, Object>();
        body2.put(ParameterConstants.BEGIN_DATE, "2009-01-01");
        Entity entity2 = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        entities.add(entity1);
        entities.add(entity2);

        Assert.assertTrue(EntityDateHelper.shouldExtract(entities, DateTime.parse("2001-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(EntityDateHelper.shouldExtract(entities, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(EntityDateHelper.shouldExtract(entities, DateTime.parse("2000-05-23", DateHelper.getDateTimeFormat())));
    }

    @SuppressWarnings("static-access")
    @Test
    public void testshouldExtractTeacherSchoolAssociation() {
        EntityDateHelper helper = new EntityDateHelper();

        Entity mSeoa = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2009-09-02");
        body.put(ParameterConstants.END_DATE, "2010-05-31");
        Mockito.when(mSeoa.getBody()).thenReturn(body);
        Mockito.when(mSeoa.getType()).thenReturn(EntityNames.STAFF_ED_ORG_ASSOCIATION);

        EdOrgExtractHelper mockEdOrgExtractHelper = Mockito.mock(EdOrgExtractHelper.class);
        Mockito.when(mockEdOrgExtractHelper.retrieveSEOAS(Matchers.eq("Staff1"), Matchers.eq("LEA"))).thenReturn(Arrays.asList(mSeoa));
        helper.setEdOrgExtractHelper(mockEdOrgExtractHelper);

        Map<String, Object> tsaBody = new HashMap<String, Object>();
        tsaBody.put(ParameterConstants.TEACHER_ID, "Staff1");
        tsaBody.put(ParameterConstants.SCHOOL_ID, "LEA");
        Entity tsa = Mockito.mock(Entity.class);
        Mockito.when(tsa.getBody()).thenReturn(tsaBody);
        Mockito.when(tsa.getType()).thenReturn(EntityNames.TEACHER_SCHOOL_ASSOCIATION);

        Assert.assertTrue(helper.shouldExtract(tsa, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(helper.shouldExtract(tsa, DateTime.parse("2009-09-02", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(helper.shouldExtract(tsa, null));
        Assert.assertFalse(helper.shouldExtract(tsa, DateTime.parse("2009-09-01", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(helper.shouldExtract(tsa, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat())));
    }

}
