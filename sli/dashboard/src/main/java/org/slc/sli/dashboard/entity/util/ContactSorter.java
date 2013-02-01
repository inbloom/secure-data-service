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


package org.slc.sli.dashboard.entity.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;

/**
 * ContactListSorter sorts contact by specified order.
 *
 * @author Takashi Osako
 *
 */

public final class ContactSorter {
    private ContactSorter() {
    }

    private static Map<String , Integer> addressPriority;
    private static Map<String , Integer> telephonePriority1;
    private static Map<String , Integer> telephonePriority2;
    private static Map<String , Integer> emailPriority;
    
    // initialize sort priorities
    static {
        //Address sort by AddressType
        //Home(1), Physical(2), Billing(4), Mailing(3), Other(7), Temporary(6), Work(5)
        addressPriority = new HashMap<String , Integer>();
        addressPriority.put(Constants.TYPE_ADDRESS_HOME, 1);
        addressPriority.put(Constants.TYPE_ADDRESS_PHYSICAL, 2);
        addressPriority.put(Constants.TYPE_ADDRESS_BILLING, 4);
        addressPriority.put(Constants.TYPE_ADDRESS_MAILING, 3);
        addressPriority.put(Constants.TYPE_ADDRESS_OTHER, 7);
        addressPriority.put(Constants.TYPE_ADDRESS_TEMPORARY, 6);
        addressPriority.put(Constants.TYPE_ADDRESS_WORK, 5);

        
        //Telephone List sort by primaryTelephoneNumberIndicator
        //Highest priority
        //true 1
        //false 2
        telephonePriority1 = new HashMap<String , Integer>();
        telephonePriority1.put("true", 1);
        telephonePriority1.put("false", 2);
        
        //Telephone sort by TelephoneType
        //Home(1), Work(2), Mobile(3), Emergency_1(4), Emergency_2(5), Fax(6), Other(7), Unlisted(8)
        telephonePriority2 = new HashMap<String , Integer>();
        telephonePriority2.put(Constants.TYPE_TELEPHONE_HOME, 1);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_WORK, 2);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_MOBILE, 3);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_EMERGENCY_1, 4);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_EMERGENCY_2, 5);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_FAX, 6);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_OTHER, 7);
        telephonePriority2.put(Constants.TYPE_TELEPHONE_UNLISTED, 8);

        //Email sort by EmailType
        //Home_Personal(1), Work(2), Organization(3), Other(4)
        emailPriority = new HashMap<String , Integer>();
        emailPriority.put(Constants.TYPE_EMAIL_HOME_PERSONAL, 1);
        emailPriority.put(Constants.TYPE_EMAIL_WORK, 2);
        emailPriority.put(Constants.TYPE_EMAIL_ORGANIZATION, 3);
        emailPriority.put(Constants.TYPE_EMAIL_OTHER, 4);        
    }
    
    /**
     * sort Address, Telephone, and ElectronicEmail by order of Type.
     *
     * @param geneicEntity
     * @return the same object of the input genericEntity, but sorted.
     */
    public static GenericEntity sort(GenericEntity genericEntity) {
        List<LinkedHashMap<String , Object>> addresses = genericEntity.getList("address");
        List<LinkedHashMap<String , Object>> telephones = genericEntity.getList("telephone");
        List<LinkedHashMap<String , Object>> electronicMails = genericEntity.getList("electronicMail");

        // sorting for Address
        // if size is less than 1, we do not need to sort.
        if (addresses.size() > 1) {
            GenericEntityComparator genericSorter = new GenericEntityComparator("addressType", addressPriority);
            Collections.sort(addresses , genericSorter);
        }
        // sorting telephone numbers
        if (telephones.size() > 1) {
            GenericEntityComparator genericSorter = new GenericEntityComparator("telephoneNumberType", telephonePriority2);
            Collections.sort(telephones , genericSorter);

            //if primaryTelephoneNumberIndicator is true, it has the highest priority.
            genericSorter = new GenericEntityComparator("primaryTelephoneNumberIndicator", telephonePriority1);
            Collections.sort(telephones , genericSorter);
        }
        // sorting email addresses
        if (electronicMails.size() > 1) {
            GenericEntityComparator genericSorter = new GenericEntityComparator("emailAddressType", emailPriority);
            Collections.sort(electronicMails , genericSorter);
        }
        return genericEntity;
    }
}
