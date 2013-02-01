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

import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.converter.AddressListConverter;
import org.slc.sli.sif.domain.converter.OperationalStatusConverter;
import org.slc.sli.sif.domain.converter.PhoneNumberListConverter;
import org.slc.sli.sif.domain.slientity.LeaEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * An implementation for translation of a SIF LEAInfo
 * to an SLI LEAEntity.
 *
 * @author slee
 *
 */
@Component
public class LeaInfoTranslationTask extends AbstractTranslationTask<LEAInfo, LeaEntity> {
    @Autowired
    OperationalStatusConverter operationalStatusConverter;

    @Autowired
    PhoneNumberListConverter phoneNumberListConverter;

    @Autowired
    AddressListConverter addressListConverter;

    @Autowired
    SifIdResolver sifIdResolver;

    public LeaInfoTranslationTask() {
        super(LEAInfo.class);
    }

    @Override
    public List<LeaEntity> doTranslate(LEAInfo leaInfo, String zoneId) {
        LeaEntity e = new LeaEntity();
        // covert properties
        e.setStateOrganizationId(leaInfo.getStateProvinceId());
        e.setWebSite(leaInfo.getLEAURL());
        e.setNameOfInstitution(leaInfo.getLEAName());
        e.setOperationalStatus(operationalStatusConverter.convert(OperationalStatus.wrap(leaInfo.getOperationalStatus())));
        e.setAddress(addressListConverter.convert(leaInfo.getAddressList()));
        e.setTelephone(phoneNumberListConverter.convertInstitutionTelephone(leaInfo.getPhoneNumberList()));

        String seaGuid = sifIdResolver.getZoneSea(zoneId);
        if (seaGuid != null) {
            e.setParentEducationAgencyReference(seaGuid);
        }

        List<LeaEntity> list = new ArrayList<LeaEntity>(1);
        list.add(e);
        return list;
    }

}
