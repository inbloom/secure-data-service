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

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * @author ablum
 */
public class EntityDateHelperTest {

    @Test
    public void testRetrieveDate() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "01-01-01");
        Entity studentProgramAssociation = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Assert.assertEquals("01-01-01", EntityDateHelper.retrieveDate(studentProgramAssociation));
    }

    @Test
    public void testIsPastOrCurrentDateWithDate() {
        Assert.assertTrue(EntityDateHelper.isPastOrCurrentDate("2010-05-23", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.DISCIPLINE_INCIDENT));
        Assert.assertTrue(EntityDateHelper.isPastOrCurrentDate("2011-05-23", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.DISCIPLINE_INCIDENT));
        Assert.assertFalse(EntityDateHelper.isPastOrCurrentDate("2012-05-23", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.DISCIPLINE_INCIDENT));
    }

    @Test
    public void testIsPastOrCurrentDateWithYear() {
        Assert.assertTrue(EntityDateHelper.isPastOrCurrentDate("2009-2010", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.GRADE));
        Assert.assertTrue(EntityDateHelper.isPastOrCurrentDate("2010-2011", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.GRADE));
        Assert.assertFalse(EntityDateHelper.isPastOrCurrentDate("2011-2012", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.GRADE));
        Assert.assertFalse(EntityDateHelper.isPastOrCurrentDate("2012-2013", DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat()), EntityNames.GRADE));
    }

}
