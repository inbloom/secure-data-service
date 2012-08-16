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
import openadk.library.common.AddressList;
import openadk.library.common.PhoneNumberList;
import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;

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
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.LEAEntity;

/**
 *
 * LEAInfoTranslationTask unit tests
 *
 */
public class LEAInfoTranslationTaskTest {
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
    public void testBasicFields() throws SifTranslationException {
        LEAInfo info = new LEAInfo();

        String stateOrgId = "stateOrgId";
        info.setStateProvinceId(stateOrgId);

        String name = "LEAName";
        info.setLEAName(name);

        String url = "LEAURL";
        info.setLEAURL(url);

        List<LEAEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        LEAEntity entity = result.get(0);

        Assert.assertEquals(stateOrgId, entity.getStateOrganizationId());
        Assert.assertEquals(name, entity.getNameOfInstitution());
        Assert.assertEquals(url, entity.getWebSite());
    }

     @Test
    public void testAddressList() throws SifTranslationException {
        AddressList addressList = new AddressList();
        LEAInfo info = new LEAInfo();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convert(Mockito.eq(addressList))).thenReturn(
                address);

        List<LEAEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        LEAEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convert(Mockito.eq(addressList));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testOperationalStatus() throws SifTranslationException {
        LEAInfo info = new LEAInfo();
        info.setOperationalStatus(OperationalStatus.AGENCY_CLOSED);
        Mockito.when(operationalStatusConverter.convert(OperationalStatus.wrap(info.getOperationalStatus()))).thenReturn("Closed");
        List<LEAEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        LEAEntity entity = result.get(0);

        Mockito.verify(operationalStatusConverter).convert(OperationalStatus.wrap(info.getOperationalStatus()));
        Assert.assertEquals("Closed", entity.getOperationalStatus());
    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        LEAInfo info = new LEAInfo();
        info.setPhoneNumberList(phoneNumberList);

        List<InstitutionTelephone> telephones = new ArrayList<InstitutionTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convertInstitutionTelephone(phoneNumberList)).thenReturn(telephones);

        List<LEAEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        LEAEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convertInstitutionTelephone(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }

}
