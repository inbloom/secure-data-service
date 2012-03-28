package org.slc.sli.test.generators;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Telephone;
import org.slc.sli.test.edfi.entities.TelephoneNumberType;

public class TelephoneGenerator {
	private static final Logger log = Logger.getLogger(TelephoneGenerator.class);
	public String filename = "src/org/slc/sli/test/data/address/city_US.csv";
	private List<String> areaCode = new ArrayList<String>();
	Random generator = new Random();

	public Telephone getTelephone() throws Exception {
		Telephone tel = new Telephone();
		tel.setPrimaryTelephoneNumberIndicator(generator.nextBoolean());

		String telephone;
		String tempAreaCode;
		areaCode = getAreaCode();
		int roll = generator.nextInt(42182) + 1;
		tempAreaCode = areaCode.get(roll);
		telephone = tempAreaCode.concat(getFirstThreeDigits()).concat(getLastFourDigits());
		tel.setTelephoneNumber(telephone);
		tel.setTelephoneNumberType(getPhoneType());
		return tel;
	}



	public List<String> getAreaCode() throws Exception {
		FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader areaCodeBuffer = new BufferedReader(new InputStreamReader(in));

        String line;
        String [] areaCodeSplit;
        line = areaCodeBuffer.readLine();
        while ((line = areaCodeBuffer.readLine()) != null){
        	areaCodeSplit = line.split(";");
        		if (areaCodeSplit.length == 5 ){
        				areaCode.add(areaCodeSplit[4]);
        		}
        		else {
        			log.info("The areaCode is : " + areaCodeSplit[4] + " which is invalid!");

        		}
        }
        return areaCode;

	}

	public String getFirstThreeDigits() throws Exception {
		int roll = generator.nextInt(999) + 1;
		String firstThreeDigits;

		if (roll >= 100)
			firstThreeDigits = "-" + roll;
		else if (roll >= 10 && roll <= 99)
			firstThreeDigits = "-0" + roll;
		else
			firstThreeDigits = "-00" + roll;

		return firstThreeDigits;
	}


	public String getLastFourDigits () throws Exception {
		int roll = generator.nextInt(9999) + 1 ;
		String lastFourDigits;

		if(roll >=1000)
			lastFourDigits = "-" + roll;
		else if (roll >= 100 && roll <= 999)
			lastFourDigits = "-0" + roll;
		else if (roll >=10 && roll <= 99)
			lastFourDigits = "-00" + roll;
		else
			lastFourDigits = "-000" + roll;

		return lastFourDigits;

	}

	public TelephoneNumberType getPhoneType() {
		int roll = generator.nextInt(8) + 1;
		switch (roll) {
			case 1:  return TelephoneNumberType.WORK;
			case 2: return TelephoneNumberType.EMERGENCY_1;
			case 3: return TelephoneNumberType.EMERGENCY_2;
			case 4: return TelephoneNumberType.FAX;
			case 5: return TelephoneNumberType.MOBILE;
			case 6: return TelephoneNumberType.OTHER;
			case 7: return TelephoneNumberType.UNLISTED;
			default: return TelephoneNumberType.HOME;
		}
	}


	public static void main(String args[]) throws Exception{
		TelephoneGenerator teleFactory= new TelephoneGenerator();
		List<TelephoneGenerator> lt = new ArrayList<TelephoneGenerator>();
		lt.add(teleFactory);
		//for(AddressFactory addressFactory:factoryList)
		for (TelephoneGenerator tf: lt)
		for (String telephoneId : "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20".split(" ")) {
			Telephone tel = tf.getTelephone();
			log.info(telephoneId + " " + tel.getTelephoneNumber() + " " + tel.getTelephoneNumberType() +
					" " + tel.isPrimaryTelephoneNumberIndicator());

		}

	}


}
