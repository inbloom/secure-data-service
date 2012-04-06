package org.slc.sli.test.generators;


import org.slc.sli.test.edfi.entities.LevelOfEducationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.Staff;

public class StaffGenerator {

    public static Staff generateLowFi(String staffId) {
        Staff staff = new Staff();
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

        return staff;
    }
}
