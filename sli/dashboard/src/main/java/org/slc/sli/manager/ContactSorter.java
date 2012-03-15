package org.slc.sli.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.slc.sli.entity.GenericEntity;

/**
 * ContactListSorter sorts contact by specified order.
 * 
 * @author Takashi Osako
 *
 */


public final class ContactSorter {
	/**
     * AddressType with priority.
     * If you want to change order of Address display. change the numer of each enum constractor.
     * 1 is the highest priority
     * @author Takashi Osako
     *
     */
    public enum AddressType {
        Home(1),
        Physical(2),
        Billing(4),
        Mailing(3),
        Other(7),
        Temporary(6),
        Work(5),
        UNKOWN(999);
        private int priority;
        private AddressType(int priority) {
        	this.priority = priority;
        }
        public int getPriority() {
        	return this.priority;
        }
	}
    
    /**
     * TelephoneNumberType with priority
     * If you want to change sorting order, change the number of each enum constroctor.
     * 1 is the highest priority
     * @author Takashi Osako
     *
     */
    public enum TelephoneNumberType {
		Home(1),
		Work(2),
		Mobile(3),
		Emergency_1(4),
		Emergency_2(5),
		Fax(6),
		Other(7),
		Unlisted(8),
		UNKNOWN(999);
		private int priority;
		private TelephoneNumberType(int priority) {
			this.priority = priority;
		}
		public int getPriority() {
			return this.priority;
		}
    }
    
    /**
     * ElectronicMailAddressType with priority
     * If you want to change sorting order, change the number of each enum constroctor.
     * 1 is the highest priority
     * @author tosako
     *
     */
    public enum ElectronicMailAddressType {
		Home_Personal(1),
		Work(2),
		Organization(3),
		Other(4),
		UNKNOWN(999);
		private int priority;
		private ElectronicMailAddressType(int priority) {
			this.priority = priority;
		}
		public int getPriority() {
			return this.priority;
		}
    }
    private ContactSorter() {
    }

    /**
     * sort Address, Telephone, and ElectronicEmail by order of Type.
     * @param geneicEntity
     * @return the same object of the input genericEntity, but sorted.
     */
    public static GenericEntity sort(GenericEntity genericEntity) {
    	List<Object> addresses = genericEntity.getList("address");
		List<Object> telephones = genericEntity.getList("telephone");
		List<Object> electronicMails = genericEntity.getList("electronicMail");
		
		//sorthing for Address
		//if size is less than 1, we do not need to sort.
		if (addresses.size() > 1) {
			List<Address> listAddress = new ArrayList<Address>();
			for (Object o:addresses) {
				LinkedHashMap<String , Object> address = (LinkedHashMap<String,Object>)o;
				Address myAddress = (new ContactSorter()).new Address(address);
				listAddress.add(myAddress);
			}
			Collections.sort(listAddress);
			addresses.clear();
			for (Address address:listAddress) { 
				addresses.add(address.getMap());
			}
		}
		
		if (telephones.size() > 1) {
			List<Telephone> listTelephone = new ArrayList<Telephone>();
			for (Object o:listTelephone) {
				LinkedHashMap<String , Object> telephone = (LinkedHashMap<String , Object>)o;
				Telephone myTelephone = (new ContactSorter()).new Telephone(telephone);
				listTelephone.add(myTelephone);
			}
			Collections.sort(listTelephone);
			telephones.clear();
			for (Telephone telephone:listTelephone) {
				telephones.add(telephone.getMap());
			}
		}

		if (electronicMails.size() > 1) {
			List<ElectronicMail> listElectronicMail = new ArrayList<ElectronicMail>();
			for (Object o:listElectronicMail) {
				LinkedHashMap<String , Object> mail = (LinkedHashMap<String , Object>)o;
				ElectronicMail myElectronicMail = (new ContactSorter()).new ElectronicMail(mail);
				listElectronicMail.add(myElectronicMail);
			}
			Collections.sort(listElectronicMail);
			electronicMails.clear();
			for (ElectronicMail mail:listElectronicMail) {
				electronicMails.add(mail.getMap());
			}
		}
		return genericEntity;
	}
    
	
    /**
     * innser Address class.
     * this class is just helping sorting by AddressType by using enum AddressType
     * @author tosako
     *
     */
	public class Address implements Comparable<Address> {
		
