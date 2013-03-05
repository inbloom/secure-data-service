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
    //public String filename = "database/address/city_US.csv";
    //private List<String> areaCode = new ArrayList<String>();
    Random generator = new Random(31);

    public Telephone getTelephone() throws Exception {
        String oneAreaCode;
        String telephone;
        Telephone tel = new Telephone();
        tel.setPrimaryTelephoneNumberIndicator(generator.nextBoolean());
        oneAreaCode = getAreaCode();
        telephone = oneAreaCode.concat(getFirstThreeDigits()).concat(getLastFourDigits());
        tel.setTelephoneNumber(telephone);
        tel.setTelephoneNumberType(getPhoneType());
        return tel;
    }


    public String getAreaCode () {
        int roll = generator.nextInt(40) + 1;
        switch (roll) {
            case 1:  return "603";
            case 2:  return "516";
            case 3:  return "787";
            case 4:  return "340";
            case 5:  return "978";
            case 6:  return "508";
            case 7:  return "617";
            case 8:  return "401";
            case 9:  return "603";
            case 10: return "207";
            case 11: return "802";
            case 12: return "860";
            case 13: return "203";
            case 14: return "201";
            case 15: return "908";
            case 16: return "973";
            case 17: return "732";
            case 18: return "609";
            case 19: return "212";
            case 20: return "718";
            case 21: return "914";
            case 22: return "724";
            case 23: return "814";
            case 24: return "215";
            case 25: return "610";
            case 26: return "304";
            case 27: return "704";
            case 28: return "828";
            case 29: return "910";
            case 30: return "336";
            case 31: return "828";
            case 32: return "864";
            case 33: return "770";
            case 34: return "404";
            case 35: return "912";
            case 36: return "706";
            case 37: return "352";
            case 38: return "561";
            case 39: return "205";
            case 40: return "256";
            case 41: return "423";
            case 42: return "901";
            default: return "413";
        }

    }
    /*
    public String getAreaCode() throws Exception {
        FileInputStream fstream = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader areaCodeBuffer = new BufferedReader(new InputStreamReader(in));
        String oneAreaCode;
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

       int roll = generator.nextInt(42180) + 1;
        oneAreaCode = areaCode.get(roll);

        return oneAreaCode;

    }
    */

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

    public static Telephone getFastTelephone() {
        Telephone tel = new Telephone();
        tel.setPrimaryTelephoneNumberIndicator(true);
        tel.setTelephoneNumber("123-456-7890");
        tel.setTelephoneNumberType(TelephoneNumberType.HOME);
        return tel;
    }


    public static void main(String args[]) throws Exception{
        TelephoneGenerator teleFactory= new TelephoneGenerator();
        List<TelephoneGenerator> lt = new ArrayList<TelephoneGenerator>();
        lt.add(teleFactory);
        //for(AddressFactory addressFactory:factoryList)
        for (TelephoneGenerator tf: lt)
        for (String telephoneId : "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20".split(" "))
            {
            Telephone tel = tf.getTelephone();
            log.info( " " + tel.getTelephoneNumber() + " " + tel.getTelephoneNumberType() +
                    " " + tel.isPrimaryTelephoneNumberIndicator());

        }

    }


}
