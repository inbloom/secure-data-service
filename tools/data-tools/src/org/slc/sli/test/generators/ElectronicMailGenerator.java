package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.ElectronicMail;
import org.slc.sli.test.edfi.entities.ElectronicMailAddressType;

public class ElectronicMailGenerator {
    private Random random = new Random();
    private ElectronicMailAddressType[] emailAddressTypes = ElectronicMailAddressType.values();

    public ElectronicMail generate(String user) {
        ElectronicMail email = new ElectronicMail();
        email.setEmailAddress(user + "@gmail.com");
        email.setEmailAddressType(emailAddressTypes[random.nextInt(emailAddressTypes.length)]);
        return email;
    }
}
