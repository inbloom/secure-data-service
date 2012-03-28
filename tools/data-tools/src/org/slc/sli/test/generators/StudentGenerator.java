package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class StudentGenerator {
    NameGenerator nameGenerator;
    Random random = new Random();

    public StudentGenerator(String state) {
        try {
            nameGenerator = new NameGenerator();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Student generate(String studentId) {
        Student s = new Student();
        s.setStudentUniqueStateId(studentId);

        s.setName(nameGenerator.getName());

        s.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        s.setHispanicLatinoEthnicity(random.nextBoolean());

        RaceType rt = new RaceType();
        s.setRace(rt);
        s.getRace().getRacialCategory().add(RaceItemType.WHITE);

        BirthDataGenerator bdg = new BirthDataGenerator();
        s.setBirthData(bdg.generate(6+random.nextInt(11)));

        return s;
    }
}
