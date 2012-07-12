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

package org.slc.sli.sif.domain.converter;

import java.util.ArrayList;
import java.util.List;

import openadk.library.common.AddressList;

import org.dozer.DozerConverter;

import org.slc.sli.sif.domain.slientity.Address;

/**
 * A customized Dozer converter to convert SIF AddressList to SLI Address list.
 *
 * @author slee
 *
 */
public class AddressListConverter extends DozerConverter<AddressList, List<Address>>
{
    public AddressListConverter() {
        super(AddressList.class, (Class<List<Address>>)new ArrayList<Address>().getClass());
    }

    @Override
    public List<Address> convertTo(AddressList source, List<Address> destination)
    {
        if (source==null) {
            return null;
        }
        openadk.library.common.Address[] addresses = source.getAddresses();
        List<Address> list = new ArrayList<Address>(addresses.length);
        for (openadk.library.common.Address address : addresses) {
            Address sliAddr = new Address();
            sliAddr.setStreetNumberName(address.getStreet().getStreetNumber()+" "+address.getStreet().getStreetName());
            sliAddr.setCity(address.getCity());
            sliAddr.setCountryCode(address.getCountry());
            sliAddr.setPostalCode(address.getPostalCode());
            sliAddr.setStateAbbreviation(address.getStateProvince());
            sliAddr.setAddressType(SchoolMappings.toSliAddressType(address.getType()));
            list.add(sliAddr);
        }
        return list;
    }

    @Override
    public AddressList convertFrom(List<Address> source, AddressList destination)
    {
        return null;
    }

}
