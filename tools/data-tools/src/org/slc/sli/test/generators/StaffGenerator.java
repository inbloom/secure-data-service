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

import java.util.Random;

import org.slc.sli.test.edfi.entities.LevelOfEducationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.OldEthnicityType;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.Staff;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;

public class StaffGenerator {
    static AddressGenerator ag;
    static NameGenerator ng;
    static TelephoneGenerator tg;
    static ElectronicMailGenerator emg;
    static Random random = new Random(31);
    static boolean optional = true;
    static RaceItemType[] raceItemTypes = RaceItemType.values();
    static LevelOfEducationType[] levelOfEducationTypes = LevelOfEducationType.values();
    static OldEthnicityType[] oldEthnicityTypes = OldEthnicityType.values();

  static {
	  try {
		  ag =  new AddressGenerator(StateAbbreviationType.NY);
		  ng = new NameGenerator();
		  tg =new TelephoneGenerator();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }

    public StaffGenerator(StateAbbreviationType state, boolean optional) {
        try {
            ng = new NameGenerator();
            tg = new TelephoneGenerator();
            emg = new ElectronicMailGenerator();
            this.optional = optional;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public static Staff generateMediumFi(String staffId) throws Exception {
        Staff staff = new Staff();
        populateFields(staff, staffId);
        return staff;
    }

    protected static void populateFields(Staff staff, String staffId) throws Exception {

        try {
            staff.setId(staffId);
            staff.setStaffUniqueStateId(staffId);
            staff.setName(ng.getName());

            staff.setSex(random.nextBoolean() ? SexType.FEMALE : SexType.MALE);

            RaceType rt = new RaceType();
            staff.setRace(rt);
            staff.getRace().getRacialCategory().add(raceItemTypes[random.nextInt(raceItemTypes.length)]);

            staff.setHighestLevelOfEducationCompleted(levelOfEducationTypes[random
                    .nextInt(levelOfEducationTypes.length)]);

            staff.setHispanicLatinoEthnicity(random.nextBoolean());

            staff.setStaffUniqueStateId(staffId);

            if (optional) {
                // TODO: add StaffIdentificationCodes

                staff.getOtherName().add(ng.getOtherName());
                if (random.nextBoolean())
                    staff.getOtherName().add(ng.getOtherName());

                staff.setBirthDate("2011-03-04");

                staff.getAddress().add(ag.getRandomAddress());
                if (random.nextBoolean())
                    staff.getAddress().add(ag.getRandomAddress());

                staff.getTelephone().add(tg.getTelephone());

//                staff.getElectronicMail().add(
//                        emg.generate(staff.getName().getFirstName() + "." + staff.getName().getLastSurname()));

                staff.setOldEthnicity(oldEthnicityTypes[random.nextInt(oldEthnicityTypes.length)]);

                staff.setYearsOfPriorProfessionalExperience(new Integer(20));

                staff.setYearsOfPriorTeachingExperience(new Integer(15));

                // TODO: add Credentials

              //  staff.setLoginId(staff.getName().getFirstName() + staff.getName().getLastSurname());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SLCStaffReferenceType getStaffReference(String staffId) {
        SLCStaffIdentityType sit = new SLCStaffIdentityType();
        sit.setStaffUniqueStateId(staffId);
        SLCStaffReferenceType srt = new SLCStaffReferenceType();
        srt.setStaffIdentity(sit);
        return srt;
    }

    public static Staff generateLowFi(String staffId) {
        Staff staff = new Staff();
        populateFieldsLowFi(staff, staffId);
        return staff;
    }

    protected static void populateFieldsLowFi(Staff staff, String staffId) {
        staff.setStaffUniqueStateId(staffId);
        Name name = new Name();
        name.setFirstName("Gaius");
        name.setLastSurname("Baltar");
        staff.setName(name);
        staff.setSex(SexType.MALE);
        staff.setHispanicLatinoEthnicity(false);
        RaceType raceType = new RaceType();
        raceType.getRacialCategory().add(RaceItemType.WHITE);
        staff.setRace(raceType);
        staff.setHighestLevelOfEducationCompleted(LevelOfEducationType.DOCTORATE);
    }
}
