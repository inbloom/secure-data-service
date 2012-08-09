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

package org.slc.sli.sif.translation;

import java.util.Arrays;
import java.util.List;

import openadk.library.common.Demographics;
import openadk.library.student.MostRecent;
import openadk.library.student.StudentPersonal;

import org.mockito.Mock;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.EmailListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.slientity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Translation task for translating StudentPersonal SIF data objects to Student SLI entities.
 *
 * @author slee
 *
 */
public class StudentPersonalTranslationTask extends AbstractTranslationTask<StudentPersonal, StudentEntity> 
{
    @Autowired
    GradeLevelsConverter gradeLevelsConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    @Autowired
    EmailListConverter emailListConverter;

    @Autowired
    AddressListConverter addressListConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;


    public StudentPersonalTranslationTask()
    {
        super(StudentPersonal.class);
    }

    @Override
    public List<StudentEntity> doTranslate(StudentPersonal sifData)
    {
        StudentPersonal sp = sifData;
        MostRecent mostRecent = sp.getMostRecent();
        Demographics demographics = sp.getDemographics();
        StudentEntity e = new StudentEntity();
        //convert properties
        if (mostRecent!=null) {
            e.setGradeLevel(gradeLevelsConverter.convert(mostRecent.getGradeLevel()));
        }
        e.setElectronicMail(emailListConverter.convert(sp.getEmailList()));
        e.setAddress(addressListConverter.convert(sp.getAddressList()));
        e.setTelephone(phoneNumberListConverter.convert(sp.getPhoneNumberList()));

        return Arrays.asList(e);
    }

}
