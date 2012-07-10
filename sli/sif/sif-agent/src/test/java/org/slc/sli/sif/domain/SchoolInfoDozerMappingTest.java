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

package org.slc.sli.sif.domain;

import junit.framework.Assert;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.sif.domain.sifentity.SchoolInfoEntity;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.uitl.SifEntityGenerator;

/**
 * JUnits for testing SchoolInfo Dozer Mapping.
 *
 * @author slee
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/applicationContext.xml" })
public class SchoolInfoDozerMappingTest
{
    @Autowired
    private Mapper dozerMapper;

    private SchoolInfoEntity schoolInfoEntity;

    @Before
    public void preMethodSetup() {
        schoolInfoEntity = new SchoolInfoEntity(SifEntityGenerator.generateTestSchoolInfo());
    }

    @Test
    public void testSchoolInfoMapping() {
        SchoolEntity schoolEntity = this.dozerMapper.map(schoolInfoEntity, SchoolEntity.class);
        Assert.assertEquals("Expecting 2 telephone numbers", 2, schoolEntity.getTelephone().size());
        Assert.assertEquals("Expecting 'Main' as the first phone type", "Main", schoolEntity.getTelephone().get(0).getInstitutionTelephoneNumberType());
        Assert.assertEquals("Expecting '(312) 555-1234' as the first phone number", "(312) 555-1234", schoolEntity.getTelephone().get(0).getTelephoneNumber());
        Assert.assertEquals("Expecting 'Main' as the first phone type", "Fax", schoolEntity.getTelephone().get(1).getInstitutionTelephoneNumberType());
        Assert.assertEquals("Expecting '(312) 555-2364' as the first phone number", "(312) 555-2364", schoolEntity.getTelephone().get(1).getTelephoneNumber());
    }

}
