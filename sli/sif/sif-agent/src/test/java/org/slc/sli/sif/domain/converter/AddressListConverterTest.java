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

import junit.framework.Assert;
import openadk.library.common.AddressList;
import openadk.library.common.AddressType;
import openadk.library.common.StatePrCode;
import openadk.library.common.Street;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.Address;

/**
 * AddressListConverter unit tests
 */
public class AddressListConverterTest extends AdkTest {

    private final AddressListConverter converter = new AddressListConverter();
    private Map<AddressType, String> map = new HashMap<AddressType, String>();
    private List<AddressType> addressTypes = new ArrayList<AddressType>();

    @Test
    public void testNullObject() {
        List<Address> result = converter.convert((AddressList) null);
        Assert.assertNull("Address list should be null", result);
    }

    @Test
    public void testEmptyList() {
        AddressList list = new AddressList();
        List<Address> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyAddress() {
        AddressList list = new AddressList();
        openadk.library.common.Address original = new openadk.library.common.Address();
        list.add(original);
        List<Address> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        Address address = result.get(0);
        Assert.assertEquals("Other", address.getAddressType());
        Assert.assertNull(address.getCity());
        Assert.assertNull(address.getCountryCode());
        Assert.assertNull(address.getPostalCode());
        Assert.assertNull(address.getStateAbbreviation());
        Assert.assertNull(address.getStreetNumberName());
    }

    @Test
    public void testConversion() {
        map.clear();
        map.put(AddressType._0369_CAMPUS, "Other");
        map.put(AddressType._0369_EMPLOYER, "Other");
        map.put(AddressType._0369_EMPLOYMENT, "Work");
        map.put(AddressType._0369_MAILING, "Mailing");
        map.put(AddressType._0369_ORGANIZATION, "Other");
        map.put(AddressType._0369_OTHER, "Other");
        map.put(AddressType._0369_PERMANENT, "Other");
        map.put(AddressType.EMPLOYERS, "Other");
        map.put(AddressType.EMPLOYMENT, "Work");
        map.put(AddressType.MAILING, "Mailing");
        map.put(AddressType.OTHER_HOME, "Home");
        map.put(AddressType.OTHER_ORGANIZATION, "Other");
        map.put(AddressType.PHYSICAL_LOCATION, "Physical");
        map.put(AddressType.SHIPPING, "Other");
        map.put(AddressType.SIF15_CAMPUS, "Other");
        map.put(AddressType.SIF15_EMPLOYER, "Other");
        map.put(AddressType.SIF15_EMPLOYMENT, "Work");
        map.put(AddressType.SIF15_MAILING, "Mailing");
        map.put(AddressType.SIF15_ORGANIZATION, "Other");
        map.put(AddressType.SIF15_OTHER, "Other");
        map.put(AddressType.SIF15_PERMANENT, "Other");

        // used to test default
        map.put(AddressType.wrap("something else"), "Other");

        addressTypes.addAll(map.keySet());

        // Come up with SIF addresses, add to list, convert
        AddressList originalList = new AddressList();
        // Add addresses...
        for (int i = 0; i < addressTypes.size(); i++) {
            openadk.library.common.Address address = getAddress(i);
            originalList.add(address);
        }

        List<Address> convertedList = converter.convert(originalList);

        Assert.assertEquals(originalList.size(), convertedList.size());
        int i = 0;
        for (Address converted : convertedList) {
            openadk.library.common.Address original = originalList.get(i++);
            testMapping(original, converted);
        }
    }

    private openadk.library.common.Address getAddress(int i) {
        openadk.library.common.Address address = new openadk.library.common.Address();

        String streetNumber = i + "";
        String streetName = i + " Street";
        String city = "City " + i;
        String countryCode = "US" + i;
        String postalCode = "2770" + (i % 10);
        StatePrCode stateAbbreviation = StatePrCode.NC;

        int addressTypeIndex = i % addressTypes.size();
        AddressType addressType = addressTypes.get(addressTypeIndex);

        Street street = new Street();
        street.setStreetNumber(streetNumber);
        street.setStreetName(streetName);
        address.setStreet(street);
        address.setCity(city);
        address.setCountry(countryCode);
        address.setPostalCode(postalCode);
        address.setStateProvince(stateAbbreviation);
        address.setType(addressType);

        return address;
    }

    private void testMapping(openadk.library.common.Address original, Address converted) {
        String expectedStreetNumberName = original.getStreet().getStreetNumber() + " "
                + original.getStreet().getStreetName();
        Assert.assertEquals(expectedStreetNumberName, converted.getStreetNumberName());

        String expectedCity = original.getCity();
        Assert.assertEquals(expectedCity, converted.getCity());

        String expectedCountryCode = original.getCountry();
        Assert.assertEquals(expectedCountryCode, converted.getCountryCode());

        String expectedPostalCode = original.getPostalCode();
        Assert.assertEquals(expectedPostalCode, converted.getPostalCode());

        String expectedStateAbbreviation = original.getStateProvince();
        Assert.assertEquals(expectedStateAbbreviation, converted.getStateAbbreviation());

        String expectedAddressType = map.get(AddressType.wrap(original.getType()));
        expectedAddressType = expectedAddressType == null ? "Other" : expectedAddressType;
        Assert.assertEquals(expectedAddressType, converted.getAddressType());
    }
}
