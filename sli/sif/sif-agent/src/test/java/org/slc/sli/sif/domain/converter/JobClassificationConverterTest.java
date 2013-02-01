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

package org.slc.sli.sif.domain.converter;

import junit.framework.Assert;
import openadk.library.hrfin.JobClassification;
import openadk.library.hrfin.JobClassificationCode;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;

/**
 * Test class for EntryTypeConverter
 */
public class JobClassificationConverterTest extends AdkTest {

    private final JobClassificationConverter converter = new JobClassificationConverter();

    @Test
    public void testNullObject() {
        String result = converter.convert(null);
        Assert.assertNull("Entry Type should be null", result);
    }

    @Test
    public void testMappings() {
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.ATHLETIC_TRAINER)), "Athletic Trainer");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.INTERPRETER)), "Certified Interpreter");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.COUNSELOR)), "Counselor");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.TEACHING_CLASSROOM_AIDE)), "Instructional Aide");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.LIBRARIAN_MEDIA_CONSULTANT)), "Librarians/Media Specialists");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.PRINCIPAL_HEADMASTER_HEADMISTRESS)), "Principal");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.PHYSICAL_THERAPIST)), "Physical Therapist");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.RESPIRATORY_THERAPIST)), "Physical Therapist");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.TEACHER)), "Teacher");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.SUPERINTENDENT_COMMISSIONER)), "Superintendent");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.NURSE_PRACTITIONER)), "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.LICENSED_PRACTICAL_NURSE)), "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.REGISTERED_NURSE)), "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICIAL_ADMINISTRATIVE)), "School Administrator");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICE_CLERICAL_ADMINISTRATIVE)), "School Administrative Support Staff");
    }

    private JobClassification getJobClassification(JobClassificationCode code) {
        JobClassification jc = new JobClassification();
        jc.setCode(code);
        return jc;
    }

}
