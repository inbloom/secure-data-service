package org.slc.sli.sif.translation;

import java.util.ArrayList;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.common.AddressList;
import openadk.library.common.GradeLevels;
import openadk.library.common.PhoneNumberList;
import openadk.library.student.SchoolFocusList;
import openadk.library.student.SchoolInfo;
import openadk.library.student.SchoolLevelType;
import openadk.library.student.Title1Status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.GradeLevelsConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.converter.SchoolTypeConverter;
import org.slc.sli.sif.domain.converter.TitleIPartASchoolDesignationConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;
import org.slc.sli.sif.domain.slientity.SchoolEntity;
import org.slc.sli.sif.domain.slientity.TitleIPartASchoolDesignation;

public class SchoolInfoTranslationTaskTest {

    @InjectMocks
    private final SchoolInfoTranslationTask translator = new SchoolInfoTranslationTask();

    @Mock
    AddressListConverter mockAddressConverter;

    @Mock
    SchoolFocusConverter mockSchoolFocusConverter;

    @Mock
    GradeLevelsConverter mockGradeLevelsConverter;

    @Mock
    SchoolTypeConverter mockSchoolTypeConverter;

    @Mock
    TitleIPartASchoolDesignationConverter mockTitleIPartASchoolDesignationConverter;

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
    public void testNotNull() throws SifTranslationException{
        List<SchoolEntity> result = translator.translate(new SchoolInfo());
        Assert.assertNotNull("Result was null", result);
    }

    // @Test
    public void testBasicFields() throws SifTranslationException{
        SchoolInfo info = new SchoolInfo();

        String stateOrgId = "stateOrgId";
        info.setStateProvinceId(stateOrgId);

        String schoolName = "schoolName";
        info.setSchoolName(schoolName);

        String schoolUrl = "schoolUrl";
        info.setSchoolURL(schoolUrl);

        info.setTitle1Status(Title1Status.SCHOOLWIDE);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Assert.assertEquals(stateOrgId, entity.getStateOrganizationId());
        Assert.assertEquals(schoolName, entity.getNameOfInstitution());
        Assert.assertEquals(schoolUrl, entity.getWebSite());
        Assert.assertEquals("School", entity.getOrganizationCategories().get(0));
        Assert.assertEquals(schoolUrl, entity.getWebSite());

    }

    // @Test
    public void testAddressList() throws SifTranslationException{

        AddressList addressList = new AddressList();
        SchoolInfo info = new SchoolInfo();
        info.setAddressList(addressList);

        List<Address> address = new ArrayList<Address>();

        Mockito.when(mockAddressConverter.convertTo(Mockito.eq(addressList), Mockito.any(List.class))).thenReturn(
                address);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockAddressConverter).convertTo(Mockito.eq(addressList), Mockito.any(List.class));
        Assert.assertEquals(address, entity.getAddress());

    }

    //@Test
    public void testSchoolFocus() throws SifTranslationException{
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

    @Test
    public void testGradeLevels() throws SifTranslationException {
        GradeLevels sifGradeLevels = new GradeLevels();
        SchoolInfo info = new SchoolInfo();
        info.setGradeLevels(sifGradeLevels);

        List<String> convertedGrades = new ArrayList<String>();

        Mockito.when(mockGradeLevelsConverter.convert(sifGradeLevels)).thenReturn(convertedGrades);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockGradeLevelsConverter).convert(Mockito.eq(sifGradeLevels));
        Assert.assertEquals(convertedGrades, entity.getGradesOffered());
    }

    @Test
    public void testSchoolTypes() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();
        info.setSchoolType(SchoolLevelType.ELEMENTARY);

        List<String> schoolTypes = new ArrayList<String>();

        Mockito.when(mockSchoolTypeConverter.convert(SchoolLevelType.ELEMENTARY.getValue())).thenReturn(schoolTypes);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockSchoolTypeConverter).convert(SchoolLevelType.ELEMENTARY.getValue());
        Assert.assertEquals(schoolTypes, entity.getSchoolCategories());
    }

    @Test
    public void testPhoneNumbers() throws SifTranslationException {
        PhoneNumberList phoneNumberList = new PhoneNumberList();
        SchoolInfo info = new SchoolInfo();
        info.setPhoneNumberList(phoneNumberList);

        List<InstitutionTelephone> telephones = new ArrayList<InstitutionTelephone>();

        Mockito.when(mockPhoneNumberListConverter.convert(phoneNumberList)).thenReturn(telephones);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockPhoneNumberListConverter).convert(phoneNumberList);
        Assert.assertEquals(telephones, entity.getTelephone());
    }

    @Test
    public void testTitleIDesignation() throws SifTranslationException {
        SchoolInfo info = new SchoolInfo();
        info.setTitle1Status(Title1Status.SCHOOLWIDE);

        Mockito.when(mockTitleIPartASchoolDesignationConverter.convert(Title1Status.SCHOOLWIDE)).thenReturn(
                TitleIPartASchoolDesignation.PART_A_SCHOOLWIDE);

        List<SchoolEntity> result = translator.translate(info);
        Assert.assertEquals(1, result.size());
        SchoolEntity entity = result.get(0);

        Mockito.verify(mockTitleIPartASchoolDesignationConverter).convert(Title1Status.SCHOOLWIDE);
        Assert.assertEquals(TitleIPartASchoolDesignation.PART_A_SCHOOLWIDE.getText(), entity.getSchoolType());
    }

}
