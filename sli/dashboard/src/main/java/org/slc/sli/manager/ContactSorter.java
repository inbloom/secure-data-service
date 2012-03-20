package org.slc.sli.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.slc.sli.entity.GenericEntity;

/**
 * ContactListSorter sorts contact by specified order.
 * 
 * @author Takashi Osako
 * 
 */

public final class ContactSorter {
    private ContactSorter() {
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
        
        //Home(1), Physical(2), Billing(4), Mailing(3), Other(7), Temporary(6), Work(5)
        Map<String , Integer> addressPriority = new HashMap<String , Integer>();
        addressPriority.put("Home", 1);
        addressPriority.put("Physical", 2);
        addressPriority.put("Billing", 4);
        addressPriority.put("Mailing", 3);
        addressPriority.put("Other", 7);
        addressPriority.put("Temporary", 6);
        addressPriority.put("Work", 5);
        
        //Home(1), Work(2), Mobile(3), Emergency_1(4), Emergency_2(5), Fax(6), Other(7), Unlisted(8)
        Map<String , Integer> telephonePriority1 = new HashMap<String , Integer>();
        telephonePriority1.put("Home", 1);
        telephonePriority1.put("Work", 2);
        telephonePriority1.put("Mobile", 3);
        telephonePriority1.put("Emergency 1", 4);
        telephonePriority1.put("Emergency 2", 5);
        telephonePriority1.put("Fax", 6);
        telephonePriority1.put("Other", 7);
        telephonePriority1.put("Unlisted", 8);
        
        //true 1
        //false 2
        Map<String , Integer> telephonePriority2 = new HashMap<String , Integer>();
        telephonePriority2.put("true", 1);
        telephonePriority2.put("false", 2);

        
        //Home_Personal(1), Work(2), Organization(3), Other(4)
        Map<String , Integer> emailPriority = new HashMap<String , Integer>();
        emailPriority.put("Home/Personal", 1);
        emailPriority.put("Work", 2);
        emailPriority.put("Organization", 3);
        emailPriority.put("Other", 4);
        
        // sorthing for Address
        // if size is less than 1, we do not need to sort.
        if (addresses.size() > 1) {
        	GenericSorter genericSorter = (new ContactSorter()).new GenericSorter("addressType", addressPriority);
        	Collections.sort(addresses , genericSorter);
        }
        if (telephones.size() > 1) {
        	GenericSorter genericSorter = (new ContactSorter()).new GenericSorter("telephoneNumberType", telephonePriority1);
            Collections.sort(telephones , genericSorter);
            
            //if primaryTelephoneNumberIndicator is true, it has the highest priority.
            genericSorter = (new ContactSorter()).new GenericSorter("primaryTelephoneNumberIndicator", telephonePriority2);
            Collections.sort(telephones , genericSorter);
        }
        
        if (electronicMails.size() > 1) {
        	GenericSorter genericSorter = (new ContactSorter()).new GenericSorter("electronicMailAddress", emailPriority);
            Collections.sort(electronicMails , genericSorter);
        }
        return genericEntity;
    }
    
    /**
     * Generic sorting for GenericEntity
     *
     */
    
    private class GenericSorter implements Comparator<LinkedHashMap<String , Object>> {
    	private String fieldName = "";
    	private Map<String , Integer> priorityList = null;
    	
    	/**
    	 * sort by given field name of JSON.
    	 * @param fieldName field name of JSON for sorting
    	 * @param priorityList Stirng: name of type, Integer priority (1 is the heigest priority)
    	 */
    	public GenericSorter(String fieldName, Map<String , Integer> priorityList) {
    		this.fieldName = fieldName;
    		
    		if (priorityList == null)
    			this.priorityList = new HashMap<String , Integer>();
    		else
    			this.priorityList = priorityList;
    	}
		@Override
		public int compare(LinkedHashMap<String , Object> o1, LinkedHashMap<String , Object> o2) {
			
			//temporary assigning priority.  Make it lowest possible.
			int o1Priority = Integer.MAX_VALUE;
			int o2Priority = Integer.MAX_VALUE;
			
			Object o1Type = o1.get(this.fieldName);
			Object o2Type = o2.get(this.fieldName);
			
			//find true priority
			if (o1Type != null) {
				if (this.priorityList.containsKey(o1Type.toString()))
					o1Priority = this.priorityList.get(o1Type.toString());
			}
			if (o2Type != null) {
				if (this.priorityList.containsKey(o2Type.toString()))
					o2Priority = this.priorityList.get(o2Type.toString());
			}
			
			return o1Priority == o2Priority ? 0 : (o1Priority < o2Priority ? -1 : 1);
		}
    }
}
