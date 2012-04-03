package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.LevelOfEducationType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.OldEthnicityType;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Teacher;

public class TeacherGenerator {
    AddressGenerator ag;

    public TeacherGenerator(StateAbbreviationType state) {
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

    public Teacher generate(String teacherId) {

        Teacher teacher = new Teacher();
        Random random = new Random();

        try {
            NameGenerator ng = new NameGenerator();
            Random rondom = new Random();

            teacher.setId(teacherId);
            teacher.setStaffUniqueStateId(teacherId);
            teacher.setName(ng.getName());

            teacher.getOtherName().add(ng.getOtherName());
            if (random.nextBoolean())
                teacher.getOtherName().add(ng.getOtherName());

            teacher.setSex(rondom.nextBoolean() ? SexType.FEMALE : SexType.MALE);

            teacher.setBirthDate("2011-03-04");

            teacher.getAddress().add(ag.getRandomAddress());
            if (random.nextBoolean())
                teacher.getAddress().add(ag.getRandomAddress());

            // teacher.getTelephone().add(e);

            // teacher.getElectronicMail().add(e);

            teacher.setOldEthnicity(OldEthnicityType.AMERICAN_INDIAN_OR_ALASKAN_NATIVE);

            RaceType rt = new RaceType();
            teacher.setRace(rt);
            teacher.getRace().getRacialCategory().add(RaceItemType.WHITE);

            teacher.setHighestLevelOfEducationCompleted(LevelOfEducationType.BACHELOR_S);

            teacher.setYearsOfPriorProfessionalExperience(new Integer(20));

            teacher.setYearsOfPriorTeachingExperience(new Integer(15));

            teacher.setHispanicLatinoEthnicity(rondom.nextBoolean());

            teacher.setTeacherUniqueStateId(teacherId);

            teacher.setHighlyQualifiedTeacher(rondom.nextBoolean());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teacher;
    }

    public static Teacher getFastTeacher(String teacherId) {
        Random random = new Random();
        Teacher teacher = new Teacher();
        teacher.setStaffUniqueStateId(teacherId);
        Name name = new Name();
        name.setFirstName("Vincent");
        name.setLastSurname("Valentine");
        teacher.setName(name);
        teacher.setSex(SexType.MALE);
        teacher.setHispanicLatinoEthnicity(false);
        RaceType raceType = new RaceType();
        raceType.getRacialCategory().add(RaceItemType.WHITE);
        raceType.getRacialCategory().add(RaceItemType.ASIAN);
        teacher.setRace(raceType);
        teacher.setHighestLevelOfEducationCompleted(LevelOfEducationType.NO_DEGREE);
        return teacher;
    }
}
