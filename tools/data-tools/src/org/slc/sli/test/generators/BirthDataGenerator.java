package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.BirthData;

public class BirthDataGenerator {

    Random random = new Random();

    public BirthData generate(int age) {
        BirthData bd = new BirthData();
        bd.setBirthDate("2011-03-04");

        return bd;
    }

    public static BirthData generateFastBirthData() {

        BirthData bd = new BirthData();
        bd.setBirthDate("2011-03-04");

        return bd;
    }

}
