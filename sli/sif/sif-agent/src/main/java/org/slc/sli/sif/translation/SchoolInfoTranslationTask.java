package org.slc.sli.sif.translation;

import java.util.ArrayList;

import openadk.library.student.SchoolInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.SchoolFocusConverter;
import org.slc.sli.sif.domain.slientity.Address;
import org.slc.sli.sif.domain.slientity.SchoolEntity;

public class SchoolInfoTranslationTask implements TranslationTask<SchoolInfo, SchoolEntity> {

    @Autowired
    AddressListConverter addressListConverter;

    @Autowired
    SchoolFocusConverter schoolFocusConverter;

    @Override
    public SchoolEntity translate(SchoolInfo schoolInfo) {

        SchoolEntity result = new SchoolEntity();
        result.setStateOrganizationId(schoolInfo.getStateProvinceId());
        result.setNameOfInstitution(schoolInfo.getSchoolName());
        result.setAddress(addressListConverter.convertTo(schoolInfo.getAddressList(), new ArrayList<Address>()));
        result.setSchoolType(schoolFocusConverter.convert(schoolInfo.getSchoolFocusList()));

        return result;
    }

}
