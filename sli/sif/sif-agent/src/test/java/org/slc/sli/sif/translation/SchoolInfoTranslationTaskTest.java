package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.AddressList;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

public class SchoolInfoTranslationTaskTest {

    @InjectMocks
    private final SchoolInfoTranslationTask translator = new SchoolInfoTranslationTask();

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    SchoolFocusConverter mockSchoolFocusConverter;

    @Before
    public void beforeTests(){
        try {
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull(){
        List<SchoolEntity> result = translator.translate(new SchoolInfo());
        Assert.assertNotNull("Result was null",result);
    }

    @Test
    public void testBasicFields(){
        SchoolInfo info = new SchoolInfo();

        String stateOrgId = "stateOrgId";
        info.setStateProvinceId(stateOrgId);

        String schoolName = "schoolName";
        info.setSchoolName(schoolName);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Assert.assertEquals(stateOrgId, entity.getStateOrganizationId());
        Assert.assertEquals(schoolName, entity.getNameOfInstitution());
    }

    @Test
    public void testAddressList(){

        AddressList addressList = new AddressList();
        SchoolInfo info = new SchoolInfo();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convertTo(Mockito.eq(addressList), Mockito.any(List.class))).thenReturn(address);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convertTo(Mockito.eq(addressList), Mockito.any(List.class));
        Assert.assertEquals(address, entity.getAddress());

    }

    @Test
    public void testSchoolFocus(){
        SchoolFocusList focusList = new SchoolFocusList();
        SchoolInfo info = new SchoolInfo();
        info.setSchoolFocusList(focusList);

        Mockito.when(mockSchoolFocusConverter.convert(focusList)).thenReturn("schoolType");

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockSchoolFocusConverter).convert(Mockito.eq(focusList));
        Assert.assertEquals("schoolType", entity.getSchoolType());
    }







}
