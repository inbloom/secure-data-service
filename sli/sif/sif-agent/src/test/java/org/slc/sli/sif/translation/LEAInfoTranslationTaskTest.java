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

import java.util.ArrayList;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;
import openadk.library.student.LEAInfo;
import openadk.library.student.SchoolInfo;
import openadk.library.student.Title1Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.OperationalStatusConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.LEAEntity;

public class LEAInfoTranslationTaskTest
{
    @InjectMocks
    private final LEAInfoTranslationTask translator = new LEAInfoTranslationTask();

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    OperationalStatusConverter operationalStatusConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;

    @Before
    public void beforeTests() {
        try {
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<LEAEntity> result = translator.translate(new LEAInfo());
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        LEAInfo info = new LEAInfo();
        info.setPhoneNumberList(phoneNumberList);

        List<InstitutionTelephone> telephones = new ArrayList<InstitutionTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convert(phoneNumberList)).thenReturn(telephones);

        List<LEAEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        LEAEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convert(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }


}
