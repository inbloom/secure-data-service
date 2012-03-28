package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.BirthData;

public class BirthDataGenerator {
    Random random = new Random();

    public BirthData generate(int age) {
        BirthData bd = new BirthData();
        Calendar rightNow = Calendar.getInstance();

        Calendar bday = Calendar.getInstance();

        bday.set(rightNow.get(Calendar.YEAR)-age, random.nextInt(12), random.nextInt(29));

        bd.setBirthDate(bday);

        return bd;
    }
}
