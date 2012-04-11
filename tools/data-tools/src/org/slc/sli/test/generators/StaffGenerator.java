package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.LevelOfEducationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.OldEthnicityType;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.Staff;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;

public class StaffGenerator {
    AddressGenerator ag;
    NameGenerator ng;
    TelephoneGenerator tg;
    ElectronicMailGenerator emg;
    Random random = new Random();
    boolean optional = true;
    RaceItemType[] raceItemTypes = RaceItemType.values();
    LevelOfEducationType[] levelOfEducationTypes = LevelOfEducationType.values();
    OldEthnicityType[] oldEthnicityTypes = OldEthnicityType.values();

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

    public Staff generate(String staffId) {
        Staff staff = new Staff();
        populateFields(staff, staffId);
        return staff;
    }

    protected void populateFields(Staff staff, String staffId) {

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

                staff.getElectronicMail().add(
                        emg.generate(staff.getName().getFirstName() + "." + staff.getName().getLastSurname()));

                staff.setOldEthnicity(oldEthnicityTypes[random.nextInt(oldEthnicityTypes.length)]);

                staff.setYearsOfPriorProfessionalExperience(new Integer(20));

                staff.setYearsOfPriorTeachingExperience(new Integer(15));

                // TODO: add Credentials

                staff.setLoginId(staff.getName().getFirstName() + staff.getName().getLastSurname());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StaffReferenceType getStaffReference(String staffId) {
        StaffIdentityType sit = new StaffIdentityType();
        sit.setStaffUniqueStateId(staffId);
        StaffReferenceType srt = new StaffReferenceType();
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
