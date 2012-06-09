package org.slc.sli.test.generators;

import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class ParentGenerator {

    static AddressGenerator ag;
    static NameGenerator nameGenerator;

    public ParentGenerator() {
            }

    public ParentGenerator(StateAbbreviationType state) {
    	this.setState(state);
    }

    public void setState(StateAbbreviationType state) {
        try {
            ag = new AddressGenerator(state);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static ParentReferenceType getParentReferenceType(String parentId)
    {
    	ParentReferenceType prt = new ParentReferenceType();
    	ParentIdentityType pit = new ParentIdentityType();
    	prt.setParentIdentity(pit);
        pit.setParentUniqueStateId(parentId);
    	return prt;
    }

    private static void setAddresses(Parent Parent) {
        Address address = AddressGenerator.generateLowFi();

        Parent.getAddress().add(address);
        Parent.getAddress().add(address);
    }


    public static Parent generate(String parentId, boolean isMale) {
        Parent p = new Parent();
        Random random = new Random();

        p.setParentUniqueStateId(parentId);
        p.setSex(isMale ? SexType.MALE : SexType.FEMALE);

        p.setName(getFastName());
        p.getOtherName().add(getFastOtherName());


        TelephoneGenerator telephonegen = new TelephoneGenerator();
    	try {
			p.getTelephone().add(telephonegen.getTelephone());
        	p.getTelephone().add(telephonegen.getTelephone());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	ElectronicMail em = new ElectronicMail();
    	em.setEmailAddress("parent.test@gmail.com");
    	em.setEmailAddressType(ElectronicMailAddressType.HOME_PERSONAL);
    	p.getElectronicMail().add(em);

        p.setLoginId("ParenttLoginID");

        return p;
    }


    public static Name getFastName() {
        Name name = new Name();

        name.setFirstName("firstName");
        name.setMiddleName("middleName");
        name.setLastSurname("lastName");
        name.setGenerationCodeSuffix(GenerationCodeSuffixType.SR);
        name.setMaidenName("maidenName");
        name.setPersonalTitlePrefix(PersonalTitlePrefixType.SR);
        name.setVerification(PersonalInformationVerificationType.DRIVERS_LICENSE);

        return name;
    }

    public static OtherName getFastOtherName() {
        OtherName otherName = new OtherName();

        otherName.setFirstName("firstName");
        otherName.setMiddleName("middleName");
        otherName.setLastSurname("lastName");
        otherName.setGenerationCodeSuffix(GenerationCodeSuffixType.VI);
        otherName.setPersonalTitlePrefix(PersonalTitlePrefixType.DR);
        otherName.setOtherNameType(OtherNameType.ALIAS);

        return otherName;
    }

}
