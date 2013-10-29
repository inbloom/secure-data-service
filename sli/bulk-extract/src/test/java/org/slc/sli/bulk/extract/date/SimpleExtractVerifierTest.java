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
public class SimpleExtractVerifierTest {

    private SimpleExtractVerifier simpleExtractVerifier = new SimpleExtractVerifier();

    @Test
    public void testShouldExtrct() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "01-01-01");
        Entity studentProgramAssociation = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Assert.assertTrue( "01-01-01",simpleExtractVerifier.shouldExtract(studentProgramAssociation, DateTime.parse("2000-01-01", DateHelper.getDateTimeFormat())));
        Assert.assertTrue( "01-01-01",simpleExtractVerifier.shouldExtract(studentProgramAssociation, DateTime.parse("1999-01-01", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testisBeforeOrEqualYear() {
        Assert.assertTrue(simpleExtractVerifier.isBeforeOrEqualYear("2007-2008", 2009));
        Assert.assertTrue(simpleExtractVerifier.isBeforeOrEqualYear("2008-2009", 2009));
        Assert.assertFalse(simpleExtractVerifier.isBeforeOrEqualYear("2009-2010", 2009));
        Assert.assertFalse(simpleExtractVerifier.isBeforeOrEqualYear("2010-2011", 2009));
    }

    @Test
    public void testshouldExtractSchoolYear() {
        Map<String, Object> gradeBody = new HashMap<String, Object>();
        gradeBody.put(ParameterConstants.SCHOOL_YEAR, "2009-2010");
        Entity grade = new MongoEntity(EntityNames.GRADE, gradeBody);

        Assert.assertTrue(simpleExtractVerifier.shouldExtract(grade, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(simpleExtractVerifier.shouldExtract(grade, DateTime.parse("2010-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertTrue(simpleExtractVerifier.shouldExtract(grade, null));
        Assert.assertFalse(simpleExtractVerifier.shouldExtract(grade, DateTime.parse("2009-05-24", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(simpleExtractVerifier.shouldExtract(grade, DateTime.parse("2008-05-24", DateHelper.getDateTimeFormat())));
    }

    @Test
    public void testshouldExtractBeginDate() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.BEGIN_DATE, "2001-01-01");
        Entity studentProgramAssociation = new MongoEntity(EntityNames.STUDENT_PROGRAM_ASSOCIATION, body);

        Assert.assertEquals(true, simpleExtractVerifier.shouldExtract(studentProgramAssociation, DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat())));
        Assert.assertEquals(true, simpleExtractVerifier.shouldExtract(studentProgramAssociation, null));
        Assert.assertEquals(false, simpleExtractVerifier.shouldExtract(studentProgramAssociation, DateTime.parse("2000-01-01", DateHelper.getDateTimeFormat())));
    }

}
