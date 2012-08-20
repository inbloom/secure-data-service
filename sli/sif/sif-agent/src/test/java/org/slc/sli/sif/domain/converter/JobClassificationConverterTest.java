/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
        String result = converter.convert(null, null);
        Assert.assertNull("Entry Type should be null", result);
    }

    @Test
    public void testMappings() {
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.ATHLETIC_TRAINER), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Athletic Trainer");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.INTERPRETER), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Certified Interpreter");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.COUNSELOR), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Counselor");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.TEACHING_CLASSROOM_AIDE), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Instructional Aide");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.LIBRARIAN_MEDIA_CONSULTANT), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Librarians/Media Specialists");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.PRINCIPAL_HEADMASTER_HEADMISTRESS), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Principal");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.PHYSICAL_THERAPIST), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Physical Therapist");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.RESPIRATORY_THERAPIST), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Physical Therapist");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.TEACHER), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Teacher");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.SUPERINTENDENT_COMMISSIONER), JobClassificationConverter.EdOrgType.SCHOOL), 
                "Superintendent");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.NURSE_PRACTITIONER), JobClassificationConverter.EdOrgType.SCHOOL), 
                "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.LICENSED_PRACTICAL_NURSE), JobClassificationConverter.EdOrgType.SCHOOL), 
                "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.REGISTERED_NURSE), JobClassificationConverter.EdOrgType.SCHOOL), 
                "School Nurse");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICIAL_ADMINISTRATIVE), JobClassificationConverter.EdOrgType.SCHOOL), 
                "School Administrator");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICE_CLERICAL_ADMINISTRATIVE), JobClassificationConverter.EdOrgType.SCHOOL), 
                "School Administrative Support Staff");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICIAL_ADMINISTRATIVE), JobClassificationConverter.EdOrgType.LEA), 
                "LEA Administrator");
        Assert.assertEquals(converter.convert(getJobClassification(JobClassificationCode.OFFICE_CLERICAL_ADMINISTRATIVE), JobClassificationConverter.EdOrgType.LEA), 
                "LEA Administrative Support Staff");
    }

    private JobClassification getJobClassification(JobClassificationCode code) {
        JobClassification jc = new JobClassification();
        jc.setCode(code);
        return jc;
    }

}
