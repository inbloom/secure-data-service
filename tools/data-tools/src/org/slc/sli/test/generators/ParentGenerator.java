package org.slc.sli.test.generators;

import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class ParentGenerator {
	
    AddressGenerator ag;
    NameGenerator nameGenerator;

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

    public Parent generate(String parentId) {
        Parent p = new Parent();
        Random random = new Random();
        
		try {
			nameGenerator = new NameGenerator();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        p.setParentUniqueStateId(parentId);
        p.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        if (p.getSex().equals(SexType.MALE)) {
        	p.setName(nameGenerator.getMaleName());
            p.getOtherName().add(nameGenerator.getMaleOtherName());
            p.getOtherName().add(nameGenerator.getMaleOtherName());
        } else {
        	p.setName(nameGenerator.getFemaleName());
            p.getOtherName().add(nameGenerator.getFemaleOtherName());
            p.getOtherName().add(nameGenerator.getFemaleOtherName());
        }
        
        p.getAddress().add(ag.getRandomAddress());
        if (random.nextBoolean())
            p.getAddress().add(ag.getRandomAddress());
       
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
}
