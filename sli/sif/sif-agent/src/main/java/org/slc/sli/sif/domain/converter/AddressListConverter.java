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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openadk.library.common.AddressList;
import openadk.library.common.AddressType;
import openadk.library.common.Street;
import openadk.library.student.StudentAddressList;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.Address;

/**
 * A custom converter to convert SIF AddressList to SLI Address list.
 *
 * Valid SLI values:
 * Home
 * Physical
 * Billing
 * Mailing
 * Other
 * Temporary
 * Work
 *
 * @author slee
 *
 */
@Component
public class AddressListConverter {

    private static final Map<AddressType, String> ADDRESS_TYPE_MAP = new HashMap<AddressType, String>();
    static {
        ADDRESS_TYPE_MAP.put(AddressType._0369_CAMPUS, "Other");
        ADDRESS_TYPE_MAP.put(AddressType._0369_EMPLOYER, "Other");
        ADDRESS_TYPE_MAP.put(AddressType._0369_EMPLOYMENT, "Work");
        ADDRESS_TYPE_MAP.put(AddressType._0369_MAILING, "Mailing");
        ADDRESS_TYPE_MAP.put(AddressType._0369_ORGANIZATION, "Other");
        ADDRESS_TYPE_MAP.put(AddressType._0369_OTHER, "Other");
        ADDRESS_TYPE_MAP.put(AddressType._0369_PERMANENT, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.EMPLOYERS, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.EMPLOYMENT, "Work");
        ADDRESS_TYPE_MAP.put(AddressType.MAILING, "Mailing");
        ADDRESS_TYPE_MAP.put(AddressType.OTHER_HOME, "Home");
        ADDRESS_TYPE_MAP.put(AddressType.OTHER_ORGANIZATION, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.PHYSICAL_LOCATION, "Physical");
        ADDRESS_TYPE_MAP.put(AddressType.SHIPPING, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_CAMPUS, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_EMPLOYER, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_EMPLOYMENT, "Work");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_MAILING, "Mailing");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_ORGANIZATION, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_OTHER, "Other");
        ADDRESS_TYPE_MAP.put(AddressType.SIF15_PERMANENT, "Other");
    }

    public List<Address> convert(AddressList addressList) {
        if (addressList == null) {
            return null;
        }

        return toSliAddressList(addressList.getAddresses());
    }

    public List<Address> convert(StudentAddressList addressList) {
        if (addressList == null) {
            return null;
        }

        return toSliAddressList(addressList.getAddresses());
    }

    private List<Address> toSliAddressList(openadk.library.common.Address[] addresses) {
        List<Address> list = new ArrayList<Address>(addresses.length);
        for (openadk.library.common.Address address : addresses) {
            Address sliAddr = new Address();
            Street street = address.getStreet();
            if (street != null) {
                sliAddr.setStreetNumberName(street.getStreetNumber() + " " + street.getStreetName());
            }
            sliAddr.setCity(address.getCity());
            sliAddr.setCountryCode(address.getCountry());
            sliAddr.setPostalCode(address.getPostalCode());
            sliAddr.setStateAbbreviation(address.getStateProvince());
            sliAddr.setAddressType(toSliAddressType(AddressType.wrap(address.getType())));
            list.add(sliAddr);
        }
        return list;
    }

    private String toSliAddressType(AddressType addressType) {
        String mapping = ADDRESS_TYPE_MAP.get(addressType);
        return mapping == null ? "Other" : mapping;
    }
}