		private AddressType addressType;
		private LinkedHashMap<String , Object> map;
		public Address(LinkedHashMap<String , Object> map) {
			this.map = map;
			Object oaddressType = this.map.get("addressType");
			if (oaddressType != null) {
				String mAddressType = oaddressType.toString();
				if (mAddressType.equals("Home"))
					this.addressType = AddressType.Home;
				else if (mAddressType.equals("Physical"))
					this.addressType = AddressType.Physical;
				else if (mAddressType.equals("Billing"))
					this.addressType = AddressType.Billing;
				else if (mAddressType.equals("Mailing"))
					this.addressType = AddressType.Mailing;
				else if (mAddressType.equals("Other"))
					this.addressType = AddressType.Other;
				else if (mAddressType.equals("Temporary"))
					this.addressType = AddressType.Temporary;
				else if (mAddressType.equals("Work"))
					this.addressType = AddressType.Work;
				else
					this.addressType = AddressType.UNKOWN;
			} else {
				this.addressType = AddressType.UNKOWN;
			}
		}
		public int compareTo(Address a) {
			if (this.addressType.getPriority() < a.addressType.getPriority())
				return -1;
			else if (this.addressType.getPriority() > a.addressType.getPriority())
				return 1;
			return 0;
		}
		private LinkedHashMap<String , Object> getMap() {
			return map;
		}
	}
	/**
	 * innser Telephone class.
	 * help sorting data by TelephoneType.
	 * @author tosako
	 *
	 */
	public class Telephone implements Comparable<Telephone> {
		private TelephoneNumberType telephoneNumberType;
		private boolean primaryTelephoneNumberIndicator = false;
		private LinkedHashMap<String , Object> map;
		public Telephone(LinkedHashMap<String , Object> map) {
			this.map = map;
			Object otelephoneNumberType = this.map.get("telephoneNumberType");
			if (otelephoneNumberType != null) {
				String mtelephoneNumberType = otelephoneNumberType.toString();
				Object oprimaryTelephoneNumberIndicator = this.map.get("primaryTelephoneNumberIndicator");
				if (oprimaryTelephoneNumberIndicator != null)
					this.primaryTelephoneNumberIndicator = oprimaryTelephoneNumberIndicator.toString().toLowerCase().equals("true");
				if (mtelephoneNumberType.equals("Home"))
					this.telephoneNumberType = TelephoneNumberType.Home;
				else if (mtelephoneNumberType.equals("Work"))
					this.telephoneNumberType = TelephoneNumberType.Work;
				else if (mtelephoneNumberType.equals("Mobile"))
					this.telephoneNumberType = TelephoneNumberType.Mobile;
				else if (mtelephoneNumberType.equals("Emergency 1"))
					this.telephoneNumberType = TelephoneNumberType.Emergency_1;
				else if (mtelephoneNumberType.equals("Emergency 2"))
					this.telephoneNumberType = TelephoneNumberType.Emergency_2;
				else if (mtelephoneNumberType.equals("Fax"))
					this.telephoneNumberType = TelephoneNumberType.Fax;
				else if (mtelephoneNumberType.equals("Other"))
					this.telephoneNumberType = TelephoneNumberType.Other;
				else if (mtelephoneNumberType.equals("Unlisted"))
					this.telephoneNumberType = TelephoneNumberType.Unlisted;
				else
					this.telephoneNumberType = TelephoneNumberType.UNKNOWN;
			} else
				this.telephoneNumberType = TelephoneNumberType.UNKNOWN;
		}
		public int compareTo(Telephone a) {
			if (this.primaryTelephoneNumberIndicator)
				return -1;
			else if (this.telephoneNumberType.getPriority() < a.telephoneNumberType.getPriority())
				return -1;
			else if (this.telephoneNumberType.getPriority() > a.telephoneNumberType.getPriority())
				return 1;
			return 0;
		}
		private LinkedHashMap<String , Object> getMap() {
			return map;
		}
	}
	
	/**
	 * inner ElectronicMailAddress
	 * help sorting data by ElectronicMailAddressType
	 * @author Takashi Osako
	 *
	 */
	public class ElectronicMail implements Comparable<ElectronicMail> {
		private ElectronicMailAddressType electronicMailAddress;
		private LinkedHashMap<String , Object> map;
		public ElectronicMail(LinkedHashMap<String, Object> map) {
			this.map = map;
			Object oelectronicMailAddress = this.map.get("electronicMailAddress");
			if (oelectronicMailAddress != null) {
				String mElectronicMailAddressType = oelectronicMailAddress.toString();
				
				if (mElectronicMailAddressType.equals("Home_Personal"))
					this.electronicMailAddress = ElectronicMailAddressType.Home_Personal;
				else if (mElectronicMailAddressType.equals("Work"))
					this.electronicMailAddress = ElectronicMailAddressType.Work;
				else if (mElectronicMailAddressType.equals("Organization"))
					this.electronicMailAddress = ElectronicMailAddressType.Organization;
				else if (mElectronicMailAddressType.equals("Other"))
					this.electronicMailAddress = ElectronicMailAddressType.Other;
				else
					this.electronicMailAddress = ElectronicMailAddressType.UNKNOWN;
			} else
				this.electronicMailAddress = ElectronicMailAddressType.UNKNOWN;
		}
		public int compareTo(ElectronicMail o) {
			if (this.electronicMailAddress.getPriority() < o.electronicMailAddress.getPriority())
				return -1;
			else if (this.electronicMailAddress.getPriority() > o.electronicMailAddress.getPriority())
				return 1;
			return 0;
		}
		private LinkedHashMap<String , Object> getMap() {
			return map;
		}
	}
}
