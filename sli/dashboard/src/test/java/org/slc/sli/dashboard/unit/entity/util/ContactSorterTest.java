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


/**
 * 
 */
package org.slc.sli.dashboard.unit.entity.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.ContactSorter;
import org.slc.sli.dashboard.util.Constants;

/**
 * @author tosako
 * 
 */
public class ContactSorterTest {
    
    private GenericEntity entity = null;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUpBefore() throws Exception {
        this.entity = new GenericEntity();
        // List<LinkedHashMap<String , Object>> addresses = genericEntity.getList("address");
        // List<LinkedHashMap<String , Object>> telephones = genericEntity.getList("telephone");
        // List<LinkedHashMap<String , Object>> electronicMails =
        // genericEntity.getList("electronicMail");
        
        // For address
        // Home(1), Physical(2), Billing(4), Mailing(3), Other(7), Temporary(6), Work(5)
        List<LinkedHashMap<String, Object>> addresses = new ArrayList<LinkedHashMap<String, Object>>();
        GenericEntity address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_WORK);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_PHYSICAL);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_HOME);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_OTHER);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_MAILING);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_TEMPORARY);
        addresses.add(address);
        
        address = new GenericEntity();
        address.put("addressType", Constants.TYPE_ADDRESS_BILLING);
        addresses.add(address);
        this.entity.put("address", addresses);
        
        // For Telephone
        // Home(1), Work(2), Mobile(3), Emergency_1(4), Emergency_2(5), Fax(6), Other(7),
        // Unlisted(8)`
        List<LinkedHashMap<String, Object>> telephones = new ArrayList<LinkedHashMap<String, Object>>();
        GenericEntity telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_UNLISTED);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_EMERGENCY_2);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_HOME);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_OTHER);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_MOBILE);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "true");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_WORK);
        telephones.add(telephone);
        
        telephone = new GenericEntity();
        telephone.put("primaryTelephoneNumberIndicator", "false");
        telephone.put("telephoneNumberType", Constants.TYPE_TELEPHONE_EMERGENCY_1);
        telephones.add(telephone);
        this.entity.put("telephone", telephones);
        
        // For Email
        // Home_Personal(1), Work(2), Organization(3), Other(4)
        List<LinkedHashMap<String, Object>> emails = new ArrayList<LinkedHashMap<String, Object>>();
        GenericEntity email = new GenericEntity();
        email.put("emailAddressType", Constants.TYPE_EMAIL_ORGANIZATION);
        emails.add(email);
        
        email = new GenericEntity();
        email.put("emailAddressType", Constants.TYPE_EMAIL_WORK);
        emails.add(email);
        
        email = new GenericEntity();
        email.put("emailAddressType", Constants.TYPE_EMAIL_OTHER);
        emails.add(email);
        
        email = new GenericEntity();
        email.put("emailAddressType", Constants.TYPE_EMAIL_HOME_PERSONAL);
        emails.add(email);
        this.entity.put("electronicMail", emails);
    }
    
    /**
     * Test method for
     * {@link org.slc.sli.dashboard.entity.util.ContactSorter#sort(org.slc.sli.dashboard.entity.GenericEntity)}.
     */
    @Test
    public void testEmailSort() {
        ContactSorter.sort(this.entity);
        List<LinkedHashMap<String, Object>> emails = this.entity.getList("electronicMail");
        assertEquals("There should be 4 records of Email", 4, emails.size());
        
        assertEquals("EmailAddressType should be " + Constants.TYPE_EMAIL_HOME_PERSONAL,
                Constants.TYPE_EMAIL_HOME_PERSONAL, emails.get(0).get("emailAddressType"));
        assertEquals("EmailAddressType should be " + Constants.TYPE_EMAIL_WORK, Constants.TYPE_EMAIL_WORK, emails
                .get(1).get("emailAddressType"));
        assertEquals("EmailAddressType should be " + Constants.TYPE_EMAIL_ORGANIZATION,
                Constants.TYPE_EMAIL_ORGANIZATION, emails.get(2).get("emailAddressType"));
        assertEquals("EmailAddressType should be " + Constants.TYPE_EMAIL_OTHER, Constants.TYPE_EMAIL_OTHER, emails
                .get(3).get("emailAddressType"));
    }
    
    /**
     * Test method for
     * {@link org.slc.sli.dashboard.entity.util.ContactSorter#sort(org.slc.sli.dashboard.entity.GenericEntity)}.
     */
    @Test
    public void testTelephoneSort() {
        ContactSorter.sort(this.entity);
        List<LinkedHashMap<String, Object>> telephones = this.entity.getList("telephone");
        assertEquals("There should be 6 records of Telephone", 7, telephones.size());
        
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_WORK, Constants.TYPE_TELEPHONE_WORK,
                telephones.get(0).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_HOME, Constants.TYPE_TELEPHONE_HOME,
                telephones.get(1).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_MOBILE,
                Constants.TYPE_TELEPHONE_MOBILE, telephones.get(2).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_EMERGENCY_1,
                Constants.TYPE_TELEPHONE_EMERGENCY_1, telephones.get(3).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_EMERGENCY_2,
                Constants.TYPE_TELEPHONE_EMERGENCY_2, telephones.get(4).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_OTHER, Constants.TYPE_TELEPHONE_OTHER,
                telephones.get(5).get("telephoneNumberType"));
        assertEquals("TelephoneNumberType should be " + Constants.TYPE_TELEPHONE_UNLISTED,
                Constants.TYPE_TELEPHONE_UNLISTED, telephones.get(6).get("telephoneNumberType"));
    }
    
    /**
     * Test method for
     * {@link org.slc.sli.dashboard.entity.util.ContactSorter#sort(org.slc.sli.dashboard.entity.GenericEntity)}.
     */
    @Test
    public void testAddressSort() {
        ContactSorter.sort(this.entity);
        List<LinkedHashMap<String, Object>> addresses = this.entity.getList("address");
        assertEquals("There should be 7 records of Address", 7, addresses.size());
        
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_HOME, Constants.TYPE_ADDRESS_HOME, addresses
                .get(0).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_PHYSICAL, Constants.TYPE_ADDRESS_PHYSICAL,
                addresses.get(1).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_MAILING, Constants.TYPE_ADDRESS_MAILING,
                addresses.get(2).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_BILLING, Constants.TYPE_ADDRESS_BILLING,
                addresses.get(3).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_WORK, Constants.TYPE_ADDRESS_WORK, addresses
                .get(4).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_TEMPORARY, Constants.TYPE_ADDRESS_TEMPORARY,
                addresses.get(5).get("addressType"));
        assertEquals("AddressType should be " + Constants.TYPE_ADDRESS_OTHER, Constants.TYPE_ADDRESS_OTHER, addresses
                .get(6).get("addressType"));
    }
}
