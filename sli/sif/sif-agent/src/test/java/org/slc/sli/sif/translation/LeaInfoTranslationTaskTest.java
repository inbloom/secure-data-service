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

package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

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

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.OperationalStatusConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.LeaEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 *
 * LEAInfoTranslationTask unit tests
 *
 */
public class LeaInfoTranslationTaskTest extends AdkTest {
    @InjectMocks
    private final LeaInfoTranslationTask translator = new LeaInfoTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    OperationalStatusConverter operationalStatusConverter;

    @Mock
    PhoneNumberListConverter mockPhoneNumberListConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<LeaEntity> result = translator.translate(new LEAInfo(), null);
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testBasicFields() throws SifTranslationException {
        LEAInfo info = new LEAInfo();

        String seaGuid = "SLI_SEAGUID";
        String zoneId = "zoneId";
        Mockito.when(mockSifIdResolver.getZoneSea(zoneId)).thenReturn(seaGuid);

        String leaOrgId = "leaOrgId";
        info.setStateProvinceId(leaOrgId);

        String name = "LEAName";
        info.setLEAName(name);

        String url = "LEAURL";
        info.setLEAURL(url);

        List<LeaEntity> result = translator.translate(info, zoneId);
        Assert.assertEquals(1, result.size());
        LeaEntity entity = result.get(0);

        Assert.assertEquals(leaOrgId, entity.getStateOrganizationId());
        Assert.assertEquals(name, entity.getNameOfInstitution());
        Assert.assertEquals(url, entity.getWebSite());

        Assert.assertEquals(seaGuid, entity.getParentEducationAgencyReference());
    }

    @Test
    public void testAddressList() throws SifTranslationException {
        AddressList addressList = new AddressList();
        LEAInfo info = new LEAInfo();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convert(Mockito.eq(addressList))).thenReturn(address);

        List<LeaEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        LeaEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convert(Mockito.eq(addressList));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testOperationalStatus() throws SifTranslationException {
        LEAInfo info = new LEAInfo();
        info.setOperationalStatus(OperationalStatus.AGENCY_CLOSED);
        Mockito.when(operationalStatusConverter.convert(OperationalStatus.wrap(info.getOperationalStatus())))
                .thenReturn("Closed");
        List<LeaEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        LeaEntity entity = result.get(0);

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

        List<LeaEntity> result = translator.translate(info, null);
        Assert.assertEquals(1, result.size());
        LeaEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convertInstitutionTelephone(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }

}
